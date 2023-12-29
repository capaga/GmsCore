package com.google.android.gms.backup;

import com.google.android.gms.backup.BackupStatsRequestConfig;
import com.google.android.gms.backup.ApplicationBackupStats;

interface IBackupStatsService {
//    void a();
//    void b(ApplicationBackupStats arg1);
//    void g(String arg1, long arg2);
//    ApplicationBackupStats[] h(BackupStatsRequestConfig arg1);

//    ApplicationBackupStats[] getApplicationBackupStats(in BackupStatsRequestConfig config, String[] packNameList) = 0;
    ApplicationBackupStats[] getApplicationBackupStats(in BackupStatsRequestConfig config) = 1;
    void setApplicationBackupStats(in ApplicationBackupStats arg1) = 2;
    void g(String arg1, long arg2) = 5;
    void a() = 6;
}