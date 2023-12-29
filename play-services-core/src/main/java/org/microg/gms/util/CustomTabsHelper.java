package org.microg.gms.util;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

import org.microg.gms.auth.AuthManager;
import org.microg.gms.auth.AuthResponse;

import java.net.URLEncoder;
import java.util.Locale;

public class CustomTabsHelper {
    private static final String TAG = CustomTabsHelper.class.getSimpleName();

    public static void startWebUrl(Activity context, String accountName, String callingPackage, String url) {
        Thread thread = new Thread(() -> {
            try {
                context.finish();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                if (accountName == null) {
                    Log.d(TAG, "the name is null");
                    customTabsIntent.launchUrl(context, Uri.parse(CustomTabsHelper.addLanguageParam(url)));
                    return;
                }
                AuthResponse authResponse = getAuthResponse(context, accountName, callingPackage, url);
                if (authResponse != null) {
                    customTabsIntent.launchUrl(context, Uri.parse(authResponse.auth));
                }
            } catch (Exception e) {
                Log.w(TAG, "fail to get auth", e);
            }
        });
        thread.start();
    }

    private static AuthResponse getAuthResponse(Activity context, String accountName, String callingPackage, String url) {
        try {
            String service = "weblogin:continue=" + URLEncoder.encode(CustomTabsHelper.addLanguageParam(url), "utf-8");
            AuthManager authManager = new AuthManager(context, accountName, callingPackage, service, null);
            AuthResponse authResponse = authManager.requestAuth(true, true, true);
            return authResponse;
        } catch (Exception e) {
            Log.d(TAG, "fail to get weblogin info.");
            return null;
        }
    }

    private static String addLanguageParam(String url) {
        String language = Locale.getDefault().getLanguage();
        if (language != null && !language.isEmpty()) {
            return url + "?hl=" + Locale.getDefault().getLanguage();
        } else {
            return language;
        }
    }
}
