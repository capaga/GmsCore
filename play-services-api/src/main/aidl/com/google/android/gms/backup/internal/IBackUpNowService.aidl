package com.google.android.gms.backup.internal;

import com.google.android.gms.backup.internal.IBackUpNowCallbacks;
import com.google.android.gms.backup.IBackUpNowClientCallbacks;
import com.google.android.gms.backup.BackUpNowConfig;

interface IBackUpNowService {
    void putBackupNowTask(String name, IBackUpNowClientCallbacks callback) = 0;
    void starBackupNowTask(IBackUpNowCallbacks callback, in BackUpNowConfig config) = 1;
    boolean hasTaskRunning() = 2;
    void removeBackupNowTask(String name) = 3;
}