package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import org.microg.safeparcel.AutoSafeParcelable;

public class Application extends AutoSafeParcelable {
    public static final Parcelable.Creator<Application> CREATOR = new AutoSafeParcelable.AutoCreator<>(Application.class);
    public String b;
    public static final Application a = new Application("com.google.android.gms");
    public Application(String str) {
        this.b = str;
    }

}
