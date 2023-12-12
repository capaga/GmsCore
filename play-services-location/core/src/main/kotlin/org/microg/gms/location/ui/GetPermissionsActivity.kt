package org.microg.gms.location.ui

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import org.microg.gms.common.Constants
import org.microg.gms.location.PermissionUtil
import org.microg.gms.location.PermissionUtil.Companion.PERMISSION_PREFERENCE
import org.microg.gms.location.PermissionUtil.Companion.PERMISSION_REJECT_SHOW
import org.microg.gms.location.PermissionUtil.Companion.PERMISSION_SHOW_TIMES
import org.microg.gms.location.core.R


class GetPermissionsActivity : AppCompatActivity() {

    private val foregroundRequestCode = 5
    private val backgroundRequestCode = 55
    private val TAG = "GetPermissionsActivity"
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var hintView:View

    private lateinit var rationaleTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_show_request_permission_rationale)
        rationaleTextView = findViewById(R.id.rationale_textview)

        if (checkAllPermissions()) {
            PermissionUtil.cancelLocationPermissionNotify(this)
            finish()
        } else if (isGranted(ACCESS_COARSE_LOCATION)
                && isGranted(ACCESS_FINE_LOCATION)
                && !isGranted(ACCESS_BACKGROUND_LOCATION)
                && SDK_INT >= Q) {
            requestBackground()
        } else {
            requestForeground()
        }

        sharedPreferences = getSharedPreferences(PERMISSION_PREFERENCE, MODE_PRIVATE)


        findViewById<View>(R.id.open_setting_tv).setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", Constants.GMS_PACKAGE_NAME, null)
            intent.data = uri
            startActivityForResult(intent, 123)
        }

        findViewById<View>(R.id.decline_remind_tv).setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean(PERMISSION_REJECT_SHOW, true)
            editor.apply()
            finish()
        }

        hintView = findViewById(R.id.hint_sl)

        val hintTitle = getString(R.string.permission_hint_title)
        val builder = SpannableStringBuilder(hintTitle + getString(R.string.permission_hint))
        val span = ForegroundColorSpan(Color.BLACK)
        builder.setSpan(span, 0, hintTitle.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), 0, hintTitle.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val hintContentTv = findViewById<TextView>(R.id.hint_content_tv)
        hintContentTv.text = builder

        val showTimes = sharedPreferences.getInt(PERMISSION_SHOW_TIMES, 0)
        Log.d(TAG, "reject show times:$showTimes")
        if (showTimes >= 1) {
            hintView.visibility = View.VISIBLE
        }
    }

    private fun checkAllPermissions(): Boolean {
        return if (SDK_INT >= Q) {
            isGranted(ACCESS_COARSE_LOCATION)
                    && isGranted(ACCESS_FINE_LOCATION)
                    && isGranted(ACCESS_BACKGROUND_LOCATION)
        } else {
            isGranted(ACCESS_COARSE_LOCATION)
                    && isGranted(ACCESS_FINE_LOCATION)
        }
    }

    private fun isGranted(permission: String): Boolean {
        return checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAndAddPermission(list: ArrayList<String>, permission: String) {
        val result = checkSelfPermission(this, permission)
        Log.i(TAG, "$permission: $result")
        if (result != PackageManager.PERMISSION_GRANTED) {
            list.add(permission)
        }
    }

    private fun requestForeground() {
        rationaleTextView.text = getString(R.string.rationale_foreground_permission, getString(R.string.gms_app_name))
        val permissions = arrayListOf<String>()
        permissions.add("com.huawei.permission.sec.MDM.v2")
        checkAndAddPermission(permissions, ACCESS_COARSE_LOCATION)
        checkAndAddPermission(permissions, ACCESS_FINE_LOCATION)
        if (SDK_INT in Q until Build.VERSION_CODES.R) {
            rationaleTextView.text = getString(R.string.rationale_permission, getString(R.string.gms_app_name))
            checkAndAddPermission(permissions, ACCESS_BACKGROUND_LOCATION)
        }
        requestPermissions(permissions, foregroundRequestCode)
    }

    private fun requestBackground() {
        rationaleTextView.setText(R.string.rationale_background_permission)
        val permissions = arrayListOf<String>()
        permissions.add("com.huawei.permission.sec.MDM.v2")
        if (SDK_INT >= Q) {
            checkAndAddPermission(permissions, ACCESS_BACKGROUND_LOCATION)
        }
        requestPermissions(permissions, backgroundRequestCode)
    }

    private fun requestPermissions(permissions: ArrayList<String>, requestCode: Int) {
        if (permissions.isNotEmpty()) {
            Log.w(TAG, "Request permissions: $permissions")
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
                    requestCode
            )
        } else {
            Log.i(TAG, "All permission granted")
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ")
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        if (SDK_INT >= 29) permissions.add(ACCESS_BACKGROUND_LOCATION)

        if (permissions.all { checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            Log.d(TAG, "location permission is all granted")
            PermissionUtil.cancelLocationPermissionNotify(this)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            foregroundRequestCode -> {
                for (i in permissions.indices) {
                    val p = permissions[i]
                    val grant = grantResults[i]
                    val msg = if (grant == PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"
                    Log.w(TAG, "$p: $grant - $msg")
                }
                requestBackground()
            }
            backgroundRequestCode -> {
                if (isGranted(ACCESS_BACKGROUND_LOCATION)) {
                    PermissionUtil.cancelLocationPermissionNotify(this)
                    setResult(RESULT_OK)
                    finish()
                } else {
                    reject()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                reject()
            }
        }
    }

    private fun reject() {
        val showTimes = sharedPreferences.getInt(PERMISSION_SHOW_TIMES, 0)
        Log.d(TAG, "reject show times:$showTimes")
        if (showTimes >= 1) {
            hintView.visibility = View.VISIBLE
        } else {
            val editor = sharedPreferences.edit()
            editor.putInt(PERMISSION_SHOW_TIMES, showTimes + 1)
            editor.apply()
            setResult(RESULT_CANCELED)
            finish()
        }

    }
}