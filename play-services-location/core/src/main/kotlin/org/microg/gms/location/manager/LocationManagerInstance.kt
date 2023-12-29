/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.manager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.SystemClock
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.common.api.internal.IStatusCallback
import com.google.android.gms.common.internal.ICancelToken
import com.google.android.gms.location.ActivityRecognitionRequest
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.ILocationCallback
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationAvailabilityRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStates
import com.google.android.gms.location.SleepSegmentRequest
import com.google.android.gms.location.internal.ClientIdentity
import com.google.android.gms.location.internal.DeviceOrientationRequestUpdateData
import com.google.android.gms.location.internal.DeviceOrientationRequestUpdateData.REMOVE_UPDATES
import com.google.android.gms.location.internal.DeviceOrientationRequestUpdateData.REQUEST_UPDATES
import com.google.android.gms.location.internal.FusedLocationProviderResult
import com.google.android.gms.location.internal.IFusedLocationProviderCallback
import com.google.android.gms.location.internal.IGeofencerCallbacks
import com.google.android.gms.location.internal.ISettingsCallbacks
import com.google.android.gms.location.internal.LocationReceiver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import org.microg.gms.location.LocationUtil
import org.microg.gms.location.PermissionUtil
import org.microg.gms.utils.warnOnTransactionIssues

