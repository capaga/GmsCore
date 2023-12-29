package org.microg.gms.auth.signin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.google.android.gms.R
import org.microg.gms.auth.AuthConstants

class ConsentSignInActivity : Activity() {
    private val TAG = ConsentSignInActivity::class.java.simpleName

    private var progressBar: ProgressBar? = null
    private var sendResult = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent_sign_in)
        progressBar = findViewById(R.id.progressBar)
        val webview = findViewById<WebView>(R.id.consent_sign)
        webview.settings.javaScriptEnabled = true
        webview.addJavascriptInterface(OAuthConsent(), "OAuthConsent")
        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
                progressBar?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar?.visibility = View.GONE
            }
        }
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return false
            }
        }
        val intent = intent
        if (intent != null) {
            val consentUrl = intent.getStringExtra(AuthConstants.CONSENT_URL)
            val cookieManager = CookieManager.getInstance()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies { _ ->
                    setCookiesAndLoadUrl(webview, consentUrl, intent, cookieManager)
                }
            } else {
                setCookiesAndLoadUrl(webview, consentUrl, intent, cookieManager)
            }
        } else {
            finish()
        }
    }

    private fun setCookiesAndLoadUrl(
        webview: WebView, consentUrl: String?, intent: Intent, cookieManager: CookieManager
    ) {
        val extras = intent.extras
        if (extras != null && extras.size() > 0) {
            for (i in 0 until extras.size()) {
                val cookie = extras[AuthConstants.CONSENT_KEY_COOKIE + i] as String?
                if (cookie != null) {
                    cookieManager.setCookie(consentUrl, cookie)
                    Log.d(TAG, "set cookie: $cookie")
                }
            }
            webview.loadUrl(consentUrl!!)
        } else {
            finish()
        }
    }

    private fun sendReplay(code: Int, result: String?) {
        val messenger = intent.getParcelableExtra<Messenger>(AuthConstants.CONSENT_MESSENGER)
        if (messenger == null) {
            Log.d(TAG, "sendReplay messenger is null ")
            return
        }
        try {
            Log.d(TAG, "sendReplay result -> $result code -> $code")
            val obtain = Message.obtain()
            obtain.what = code
            obtain.obj = result
            messenger.send(obtain)
            sendResult = true
        } catch (e: Exception) {
            Log.d(TAG, "sendReplay Exception -> " + e.message)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "ConsentSignInActivity onDestroy ")
        if (!sendResult) {
            sendReplay(RESULT_OK, AuthConstants.CONSENT_USER_EXIT)
        }
        super.onDestroy()
    }

    private inner class OAuthConsent {
        @JavascriptInterface
        fun cancel() {
            Log.d(TAG, "OAuthConsent cancel: sendReplay ")
            sendReplay(RESULT_OK, AuthConstants.CONSENT_USER_EXIT)
            finish()
        }

        @get:JavascriptInterface
        val moduleVersion: Unit
            get() {
                Log.d(TAG, "getModuleVersion: ")
            }

        @JavascriptInterface
        fun setConsentResult(s: String) {
            Log.d(TAG, "OAuthConsent sendReplay consentResult -> $s")
            sendReplay(RESULT_OK, s)
            finish()
        }

        @JavascriptInterface
        fun showView() {
            Log.d(TAG, "OAuthConsent showView: ")
        }
    }
}