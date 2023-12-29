package org.microg.gms.backup;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import java.lang.reflect.Method;


/**
 * 通过反射的方式调用BackupManager类下的相关方法
 */
public class BackupManagerWrapper {
    private final String TAG = "BackupManagerWrapper";
    private final BackupManager backupManager;
    private final Context context;

    private static BackupManagerWrapper mBackupMangerWrapper = null;

    private BackupManagerWrapper(Context context) {
        this.context = context;
        this.backupManager = new BackupManager(context);
    }
    public static BackupManagerWrapper getInstance(Context context){
        synchronized (BackupManagerWrapper.class){
            if (mBackupMangerWrapper == null){
                mBackupMangerWrapper = new BackupManagerWrapper(context);
            }
            return mBackupMangerWrapper;
        }
    }

    public boolean isBackupEnabled(){
        if (!checkPermission()){
            Log.d(TAG, "getBackupIsEnabled: no permission");
            return false;
        }
        try {
            Method method = this.backupManager.getClass().getDeclaredMethod("isBackupEnabled");
            method.setAccessible(true);
            boolean isEnabled = (boolean) method.invoke(this.backupManager);
            Log.d(TAG, "getBackupIsEnabled: isEnabled: " + isEnabled);
            return isEnabled;
        } catch (Exception e) {
            Log.e(TAG, "isBackupEnabled: error", e);
        }
        return false;
    }

    public boolean checkPermission(){
        return this.context.checkPermission("android.permission.BACKUP",
                android.os.Process.myPid(),
                android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    public void setBackupEnabled(boolean enable){
        if (!checkPermission()){
            Log.d(TAG, "getBackupIsEnabled: no permission");
            return;
        }
        try {
            Method method = this.backupManager.getClass().getDeclaredMethod("setBackupEnabled", boolean.class);
            method.setAccessible(true);
            Log.d(TAG, "setBackupEnabled: setBackupEnable: " + enable);
            method.invoke(this.backupManager, enable);
        } catch (Exception e) {
            Log.e(TAG, "setBackupEnabled: error", e);
        }
    }

    public String getCurrentTransport(){
        if (!checkPermission()){
            Log.d(TAG, "getBackupIsEnabled: no permission");
            return null;
        }
        try {
            Method method = this.backupManager.getClass().getDeclaredMethod("getCurrentTransport");
            method.setAccessible(true);
            String transportName = (String) method.invoke(this.backupManager);
            Log.d(TAG, "getCurrentTransportComponent: " + transportName);
            return transportName;
        } catch (Exception e) {
            Log.e(TAG, "getCurrentTransport: error", e);
        }
        return null;
    }

    public void selectBackupTransport(String transport){
        if (!checkPermission()){
            Log.d(TAG, "getBackupIsEnabled: no permission");
            return;
        }
        try {
            Method method = this.backupManager.getClass().getDeclaredMethod("selectBackupTransport", String.class);
            method.setAccessible(true);
            Log.d(TAG, "selectBackupTransport: " + transport);
            method.invoke(this.backupManager, transport);
        } catch (Exception e){
            Log.e(TAG, "selectBackupTransport: error", e);
        }
    }

    public void setGmsBackupTransportSelected(){
        if (!checkPermission()){
            Log.d(TAG, "getBackupIsEnabled: no permission");
            return;
        }
        String currentTransport = getCurrentTransport();
        Log.d(TAG, "setGmsBackupTransportEnable: currentTransport: " + currentTransport);
        if (!currentTransport.equals(BackupTransportImpl.TRANSPORT_COMPONENT_NAME)) {
            selectBackupTransport(BackupTransportImpl.TRANSPORT_COMPONENT_NAME);
        }
    }

    public void backupNow(){
        if (!checkPermission()){
            Log.d(TAG, "getBackupIsEnabled: no permission");
            return;
        }
        try {
            Method method = this.backupManager.getClass().getDeclaredMethod("backupNow");
            method.setAccessible(true);
            method.invoke(this.backupManager);
        } catch (Exception e) {
            Log.e(TAG, "backupNow: error", e);
        }
    }
}
