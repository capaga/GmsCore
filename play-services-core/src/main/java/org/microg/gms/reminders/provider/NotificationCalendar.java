package org.microg.gms.reminders.provider;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.reminders.internal.IRemindersListener;

import java.util.List;
import java.util.Objects;

public class NotificationCalendar implements SQLiteTransactionListener {
    private static final String TAG = "NotificationCalendar";
    private Boolean started = false;
    private IRemindersListener calendar;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            calendar = IRemindersListener.Stub.asInterface(service);
            if (started) {
                ContentResolver contentResolver = mContext.getContentResolver();
                DataHolder dataHolder = new DataHolder(Objects.requireNonNull(contentResolver.query(RemindersProvider.REMINDERS_EVENTS_URI,
                        null,
                        "(due_date_millis IS NOT NULL) AND (due_date_absolute_time_ms IS NULL)",
                        null,
                        null)), 0, null);
                try {
                    calendar.createTasks(dataHolder);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final Context mContext;

    private static final String BIND_LISTENER = "com.google.android.gms.reminders.BIND_LISTENER";

    public NotificationCalendar(Context context) {
        this.mContext = context;
    }

    @Override
    public void onBegin() {
        this.started = true;
    }

    @Override
    public void onCommit() {
        List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentServices(new Intent(BIND_LISTENER), PackageManager.GET_SERVICES);
        if (!resolveInfoList.isEmpty()) {
            for (ResolveInfo resolveInfo : resolveInfoList) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (serviceInfo != null) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
                    intent.setAction(BIND_LISTENER);
                    mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }

    @Override
    public void onRollback() {

    }


    public void sendTaskNotification(long reminderId) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(RemindersProvider.REMINDERS_EVENTS_URI,
                null,
                "reminders._id=?",
                new String[]{String.valueOf(reminderId)},
                null);
        if (cursor == null) {
            return;
        }
        DataHolder dataHolder = new DataHolder(cursor, 0, null);
        if (dataHolder.getCount() == 0) {
            return;
        }
        int archived = dataHolder.getInteger("archived", 0, 0);
        int deleted = dataHolder.getInteger("deleted", 0, 0);
        int due_date_all_day = dataHolder.getInteger("due_date_all_day", 0, 0);
        if (archived == 1 || deleted == 1 || due_date_all_day == 1) {
            return;
        }
        if (calendar == null) {
            List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentServices(new Intent(BIND_LISTENER), PackageManager.GET_SERVICES);
            if (!resolveInfoList.isEmpty()) {
                for (ResolveInfo resolveInfo : resolveInfoList) {
                    ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                    if (serviceInfo != null) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
                        intent.setAction(BIND_LISTENER);
                        mContext.bindService(intent, new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                calendar = IRemindersListener.Stub.asInterface(service);
                                try {
                                    calendar.sendTaskNotifications(dataHolder);
                                } catch (RemoteException e) {
                                    Log.e(TAG, "sendTaskNotifications: error", e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        }, Context.BIND_AUTO_CREATE);
                    }
                }
            }
        } else {
            try {
                calendar.sendTaskNotifications(dataHolder);
            } catch (RemoteException e) {
                Log.e(TAG, "sendTaskNotifications: error", e);
            }
        }

    }
}
