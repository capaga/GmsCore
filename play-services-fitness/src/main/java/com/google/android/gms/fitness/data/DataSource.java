package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import org.microg.gms.profile.Build;
import org.microg.safeparcel.AutoSafeParcelable;

import java.util.Locale;

public class DataSource extends AutoSafeParcelable {
    public static final Parcelable.Creator<DataSource> CREATOR = new AutoSafeParcelable.AutoCreator<>(DataSource.class);
    public DataType a;
    public int b;
    public Device c;
    public Application d;
    public String e;
    public String f;
    //private static final String g = ctji.RAW.name().toLowerCase(Locale.ROOT);
    private static final String h = Build.DEVICE.toLowerCase(Locale.ROOT);

}
