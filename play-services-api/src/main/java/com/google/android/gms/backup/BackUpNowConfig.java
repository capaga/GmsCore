package com.google.android.gms.backup;

import org.microg.safeparcel.AutoSafeParcelable;

public class BackUpNowConfig extends AutoSafeParcelable {
    @Field(1)
    public boolean a;
    @Field(2)
    public boolean b;
    @Field(3)
    public boolean c;
    @Field(4)
    public boolean d;
    @Field(5)
    public boolean e;
    @Field(6)
    public boolean f;
    @Field(7)
    public boolean g;
    @Field(8)
    public boolean h;
    public static final Creator<BackUpNowConfig> CREATOR = new AutoCreator<>(BackUpNowConfig.class);
}
