package org.microg.gms.games.signin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.games.core.R;

import org.microg.common.beans.Cookie;

import java.util.List;

public class ConsentSignInActivity extends AppCompatActivity {


    private static final String CONSENT_URL = "consentUrl";

    private static final String KEY_COOKIE = "cookie-";
    private final String TAG = ConsentSignInActivity.class.getSimpleName();
    private ProgressBar progressBar;

    public static void toConsentSignInActivity(Context context, String consentUrl, List<Cookie> cookies) {
        try {
            Intent intent = new Intent(context, ConsentSignInActivity.class);
            intent.putExtra(CONSENT_URL, consentUrl);
            for (int i = 0; i < cookies.size(); i++) {
                Cookie cookie = cookies.get(i);
                intent.putExtra(KEY_COOKIE + i, String.format("%s=%s;", cookie.cookieName, cookie.cookieValue));
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.w("ConsentSignInActivity", "toConsentSignInActivity: ", e);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_sign_in);
        progressBar = findViewById(R.id.progressBar);
        WebView webview = findViewById(R.id.consent_sign);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new OAuthConsent(), "OAuthConsent");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String consentUrl = intent.getStringExtra(CONSENT_URL);
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean value) {
                        if (value == true) {
                            Log.d(TAG, "removeAllCookies: " + true);
                            setCookiesAndLoadUrl(webview, consentUrl, intent, cookieManager);
                        } else {
                            Log.d(TAG, "removeAllCookies: " + false);
                            finish();
                        }
                    }
                });
            } else {
                setCookiesAndLoadUrl(webview, consentUrl, intent, cookieManager);
            }
        } else {
            finish();
        }
    }

    private void setCookiesAndLoadUrl(WebView webview, String consentUrl, Intent intent, CookieManager cookieManager){
        Bundle extras = intent.getExtras();
        if (extras != null && extras.size() > 0) {
            for (int i = 0; i < extras.size(); i++) {
                String cookie = (String) extras.get(KEY_COOKIE + i);
                if (cookie != null){
                    cookieManager.setCookie(consentUrl, cookie);
                    Log.d(TAG, "set cookie: " + cookie);
                }
            }
            webview.loadUrl(consentUrl);
        } else {
            finish();
        }
    }

    private class OAuthConsent {
        @JavascriptInterface
        public void cancel() {
            Log.d(TAG, "cancel: ");
            finish();
        }

        @JavascriptInterface
        public void getModuleVersion() {
            Log.d(TAG, "getModuleVersion: ");
        }

        @JavascriptInterface
        public void setConsentResult(String s) {
            Log.d(TAG, "setConsentResult: " + s);
            Intent intent = new Intent().setAction(AuthSdkManager.CONSENT_RESULT).putExtra("consentResult", s);
            sendBroadcast(intent);
            finish();
        }

        @JavascriptInterface
        public void showView() {
            Log.d(TAG, "showView: ");
        }
    }
}