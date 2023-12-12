package org.microg.gms.location.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.microg.gms.common.Constants
import org.microg.gms.location.PermissionUtil

class PermissionToSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", Constants.GMS_PACKAGE_NAME, null) // 替换成您要跳转的应用程序的包名
        intent.data = uri
        startActivityForResult(intent, 123)
        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("PermissionToSettings", "onActivityResult: ")
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= 29) permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        if (permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            Log.d("PermissionToSettings", "location permission is all granted")
            PermissionUtil.cancelLocationPermissionNotify(this)
        }
        finish()
    }
}