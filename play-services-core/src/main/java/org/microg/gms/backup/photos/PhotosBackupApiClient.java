package org.microg.gms.backup.photos;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.libraries.photos.backup.api.AutoBackupState;
import com.google.android.libraries.photos.backup.api.IPhotosBackup;

public class PhotosBackupApiClient {
    private static final String TAG = "PhotosBackupClient";
    private static IPhotosBackup mPhotosBackupClient;
    private PendingIntent mBackupPreference;
    private static PhotosBackupApiClient mPhotosBackupApiClient = null;

    private PhotosBackupApiClient(Context context) {
        Intent intent = new Intent()
                .setPackage("com.google.android.apps.photos")
                .setAction("com.google.android.apps.photos.backup.apiservice.PHOTOS_BACKUP_SERVICE");
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPhotosBackupClient = IPhotosBackup.Stub.asInterface(service);
                mBackupPreference = getBackupPreference();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPhotosBackupClient = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public static PhotosBackupApiClient getInstance(Context context) {
        synchronized (PhotosBackupApiClient.class) {
            if (mPhotosBackupApiClient == null || mPhotosBackupClient == null) {
                mPhotosBackupApiClient = new PhotosBackupApiClient(context);
            }
            return mPhotosBackupApiClient;
        }
    }

    /**
     * 获取Photos备份设置
     *
     * @return AutoBackupState or null
     */
    public AutoBackupState getAutoBackupState() {
        try {
            if (mPhotosBackupClient != null) {
                AutoBackupState autoBackupState = mPhotosBackupClient.getAutoBackupState();
                if (autoBackupState != null) {
                    Log.d(TAG, "getAutoBackupState: autoBackupState=> " + autoBackupState);
                    return autoBackupState;
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getAutoBackupState: error", e);
        }
        return null;
    }

    /**
     * 关闭Photos自动备份
     */
    public void disableAutoBackup() {
        try {
            if (mPhotosBackupClient != null) {
                mPhotosBackupClient.disableAutoBackup();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "disablePhotosAutoBackup error: " + e.getMessage());
        }
    }

    /**
     * 获取跳转 Photos Apk备份设置页面的 PendingIntent
     */
    private PendingIntent getBackupPreference() {
        Log.d(TAG, "getPhotosBackupPreference: enter");
        if (mPhotosBackupClient != null) {
            try {
                PendingIntent backupPreference = mPhotosBackupClient.getBackupPreference();
                if (backupPreference != null) {
                    return backupPreference;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "disablePhotosAutoBackup error: " + e.getMessage());
            }
        }
        return null;
    }

    public PendingIntent getBackupPreferenceSettings() {
        return this.mBackupPreference;
    }
}

