package com.google.android.gms.backup.internal;

import com.google.android.gms.backup.internal.IBackUpNowCallbacks;
import com.google.android.gms.backup.IBackUpMmsClientCallbacks;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.backup.BackupOptInSettings;

interface IG1BackupService {
    void setMMSBackupState(boolean enable, String accountName) = 0;
    boolean isBackupEnabled() = 2;
    boolean isSMSBackupEnable() = 3;
    boolean isMMSBackupEnabled() = 4;
    void starMMSBackup(IBackUpNowCallbacks callback) = 5;
    boolean checkAvailableRestoreToken() = 6;
    void setMMSUesMobileData(boolean useMobileData) = 7;
    void setUseMobileData(boolean useMobileData) = 8;
    boolean isUseMobileDataForMms() = 9;
    boolean isUseMobileData() = 10;
    void setBackupEnable(IStatusCallback callback, boolean z, String accountName, in BackupOptInSettings backupOptInSettings) = 11;
    void putBackUpMmsClientCallbacks(String name, IBackUpMmsClientCallbacks callbacks) = 12;
    void removeBackUpMmsClientCallbacks(String name) = 13;
    int getBackupDeviceState() = 14;
    long getLastFullBackupPassTimeMs() = 15;
}
