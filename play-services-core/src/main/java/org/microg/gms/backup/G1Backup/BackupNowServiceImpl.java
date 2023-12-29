package org.microg.gms.backup.G1Backup;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.backup.BackUpNowConfig;
import com.google.android.gms.backup.BackupTransportService;
import com.google.android.gms.backup.IBackUpNowClientCallbacks;
import com.google.android.gms.backup.internal.IBackUpNowCallbacks;
import com.google.android.gms.backup.internal.IBackUpNowService;
import com.google.android.gms.common.api.Status;

import org.microg.gms.backup.BackupManagerWrapper;

public class BackupNowServiceImpl extends IBackUpNowService.Stub {
    private static final String TAG = "BackupNowServiceImpl";
    private final Context context;
    private final BackupManagerWrapper backupManagerWrapper;

    public BackupNowServiceImpl(Context context) {
        this.context = context;
        this.backupManagerWrapper = BackupManagerWrapper.getInstance(this.context);
    }
    @Override
    public void starBackupNowTask(IBackUpNowCallbacks callback, BackUpNowConfig config) throws RemoteException {
        try {
            this.backupManagerWrapper.backupNow();
            boolean backingUp;
            Thread.sleep(3000);
            do {
                backingUp = BackupTransportService.isBackingUp(this.context);
            } while (backingUp);
            callback.setStatus(Status.SUCCESS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putBackupNowTask(String name, IBackUpNowClientCallbacks callback) throws RemoteException {
        Log.w(TAG, "Method 'putBackupNowTask' not yet implement");
        callback.a(0);
    }

    @Override
    public void removeBackupNowTask(String name) throws RemoteException {
        Log.d(TAG, "Method 'removeBackupNowTask' not yet implement");
    }

    @Override
    public boolean hasTaskRunning() throws RemoteException {
        boolean backingUp = BackupTransportService.isBackingUp(this.context);
        Log.d(TAG, "hasTaskRunning: " + backingUp);
        return backingUp;
    }
}
