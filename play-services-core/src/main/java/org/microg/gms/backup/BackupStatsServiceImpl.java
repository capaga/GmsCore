package org.microg.gms.backup;

import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.backup.ApplicationBackupStats;
import com.google.android.gms.backup.BackupStatsRequestConfig;
import com.google.android.gms.backup.IBackupStatsService;

public class BackupStatsServiceImpl extends IBackupStatsService.Stub {
    private static final String TAG = "BackupStatsServiceImpl";
    @Override
    public void a() throws RemoteException {
        Log.w(TAG, "Method 'a' not yet implement." );
    }

    @Override
    public void setApplicationBackupStats(ApplicationBackupStats arg1) throws RemoteException {
        Log.w(TAG, "Method 'setApplicationBackupStats' not yet implement.");
    }

    @Override
    public void g(String arg1, long arg2) throws RemoteException {
        Log.w(TAG, "Method 'g' Not yet implement.");
    }

    @Override
    public ApplicationBackupStats[] getApplicationBackupStats(BackupStatsRequestConfig config) throws RemoteException {
        Log.w(TAG, "getApplicationBackupStats: called, config=>" + config);
        return new ApplicationBackupStats[0];
    }
}
