package org.microg.gms.backup;

import android.content.Context;
import android.content.SharedPreferences;

public class BackupSettingsSharedPrefs {
    private static BackupSettingsSharedPrefs instance;
    private SharedPreferences prefs;

    public static final String KEY_MMS_BACKUP_STATE = "MMSBackupState";
    public static final String KEY_USE_MOBILE_DATA = "useMobileData";

    public BackupSettingsSharedPrefs(Context context) {
        prefs = context.getSharedPreferences("BackupSettings", Context.MODE_MULTI_PROCESS);
    }

//    public static synchronized BackupSettingsSharedPrefs getInstance(Context context) {
//        if (instance == null) {
//            instance = new BackupSettingsSharedPrefs(context);
//        }
//        return instance;
//    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
//        synchronized (this) {
//            editor.apply();
//        }
    }

    public void setValue(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
//        synchronized (this) {
//            editor.apply();
//        }
    }

    public void setValue(String key, long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
//        synchronized (this) {
//            editor.apply();
//        }
    }

    public long getLong(String key){
        synchronized (this) {
            return prefs.getLong(key, 0);
        }
    }

    public boolean getBoolean(String key) {
        synchronized (this) {
            return prefs.getBoolean(key, false);
        }
    }

    public String getString(String key) {
        synchronized (this) {
            return prefs.getString(key, "");
        }
    }
}
