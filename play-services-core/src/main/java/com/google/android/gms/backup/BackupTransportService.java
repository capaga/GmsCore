package com.google.android.gms.backup;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.backup.component.BackupSettingsActivity;
import org.microg.gms.backup.BackupTransportImpl;
import org.microg.gms.backup.TransportParameters;

import java.io.File;


/**
 *  本类路径需与系统白名单中的路径对应，不能移动位置
 */
public class BackupTransportService extends Service {
    private static final String TAG = "BackupTransportService";
    private static final String BACKING_UP_TAG = "BACKING_UP";
    private static BackupTransportImpl sTransport;
    @Override
    public void onCreate() {
        createBackingUpTag();
        if (sTransport == null) {
            TransportParameters parameters = new TransportParameters(new Handler(), getContentResolver());
            sTransport = new BackupTransportImpl(this.getApplicationContext(), parameters);
        }
        sTransport.getParameters().start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTransport.getBinder();
    }

    @Override
    public void onDestroy() {
        deleteBackingUpTag();
    }

    private void createBackingUpTag(){
        synchronized (BackupTransportService.class){
            File file = new File(getFilesDir(), BACKING_UP_TAG);
            try {
                file.createNewFile();
            } catch (Exception e) {
                Log.e(TAG, "createBackingUpTag: ", e);
            }
        }
    }

    private void deleteBackingUpTag(){
        synchronized (BackupSettingsActivity.class){
            File file = new File(getFilesDir(), BACKING_UP_TAG);
            try {
                file.delete();
            } catch (Exception e) {
                Log.e(TAG, "deleteBackingUpTag: ", e);
            }
        }
    }

    public static boolean isBackingUp(Context context){
        synchronized (BackupTransportService.class){
            File file = new File(context.getFilesDir(), BACKING_UP_TAG);
            return file.exists();
        }
    }
}
