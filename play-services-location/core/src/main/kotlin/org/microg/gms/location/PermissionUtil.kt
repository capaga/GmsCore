package org.microg.gms.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.microg.gms.location.core.R
import org.microg.gms.location.ui.GetPermissionsActivity

class PermissionUtil {
    companion object {
        const val DELETE_NOTICE = "org.microg.cust.DELETE_NOTICE"
        const val PERMISSION_PREFERENCE = "permission_preferences"
        const val PERMISSION_SHOW_TIMES = "permission_show_times"
        const val PERMISSION_REJECT_SHOW = "permission_reject_show"
        var notificationIsShow = false
        private const val TAG = "PermissionUtil"
        fun cancelLocationPermissionNotify(context: Context) {
            if (Build.VERSION.SDK_INT < 23)
                return
            val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1175)
            notificationIsShow = false
        }
        fun sendLocationPermissionNotify(context: Context) {
            Log.d(TAG, "sendLocationPermissionNotify: ")
            val sharedPreferences = context.getSharedPreferences(PERMISSION_PREFERENCE, MODE_PRIVATE)
            if (sharedPreferences.getBoolean(PERMISSION_REJECT_SHOW, false)) {
                cancelLocationPermissionNotify(context)
                Log.d(TAG, "user select reject show permission")
                return
            }
            if (Build.VERSION.SDK_INT < 23)
                return

            val permissions = mutableListOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= 29) permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

            if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                Log.d(TAG, "location permission is all granted")
                cancelLocationPermissionNotify(context)
                return
            }
            if (notificationIsShow) {
                Log.d(TAG, "location permission notify is show")
                return
            }
            val channelId = "1112"
            val name = context.getString(R.string.notice_permission_channel_name)
            createNotificationChannel(context, channelId, name, NotificationManager.IMPORTANCE_HIGH)
            val intent = Intent(context, GetPermissionsActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val deleteIntent = Intent(DELETE_NOTICE)
            deleteIntent.setPackage(context.packageName)

            val text = context.getString(R.string.notice_permission_content, context.getString(R.string.gms_app_name))
            val notification: Notification = NotificationCompat.Builder(context, channelId)
                    .setContentTitle(context.getString(R.string.notice_permission_title, context.getString(R.string.gms_app_name)).trim { it <= ' ' })
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_app_new)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setDeleteIntent(PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_IMMUTABLE))
                    .build()
            val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1175, notification)
            notificationIsShow = true
        }

        private fun createNotificationChannel(context: Context, id: String, name: String, importance: Int) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                val temp = notificationManager.getNotificationChannel(id)
                if (temp != null) {
                    return
                }
                val channel = NotificationChannel(id, name, importance)
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(100, 200, 300)
                channel.enableLights(true)
                channel.setSound(null, null)
                channel.description = name
                channel.canShowBadge()
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}