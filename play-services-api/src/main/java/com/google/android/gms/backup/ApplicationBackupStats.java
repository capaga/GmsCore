package com.google.android.gms.backup;

import org.microg.safeparcel.AutoSafeParcelable;

public class ApplicationBackupStats extends AutoSafeParcelable {
    @Field(1)
    public String packageName;
    @Field(2)
    public int b;
    @Field(3)
    public int c;
    @Field(4)
    public long timeStamp;
    @Field(5)
    public long e;
    public static final Creator<ApplicationBackupStats> CREATOR = new AutoCreator<>(ApplicationBackupStats.class);
}
