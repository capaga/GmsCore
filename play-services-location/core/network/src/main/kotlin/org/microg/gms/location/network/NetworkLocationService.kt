/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.network

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Build.VERSION.SDK_INT
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.os.WorkSource
import android.util.Log
import androidx.collection.LruCache
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.microg.gms.location.LocationSettings
import org.microg.gms.location.LocationUtil
import org.microg.gms.location.elapsedMillis
import org.microg.gms.location.formatDuration
import org.microg.gms.location.formatRealtime
import org.microg.gms.location.network.LocationCacheDatabase.Companion.NEGATIVE_CACHE_ENTRY
import org.microg.gms.location.network.cell.CellDetails
import org.microg.gms.location.network.cell.CellDetailsCallback
import org.microg.gms.location.network.cell.CellDetailsSource
import org.microg.gms.location.network.mozilla.MozillaLocationServiceClient
import org.microg.gms.location.network.mozilla.ServiceException
import org.microg.gms.location.network.wifi.*
import org.microg.gms.ui.AskPermissionActivity
import org.microg.gms.ui.EXTRA_BROADCAST_ACTION
import org.microg.gms.ui.EXTRA_PERMISSIONS
import org.microg.gms.ui.PERMISSION_RESULT_EXTRA
import java.io.FileDescriptor
import java.io.PrintWriter
import kotlin.math.min

class NetworkLocationService : LifecycleService(), WifiDetailsCallback, CellDetailsCallback {
    private val NETWORK_LOCATION_PERMISSION_RESULT_ACTION = "network_location_permission_result_action"
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler
    private val activeRequests = HashSet<NetworkLocationRequest>()
    private val highPowerScanRunnable = Runnable { this.scan(false) }
    private val lowPowerScanRunnable = Runnable { this.scan(true) }
    private val wifiDetailsSource by lazy { WifiDetailsSource.create(this, this) }
    private val cellDetailsSource by lazy { CellDetailsSource.create(this, this) }
    private val mozilla by lazy { MozillaLocationServiceClient(this) }
    private val cache by lazy { LocationCacheDatabase(this) }
    private val movingWifiHelper by lazy { MovingWifiHelper(this) }
    private val settings by lazy { LocationSettings(this) }
    private val wifiScanCache = LruCache<String?, Location>(100)

    private var lastHighPowerScanRealtime = 0L
    private var lastLowPowerScanRealtime = 0L
    private var highPowerIntervalMillis = Long.MAX_VALUE
    private var lowPowerIntervalMillis = Long.MAX_VALUE

    private var lastWifiDetailsRealtimeMillis = 0L
    private var lastCellDetailsRealtimeMillis = 0L

    private val locationLock = Any()
    private var lastWifiLocation: Location? = null
    private var lastCellLocation: Location? = null
    private var lastLocation: Location? = null
    private var localPermissionResultReceiver : LocalPermissionResultReceiver? = null

    private val interval: Long
        get() = min(highPowerIntervalMillis, lowPowerIntervalMillis)

    override fun onCreate() {
        super.onCreate()
        handlerThread = HandlerThread(NetworkLocationService::class.java.simpleName)
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        wifiDetailsSource.enable()
        cellDetailsSource.enable()
    }

