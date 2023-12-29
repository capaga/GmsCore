package com.google.android.libraries.photos.backup.api;


import com.google.android.libraries.photos.backup.api.AutoBackupState;

interface IPhotosBackup {
    AutoBackupState getAutoBackupState() = 0;
    // boolean f(String arg1) = 1;
    void disableAutoBackup() = 2;
    PendingIntent getBackupPreference() = 3;
}
