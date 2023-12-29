package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import com.google.android.gms.fitness.request.DataDeleteRequest;

import org.microg.safeparcel.AutoSafeParcelable;

public class Session extends AutoSafeParcelable {
    public static final Parcelable.Creator<Session> CREATOR = new AutoSafeParcelable.AutoCreator<>(Session.class);
}
