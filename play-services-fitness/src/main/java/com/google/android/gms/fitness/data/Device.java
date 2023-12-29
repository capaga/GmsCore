package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import org.microg.safeparcel.AutoSafeParcelable;

public class Device extends AutoSafeParcelable {
    public static final Parcelable.Creator<Device> CREATOR = new AutoSafeParcelable.AutoCreator<>(Device.class);
    public String a;
    public String b;
    public String c;
    public int d;
    public int e;

}
