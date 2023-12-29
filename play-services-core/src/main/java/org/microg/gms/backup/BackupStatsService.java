package org.microg.gms.backup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackupStatsService extends Service {
    private static final String TAG = "BackupStatsService";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new BackupStatsServiceImpl().asBinder();
    }
}