class LocationManagerInstance(
        private val context: Context,
        private val locationManager: LocationManager,
        private val packageName: String,
        private val lifecycle: Lifecycle
) :
        AbstractLocationManagerInstance(), LifecycleOwner {

    // region Geofences

    override fun addGeofences(geofencingRequest: GeofencingRequest?, pendingIntent: PendingIntent?, callbacks: IGeofencerCallbacks?) {
        Log.d(TAG, "Not yet implemented: addGeofences by ${getClientIdentity().packageName}")
    }

    override fun removeGeofencesByIntent(pendingIntent: PendingIntent?, callbacks: IGeofencerCallbacks?, packageName: String?) {
        Log.d(TAG, "Not yet implemented: removeGeofencesByIntent by ${getClientIdentity().packageName}")
    }

    override fun removeGeofencesById(geofenceRequestIds: Array<out String>?, callbacks: IGeofencerCallbacks?, packageName: String?) {
        Log.d(TAG, "Not yet implemented: removeGeofencesById by ${getClientIdentity().packageName}")
    }

    override fun removeAllGeofences(callbacks: IGeofencerCallbacks?, packageName: String?) {
        Log.d(TAG, "Not yet implemented: removeAllGeofences by ${getClientIdentity().packageName}")
    }

    // endregion

    // region Activity

    override fun getLastActivity(packageName: String?): ActivityRecognitionResult {
        Log.d(TAG, "Not yet implemented: getLastActivity by ${getClientIdentity().packageName}")
        return ActivityRecognitionResult(listOf(DetectedActivity(DetectedActivity.UNKNOWN, 0)), System.currentTimeMillis(), SystemClock.elapsedRealtime())
    }

    override fun requestActivityTransitionUpdates(request: ActivityTransitionRequest?, pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        Log.d(TAG, "Not yet implemented: requestActivityTransitionUpdates by ${getClientIdentity().packageName}")
        callback?.onResult(Status.SUCCESS)
    }

    override fun removeActivityTransitionUpdates(pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        Log.d(TAG, "Not yet implemented: removeActivityTransitionUpdates by ${getClientIdentity().packageName}")
        callback?.onResult(Status.SUCCESS)
    }

    override fun requestActivityUpdatesWithCallback(request: ActivityRecognitionRequest?, pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        Log.d(TAG, "Not yet implemented: requestActivityUpdatesWithCallback by ${getClientIdentity().packageName}")
        callback?.onResult(Status.SUCCESS)
    }

    override fun removeActivityUpdates(callbackIntent: PendingIntent?) {
        Log.d(TAG, "Not yet implemented: removeActivityUpdates by ${getClientIdentity().packageName}")
    }

    // endregion

    // region Sleep

    override fun removeSleepSegmentUpdates(pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        Log.d(TAG, "Not yet implemented: removeSleepSegmentUpdates by ${getClientIdentity().packageName}")
        callback?.onResult(Status.SUCCESS)
    }

    override fun requestSleepSegmentUpdates(pendingIntent: PendingIntent?, request: SleepSegmentRequest?, callback: IStatusCallback?) {
        Log.d(TAG, "Not yet implemented: requestSleepSegmentUpdates by ${getClientIdentity().packageName}")
        callback?.onResult(Status.SUCCESS)
    }

    // endregion

    // region Location

    override fun flushLocations(callback: IFusedLocationProviderCallback?) {
        Log.d(TAG, "flushLocations by ${getClientIdentity().packageName}")
        checkHasAnyLocationPermission()
        Log.d(TAG, "Not yet implemented: flushLocations")
    }

    override fun getLocationAvailabilityWithReceiver(request: LocationAvailabilityRequest, receiver: LocationReceiver) {
        Log.d(TAG, "getLocationAvailabilityWithReceiver by ${getClientIdentity().packageName}")
        val callback = receiver.availabilityStatusCallback
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (checkHasAnyLocationPermission()) {
                    callback.onLocationAvailabilityStatus(Status.SUCCESS, locationManager.getLocationAvailability(clientIdentity, request))
                }
            } catch (e: Exception) {
                try {
                    callback.onLocationAvailabilityStatus(Status(CommonStatusCodes.ERROR, e.message), LocationAvailability.UNAVAILABLE)
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun getCurrentLocationWithReceiver(request: CurrentLocationRequest, receiver: LocationReceiver): ICancelToken {
        Log.d(TAG, "getCurrentLocationWithReceiver by ${getClientIdentity().packageName}")
        lifecycleScope.launchWhenStarted {
            checkHasAnyLocationPermission()
        }
        var returned = false
        val callback = receiver.statusCallback
        val clientIdentity = getClientIdentity()
        val binderIdentity = Binder()
        val job = lifecycleScope.launchWhenStarted {
            try {
                val scope = this
                val callbackForRequest = object : ILocationCallback.Stub() {
                    override fun onLocationResult(result: LocationResult?) {
                        if (!returned) runCatching { callback.onLocationStatus(Status.SUCCESS, result?.lastLocation) }
                        returned = true
                        scope.cancel()
                    }

                    override fun onLocationAvailability(availability: LocationAvailability?) {
                        // Ignore
                    }

                    override fun cancel() {
                        if (!returned) runCatching { callback.onLocationStatus(Status.SUCCESS, null) }
                        returned = true
                        scope.cancel()
                    }
                }
                val currentLocationRequest = LocationRequest.Builder(request.priority, 1000)
                    .setGranularity(request.granularity)
                    .setMaxUpdateAgeMillis(request.maxUpdateAgeMillis)
                    .setDurationMillis(request.durationMillis)
                    .setPriority(request.priority)
                    .setWorkSource(request.workSource)
                    .setThrottleBehavior(request.throttleBehavior)
                    .build()
                locationManager.addBinderRequest(clientIdentity, binderIdentity, callbackForRequest, currentLocationRequest)
                awaitCancellation()
            } catch (e: CancellationException) {
                // Don't send result. Either this was cancelled from the CancelToken or because a location was retrieved.
                // Both cases send the result themselves.
            } catch (e: Exception) {
                try {
                    if (!returned) callback.onLocationStatus(Status(CommonStatusCodes.ERROR, e.message), null)
                    returned = true
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            } finally {
                runCatching { locationManager.removeBinderRequest(binderIdentity) }
            }
        }
        return object : ICancelToken.Stub() {
            override fun cancel() {
                if (!returned) runCatching { callback.onLocationStatus(Status.CANCELED, null) }
                returned = true
                job.cancel()
            }
        }
    }

    override fun getLastLocationWithReceiver(request: LastLocationRequest, receiver: LocationReceiver) {
        Log.d(TAG, "getLastLocationWithReceiver by ${getClientIdentity().packageName}")
        val callback = receiver.statusCallback
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (checkHasAnyLocationPermission()) {
                    callback.onLocationStatus(Status.SUCCESS, locationManager.getLastLocation(clientIdentity, request))
                }
            } catch (e: Exception) {
                try {
                    Log.d(TAG, "getLastLocationWithReceiver: ")
                    callback.onLocationStatus(Status(CommonStatusCodes.ERROR, e.message), null)
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun requestLocationSettingsDialog(settingsRequest: LocationSettingsRequest?, callback: ISettingsCallbacks?, packageName: String?) {
        Log.d(TAG, "requestLocationSettingsDialog by ${getClientIdentity().packageName}")
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                checkHasAnyLocationPermission()
            } catch (e:Exception) {
                Log.w(TAG, "requestLocationSettingsDialog: ", e)
            }

            val locationManager = context.getSystemService<android.location.LocationManager>()
            val gpsPresent = locationManager?.allProviders?.contains(GPS_PROVIDER) == true
            val networkPresent = locationManager?.allProviders?.contains(NETWORK_PROVIDER) == true || LocationUtil.isNetWorkLocationServiceAvailable(context)
            val gpsUsable = gpsPresent && locationManager?.isProviderEnabled(GPS_PROVIDER) == true &&
                    context.packageManager.checkPermission(ACCESS_FINE_LOCATION, clientIdentity.packageName) == PERMISSION_GRANTED
            val networkUsable = networkPresent && locationManager?.isProviderEnabled(NETWORK_PROVIDER) == true &&
                    context.packageManager.checkPermission(ACCESS_COARSE_LOCATION, clientIdentity.packageName) == PERMISSION_GRANTED
            callback?.onLocationSettingsResult(LocationSettingsResult(LocationSettingsStates(gpsUsable, networkUsable, false, gpsPresent, networkPresent, true), Status.SUCCESS))
        }
    }

    // region Mock locations

    override fun setMockModeWithCallback(mockMode: Boolean, callback: IStatusCallback) {
        Log.d(TAG, "setMockModeWithCallback by ${getClientIdentity().packageName}")
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (checkHasAnyLocationPermission()) {
                    Log.d(TAG, "Not yet implemented: setMockModeWithCallback")
                    callback.onResult(Status.SUCCESS)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed", e)
            }
        }
    }

    override fun setMockLocationWithCallback(mockLocation: Location, callback: IStatusCallback) {
        Log.d(TAG, "setMockLocationWithCallback by ${getClientIdentity().packageName}")
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (checkHasAnyLocationPermission()) {
                    Log.d(TAG, "Not yet implemented: setMockLocationWithCallback")
                    callback.onResult(Status.SUCCESS)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed", e)
            }
        }
    }

    // endregion

    // region Location updates

    override fun registerLocationUpdates(
            oldBinder: IBinder?,
            binder: IBinder,
            callback: ILocationCallback,
            request: LocationRequest,
            statusCallback: IStatusCallback
    ) {
        Log.d(TAG, "registerLocationUpdates (callback) by ${getClientIdentity().packageName}")
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (checkHasAnyLocationPermission()) {
                    if (oldBinder != null) {
                        locationManager.updateBinderRequest(clientIdentity, oldBinder, binder, callback, request)
                    } else {
                        locationManager.addBinderRequest(clientIdentity, binder, callback, request)
                    }
                    statusCallback.onResult(Status.SUCCESS)
                }
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun registerLocationUpdates(pendingIntent: PendingIntent, request: LocationRequest, statusCallback: IStatusCallback) {
        Log.d(TAG, "registerLocationUpdates (intent) by ${getClientIdentity().packageName}")
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (checkHasAnyLocationPermission()) {
                    locationManager.addIntentRequest(clientIdentity, pendingIntent, request)
                    statusCallback.onResult(Status.SUCCESS)
                }
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun unregisterLocationUpdates(binder: IBinder, statusCallback: IStatusCallback) {
        Log.d(TAG, "unregisterLocationUpdates (callback) by ${getClientIdentity().packageName}")
        lifecycleScope.launchWhenStarted {
            try {
                if (!checkHasAnyLocationPermission()) {
                    throw SecurityException("$packageName does not have any of permissions")
                } else {
                    locationManager.removeBinderRequest(binder)
                    statusCallback.onResult(Status.SUCCESS)
                }
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun unregisterLocationUpdates(pendingIntent: PendingIntent, statusCallback: IStatusCallback) {
        Log.d(TAG, "unregisterLocationUpdates (intent) by ${getClientIdentity().packageName}")
        lifecycleScope.launchWhenStarted {
            try {
                locationManager.removeIntentRequest(pendingIntent)
                statusCallback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    // endregion

    // endregion

    // region Device Orientation

    override fun updateDeviceOrientationRequest(request: DeviceOrientationRequestUpdateData) {
        Log.d(TAG, "updateDeviceOrientationRequest by ${getClientIdentity().packageName}")
        val clientIdentity = getClientIdentity()
        val callback = request.fusedLocationProviderCallback
        lifecycleScope.launchWhenStarted {
            try {
                if (!checkHasAnyLocationPermission()) {
                    throw SecurityException("$packageName does not have any of permissions")
                } else {
                    when (request.opCode) {
                        REQUEST_UPDATES -> locationManager.deviceOrientationManager.add(clientIdentity, request.request, request.listener)
                        REMOVE_UPDATES -> locationManager.deviceOrientationManager.remove(clientIdentity, request.listener)
                        else -> throw UnsupportedOperationException("Op code ${request.opCode} not supported")
                    }
                    callback?.onFusedLocationProviderResult(FusedLocationProviderResult.SUCCESS)
                }
            } catch (e: Exception) {
                try {
                    callback?.onFusedLocationProviderResult(FusedLocationProviderResult.create(Status(CommonStatusCodes.ERROR, e.message)))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    // endregion

    private fun getClientIdentity() = ClientIdentity(packageName).apply { uid = getCallingUid(); pid = getCallingPid() }

    private fun checkHasAnyLocationPermission(): Boolean {
        val permissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        if (checkHasAnyPermission(permissions)) {
            return true
        }
        //return false
        throw SecurityException("$packageName does not have any of ${permissions.joinToString(", ")}")
    }

    private fun checkHasAnyPermission(permissions: Array<String>): Boolean {
        PermissionUtil.sendLocationPermissionNotify(context)
        for (permission in permissions) {
            if (context.packageManager.checkPermission(permission, packageName) == PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean =
            warnOnTransactionIssues(code, reply, flags, TAG) { super.onTransact(code, data, reply, flags) }
}