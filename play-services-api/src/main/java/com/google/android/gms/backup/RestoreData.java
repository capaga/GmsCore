package com.google.android.gms.backup;

import org.microg.safeparcel.AutoSafeParcelable;

public class RestoreData extends AutoSafeParcelable {
    @Field(1)
    public long a;
    @Field(2)
    public long b;

    public static final Creator<RestoreData> CREATOR = new AutoCreator<>(RestoreData.class);
}
