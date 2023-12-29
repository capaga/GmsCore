package com.google.android.gms.backup;

import org.microg.safeparcel.AutoSafeParcelable;

public class BackupOptInSettings extends AutoSafeParcelable {
    @Field(1)
    public boolean userFullDataBackupAware;
    @Field(2)
    public boolean enableSMSBackup;
    @Field(3)
    public boolean enableCallLogBackup;
    @Field(4)
    public boolean backupEncryptionOptInDisplayed;

    public BackupOptInSettings(boolean userFullDataBackupAware, boolean enableSMSBackup, boolean enableCallLogBackup, boolean backupEncryptionOptInDisplayed) {
        this.userFullDataBackupAware = userFullDataBackupAware;
        this.enableSMSBackup = enableSMSBackup;
        this.enableCallLogBackup = enableCallLogBackup;
        this.backupEncryptionOptInDisplayed = backupEncryptionOptInDisplayed;
    }
    public static final Creator<BackupOptInSettings> CREATOR = new AutoCreator<>(BackupOptInSettings.class);
}
