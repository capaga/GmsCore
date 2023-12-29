package org.microg.gms.backup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackupAccountManagerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new BackupAccountManagerSvcImpl(this).asBinder();
    }
}
