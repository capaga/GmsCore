//package org.microg.gms.games.signin;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.gson.Gson;
//
//public class GoogleAccountCache {
//    private static final String PREF_NAME = "google_account_cache";
//    private static final String KEY_ACCOUNT_PREFIX = "last_google_sign_in_account-";
//    private static volatile GoogleAccountCache instance = null;
//    private final SharedPreferences sharedPreferences;
//
//    private GoogleAccountCache(Context context) {
//        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//    }
//
//    public static GoogleAccountCache getInstance(Context context) {
//        if (instance == null) {
//            synchronized (GoogleAccountCache.class) {
//                if (instance == null) {
//                    instance = new GoogleAccountCache(context);
//                }
//            }
//        }
//        return instance;
//    }
//
//    public void save(String pkgName, GoogleSignInAccount googleSignInAccount) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        if (googleSignInAccount != null) {
//            try {
//                String accountJson = new Gson().toJson(googleSignInAccount);
//                editor.putString(KEY_ACCOUNT_PREFIX + pkgName, accountJson);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        editor.apply();
//    }
//
//    public GoogleSignInAccount get(String pkgName) {
//        String accountJson = sharedPreferences.getString(KEY_ACCOUNT_PREFIX + pkgName, null);
//        if (accountJson != null) {
//            try {
//                return new Gson().fromJson(accountJson,GoogleSignInAccount.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//}
