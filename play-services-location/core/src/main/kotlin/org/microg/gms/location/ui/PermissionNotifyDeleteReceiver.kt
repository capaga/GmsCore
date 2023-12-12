package org.microg.gms.location.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.microg.gms.location.PermissionUtil

class PermissionNotifyDeleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (PermissionUtil.DELETE_NOTICE == intent.action) {
            PermissionUtil.notificationIsShow = false
        }
    }
}