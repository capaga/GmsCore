package org.microg.gms.location

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import org.microg.gms.common.Constants

class LocationUtil {

    companion object {
        const val ACTION_REPORT_LOCATION = "org.microg.gms.location.network.ACTION_REPORT_LOCATION"
        const val ACTION_NETWORK_LOCATION_SERVICE = "org.microg.gms.location.network.ACTION_SERVICE"
        const val EXTRA_PENDING_INTENT = "pending_intent"
        const val EXTRA_ENABLE = "enable"
        const val EXTRA_INTERVAL_MILLIS = "interval"
        const val EXTRA_FORCE_NOW = "force_now"
        const val EXTRA_LOW_POWER = "low_power"
        const val EXTRA_WORK_SOURCE = "work_source"
        const val EXTRA_BYPASS = "bypass"
        const val EXTRA_LOCATION = "location"
        const val LOCATION_TIME_CLIFF_MS = 30000L
        const val DEBOUNCE_DELAY_MS = 5000L
        const val MAX_WIFI_SCAN_CACHE_AGE = 1000L * 60 * 60 * 24 // 1 day

        @JvmStatic
        private var netWorkLocationServiceAvailableFlag: Boolean = true

        @JvmStatic
        private var initFlag: Boolean = false

        @JvmStatic
        fun isNetWorkLocationServiceAvailable(context: Context?): Boolean {
            if (!initFlag) {
                try {
                    val serviceIntent = Intent().apply {
                        action = ACTION_NETWORK_LOCATION_SERVICE
                        setPackage(Constants.GMS_PACKAGE_NAME)
                    }
                    val services = context?.packageManager?.queryIntentServices(serviceIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    netWorkLocationServiceAvailableFlag = services?.isNotEmpty() ?: false
                    initFlag = true
                } catch (e: Exception) {
                    Log.w("LocationUtil", e)
                    netWorkLocationServiceAvailableFlag = false
                }
            }
            return netWorkLocationServiceAvailableFlag
        }
    }
}