package org.microg.gms.reminders.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class RemindersSyncService extends Service {
    private RemindersSyncAdapter adapter;

    @Override
    public void onCreate() {
        synchronized(this) {
            if(this.adapter == null) {
                this.adapter = new RemindersSyncAdapter(this.getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}
