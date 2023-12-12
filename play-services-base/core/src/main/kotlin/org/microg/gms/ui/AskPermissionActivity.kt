package org.microg.gms.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager


private const val REQUEST_CODE_PERMISSION = 101

const val EXTRA_MESSENGER = "messenger"
const val EXTRA_PERMISSIONS = "permissions"
const val EXTRA_GRANT_RESULTS = "grantResults"
const val EXTRA_BROADCAST_ACTION = "permissionBroadcastAction"
const val PERMISSION_RESULT_EXTRA = "permissionResult"

class AskPermissionActivity : AppCompatActivity() {
    private val TAG = "AskPermissionActivity"
    private var permissionBroadcastAction: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS)
        permissionBroadcastAction = intent.getStringExtra(EXTRA_BROADCAST_ACTION)
        Log.d(TAG, "onCreate: ")
        if (permissions.isNullOrEmpty()) {
            sendReply(RESULT_CANCELED)
            sendPermissionsResult(intArrayOf(PackageManager.PERMISSION_GRANTED))
            finish()
        } else {
            Log.d(TAG, "Requesting permissions: ${permissions.toList()}")
            if (SDK_INT < 23) {
                sendReply(RESULT_CANCELED)
                finish()
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION)
            }
        }
    }

    private fun sendReply(code: Int = RESULT_OK, extras: Bundle = Bundle.EMPTY) {
        intent.getParcelableExtra<Messenger>(EXTRA_MESSENGER)?.let {
            it.send(Message.obtain().apply {
                what = code
                data = extras
            })
        }
        setResult(code, Intent().apply { putExtras(extras) })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult: " + grantResults.joinToString(", "))
        if (requestCode == REQUEST_CODE_PERMISSION) {
            sendPermissionsResult(grantResults)
            sendReply(extras = bundleOf(EXTRA_GRANT_RESULTS to grantResults))
            finish()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun sendPermissionsResult(grantResults: IntArray) {
        Log.d(TAG, "sendPermissionsResult: $permissionBroadcastAction")
        if (!permissionBroadcastAction.isNullOrEmpty()) {
            Log.d(TAG, "sendPermissionsResult: ")
            val intent = Intent(permissionBroadcastAction)
            intent.putExtra(PERMISSION_RESULT_EXTRA, grantResults)
            sendBroadcast(intent)
        }
    }
}