    @SuppressLint("WrongConstant")
    private fun scan(lowPower: Boolean) {
        Log.d(TAG, "scan: ")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!lowPower) lastHighPowerScanRealtime = SystemClock.elapsedRealtime()
            lastLowPowerScanRealtime = SystemClock.elapsedRealtime()
            val workSource = synchronized(activeRequests) { activeRequests.minByOrNull { it.intervalMillis }?.workSource }
            wifiDetailsSource.startScan(workSource)
            cellDetailsSource.startScan(workSource)
            updateRequests()
        } else {
            if (localPermissionResultReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(NETWORK_LOCATION_PERMISSION_RESULT_ACTION)
                localPermissionResultReceiver = LocalPermissionResultReceiver()
                registerReceiver(localPermissionResultReceiver!!, intentFilter)
            }
            val intent = Intent(this, AskPermissionActivity::class.java)
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            intent.putExtra(EXTRA_PERMISSIONS, permissions)
            intent.putExtra(EXTRA_BROADCAST_ACTION, NETWORK_LOCATION_PERMISSION_RESULT_ACTION)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            Log.w(TAG, "scan Missing permissions:" + Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private inner class LocalPermissionResultReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent == null) {
                Log.e(TAG, "LocalPermissionResultReceiver onReceive intent is null")
                return
            }
            val action = intent.action
            Log.i(TAG, "LocalPermissionResultReceiver onReceive action: $action")
            if (NETWORK_LOCATION_PERMISSION_RESULT_ACTION == action) {
                val result = intent.getIntArrayExtra(PERMISSION_RESULT_EXTRA)
                Log.i(TAG, "LocalPermissionResultReceiver onReceive result: ${result?.joinToString(", ")}")
                var grantedNum = 0
                result?.forEach {
                    if (it == PackageManager.PERMISSION_GRANTED) {
                        grantedNum += 1
                    }
                }
                if (grantedNum == result?.size) {
                    Log.d(TAG, "onReceive permission is granted")
                    updateRequests()
                }
            }
        }
    }

    private fun updateRequests(forceNow: Boolean = false, lowPower: Boolean = true) {
        synchronized(activeRequests) {
            lowPowerIntervalMillis = Long.MAX_VALUE
            highPowerIntervalMillis = Long.MAX_VALUE
            for (request in activeRequests) {
                if (request.lowPower) lowPowerIntervalMillis = min(lowPowerIntervalMillis, request.intervalMillis)
                else highPowerIntervalMillis = min(highPowerIntervalMillis, request.intervalMillis)
            }
        }

        // Low power must be strictly less than high power
        if (highPowerIntervalMillis <= lowPowerIntervalMillis) lowPowerIntervalMillis = Long.MAX_VALUE

        val nextHighPowerRequestIn =
            if (highPowerIntervalMillis == Long.MAX_VALUE) Long.MAX_VALUE else highPowerIntervalMillis - (SystemClock.elapsedRealtime() - lastHighPowerScanRealtime)
        val nextLowPowerRequestIn =
            if (lowPowerIntervalMillis == Long.MAX_VALUE) Long.MAX_VALUE else lowPowerIntervalMillis - (SystemClock.elapsedRealtime() - lastLowPowerScanRealtime)

        handler.removeCallbacks(highPowerScanRunnable)
        handler.removeCallbacks(lowPowerScanRunnable)
        if ((forceNow && !lowPower) || nextHighPowerRequestIn <= 0) {
            Log.d(TAG, "Schedule high-power scan now")
            handler.post(highPowerScanRunnable)
        } else if (forceNow || nextLowPowerRequestIn <= 0) {
            Log.d(TAG, "Schedule low-power scan now")
            handler.post(lowPowerScanRunnable)
        } else {
            // Reschedule next request
            if (nextLowPowerRequestIn < nextHighPowerRequestIn) {
                Log.d(TAG, "Schedule low-power scan in ${nextLowPowerRequestIn}ms")
                handler.postDelayed(lowPowerScanRunnable, nextLowPowerRequestIn)
            } else if (nextHighPowerRequestIn != Long.MAX_VALUE) {
                Log.d(TAG, "Schedule high-power scan in ${nextHighPowerRequestIn}ms")
                handler.postDelayed(highPowerScanRunnable, nextHighPowerRequestIn)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post {
            val pendingIntent = intent?.getParcelableExtra<PendingIntent>(LocationUtil.EXTRA_PENDING_INTENT) ?: return@post
            val enable = intent.getBooleanExtra(LocationUtil.EXTRA_ENABLE, false)
            if (enable) {
                val intervalMillis = intent.getLongExtra(LocationUtil.EXTRA_INTERVAL_MILLIS, -1L)
                if (intervalMillis < 0) return@post
                var forceNow = intent.getBooleanExtra(LocationUtil.EXTRA_FORCE_NOW, false)
                val lowPower = intent.getBooleanExtra(LocationUtil.EXTRA_LOW_POWER, true)
                val bypass = intent.getBooleanExtra(LocationUtil.EXTRA_BYPASS, false)
                val workSource = intent.getParcelableExtra(LocationUtil.EXTRA_WORK_SOURCE) ?: WorkSource()
                synchronized(activeRequests) {
                    if (activeRequests.any { it.pendingIntent == pendingIntent }) {
                        forceNow = false
                        activeRequests.removeAll { it.pendingIntent == pendingIntent }
                    }
                    activeRequests.add(NetworkLocationRequest(pendingIntent, intervalMillis, lowPower, bypass, workSource))
                }
                handler.post { updateRequests(forceNow, lowPower) }
            } else {
                synchronized(activeRequests) {
                    activeRequests.removeAll { it.pendingIntent == pendingIntent }
                }
                handler.post { updateRequests() }
            }
        }
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        handlerThread.stop()
        wifiDetailsSource.disable()
        cellDetailsSource.disable()
        if (localPermissionResultReceiver != null) {
            unregisterReceiver(localPermissionResultReceiver)
        }
        super.onDestroy()
    }

    suspend fun requestLocation(requestableWifis: List<WifiDetails>, currentLocalMovingWifi: WifiDetails?): Location? {
        var candidate: Location? = null
        if (currentLocalMovingWifi != null && settings.wifiMoving) {
            try {
                withTimeout(5000L) {
                    candidate = movingWifiHelper.retrieveMovingLocation(currentLocalMovingWifi)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed retrieving location for current moving wifi ${currentLocalMovingWifi.ssid}", e)
            }
        }
        if ((candidate?.accuracy ?: Float.MAX_VALUE) <= 50f) return candidate
        if (requestableWifis.size >= 3) {
            try {
                candidate = when (val cacheLocation = requestableWifis.hash()?.let { wifiScanCache[it.toHexString()] }
                    ?.takeIf { it.time > System.currentTimeMillis() - LocationUtil.MAX_WIFI_SCAN_CACHE_AGE }) {
                    NEGATIVE_CACHE_ENTRY -> null
                    null -> {
                        if (settings.wifiMls) {
                            val location = mozilla.retrieveMultiWifiLocation(requestableWifis)
                            location.time = System.currentTimeMillis()
                            requestableWifis.hash()?.let { wifiScanCache[it.toHexString()] = location }
                            location
                        } else {
                            null
                        }
                    }

                    else -> cacheLocation
                }?.takeIf { candidate == null || it.accuracy < candidate?.accuracy!! } ?: candidate
            } catch (e: Exception) {
                Log.w(TAG, "Failed retrieving location for ${requestableWifis.size} wifi networks", e)
                if (e is ServiceException && e.error.code == 404 || e is VolleyError && e.networkResponse?.statusCode == 404) {
                    requestableWifis.hash()?.let { wifiScanCache[it.toHexString()] = NEGATIVE_CACHE_ENTRY }
                }
            }
        }
        return candidate
    }

    override fun onWifiDetailsAvailable(wifis: List<WifiDetails>) {
        if (wifis.isEmpty()) return
        val scanResultTimestamp = min(wifis.maxOf { it.timestamp ?: Long.MAX_VALUE }, System.currentTimeMillis())
        val scanResultRealtimeMillis =
            if (SDK_INT >= 17) SystemClock.elapsedRealtime() - (System.currentTimeMillis() - scanResultTimestamp) else scanResultTimestamp
        if (scanResultRealtimeMillis < lastWifiDetailsRealtimeMillis + interval / 2 && lastWifiDetailsRealtimeMillis != 0L) {
            Log.d(TAG, "Ignoring wifi details, similar age as last ($scanResultRealtimeMillis < $lastWifiDetailsRealtimeMillis + $interval / 2)")
            return
        }
        @Suppress("DEPRECATION")
        val currentLocalMovingWifi = getSystemService<WifiManager>()?.connectionInfo
            ?.let { wifiInfo -> wifis.filter { it.macAddress == wifiInfo.bssid && it.isMoving } }
            ?.filter { movingWifiHelper.isLocallyRetrievable(it) }
            ?.singleOrNull()
        val requestableWifis = wifis.filter(WifiDetails::isRequestable)
        if (requestableWifis.size < 3 && currentLocalMovingWifi == null) return
        val previousLastRealtimeMillis = lastWifiDetailsRealtimeMillis
        lastWifiDetailsRealtimeMillis = scanResultRealtimeMillis
        lifecycleScope.launch {
            val location = requestLocation(requestableWifis, currentLocalMovingWifi)
            if (location == null) {
                lastWifiDetailsRealtimeMillis = previousLastRealtimeMillis
                return@launch
            }
            location.time = scanResultTimestamp
            if (SDK_INT >= 17) location.elapsedRealtimeNanos = scanResultRealtimeMillis * 1_000_000L
            synchronized(locationLock) {
                lastWifiLocation = location
            }
            sendLocationUpdate()
        }
    }

    override fun onCellDetailsAvailable(cells: List<CellDetails>) {
        val scanResultTimestamp = min(cells.maxOf { it.timestamp ?: Long.MAX_VALUE }, System.currentTimeMillis())
        val scanResultRealtimeMillis =
            if (SDK_INT >= 17) SystemClock.elapsedRealtime() - (System.currentTimeMillis() - scanResultTimestamp) else scanResultTimestamp
        if (scanResultRealtimeMillis < lastCellDetailsRealtimeMillis + interval / 2) {
            Log.d(TAG, "Ignoring cell details, similar age as last")
            return
        }
        lastCellDetailsRealtimeMillis = scanResultRealtimeMillis
        lifecycleScope.launch {
            val singleCell =
                cells.filter { it.location != NEGATIVE_CACHE_ENTRY }.maxByOrNull { it.timestamp ?: it.signalStrength?.toLong() ?: 0L } ?: return@launch
            val location = singleCell.location ?: try {
                when (val cacheLocation = cache.getCellLocation(singleCell)) {
                    NEGATIVE_CACHE_ENTRY -> null

                    null -> if (settings.cellMls) {
                        mozilla.retrieveSingleCellLocation(singleCell).also {
                            it.time = System.currentTimeMillis()
                            cache.putCellLocation(singleCell, it)
                        }
                    } else {
                        null
                    }

                    else -> cacheLocation
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed retrieving location for $singleCell", e)
                if (e is ServiceException && e.error.code == 404 || e is VolleyError && e.networkResponse?.statusCode == 404) {
                    cache.putCellLocation(singleCell, NEGATIVE_CACHE_ENTRY)
                }
                null
            } ?: return@launch
            location.time = singleCell.timestamp ?: scanResultTimestamp
            if (SDK_INT >= 17) location.elapsedRealtimeNanos =
                singleCell.timestamp?.let { SystemClock.elapsedRealtimeNanos() - (System.currentTimeMillis() - it) * 1_000_000L }
                    ?: (scanResultRealtimeMillis * 1_000_000L)
            synchronized(locationLock) {
                lastCellLocation = location
            }
            sendLocationUpdate()
        }
    }

    private fun sendLocationUpdate(now: Boolean = false) {
        val location = synchronized(locationLock) {
            if (lastCellLocation == null && lastWifiLocation == null) return
            when {
                // Only non-null
                lastCellLocation == null -> lastWifiLocation
                lastWifiLocation == null -> lastCellLocation
                // Consider cliff
                lastCellLocation!!.elapsedMillis > lastWifiLocation!!.elapsedMillis + LocationUtil.LOCATION_TIME_CLIFF_MS -> lastCellLocation
                lastWifiLocation!!.elapsedMillis > lastCellLocation!!.elapsedMillis + LocationUtil.LOCATION_TIME_CLIFF_MS -> lastWifiLocation
                // Wifi out of cell range with higher precision
                lastCellLocation!!.precision > lastWifiLocation!!.precision && lastWifiLocation!!.distanceTo(lastCellLocation!!) > 2 * lastCellLocation!!.accuracy -> lastCellLocation
                else -> lastWifiLocation
            }
        } ?: return
        if (location == lastLocation) return
        if (lastLocation == lastWifiLocation && location == lastCellLocation && !now) {
            handler.postDelayed({
                sendLocationUpdate(true)
            }, LocationUtil.DEBOUNCE_DELAY_MS)
            return
        }
        lastLocation = location
        synchronized(activeRequests) {
            for (request in activeRequests.toList()) {
                try {
                    request.send(this@NetworkLocationService, location)
                } catch (e: Exception) {
                    Log.w(TAG, "Pending intent error $request")
                    activeRequests.remove(request)
                }
            }
        }
    }

    override fun dump(fd: FileDescriptor?, writer: PrintWriter, args: Array<out String>?) {
        writer.println("Last scan elapsed realtime: high-power: ${lastHighPowerScanRealtime.formatRealtime()}, low-power: ${lastLowPowerScanRealtime.formatRealtime()}")
        writer.println("Last scan result time: wifi: ${lastWifiDetailsRealtimeMillis.formatRealtime()}, cells: ${lastCellDetailsRealtimeMillis.formatRealtime()}")
        writer.println("Interval: high-power: ${highPowerIntervalMillis.formatDuration()}, low-power: ${lowPowerIntervalMillis.formatDuration()}")
        writer.println("Last wifi location: $lastWifiLocation${if (lastWifiLocation == lastLocation) " (active)" else ""}")
        writer.println("Last cell location: $lastCellLocation${if (lastCellLocation == lastLocation) " (active)" else ""}")
        writer.println("Settings: Wi-Fi MLS=${settings.wifiMls} moving=${settings.wifiMoving} Cell MLS=${settings.cellMls}")
        writer.println("Wifi scan cache size=${wifiScanCache.size()} hits=${wifiScanCache.hitCount()} miss=${wifiScanCache.missCount()} puts=${wifiScanCache.putCount()} evicts=${wifiScanCache.evictionCount()}")
        synchronized(activeRequests) {
            if (activeRequests.isNotEmpty()) {
                writer.println("Active requests:")
                for (request in activeRequests) {
                    writer.println("- ${request.workSource} ${request.intervalMillis.formatDuration()} (low power: ${request.lowPower}, bypass: ${request.bypass})")
                }
            }
        }
    }
}

private operator fun <K, V> LruCache<K, V>.set(key: K, value: V) {
    put(key, value)
}
