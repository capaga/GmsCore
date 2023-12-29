package com.google.android.gms.backup;

import org.microg.safeparcel.AutoSafeParcelable;

public class BackupStatsRequestConfig extends AutoSafeParcelable {
    @Field(1)
    public boolean a;
    @Field(2)
    public boolean b;
    public static final Creator<BackupStatsRequestConfig> CREATOR = new AutoCreator<>(BackupStatsRequestConfig.class);
}
