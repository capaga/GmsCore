package com.google.android.gms.mdisync;

import org.microg.safeparcel.AutoSafeParcelable;

public class CallerInfo extends AutoSafeParcelable {
    @Field(1)
    public String a;
    @Field(2)
    public long b;

    public static final Creator<CallerInfo> CREATOR = new AutoCreator<>(CallerInfo.class);
}
