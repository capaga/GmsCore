package com.google.android.gms.auth.api.identity;

import android.app.PendingIntent;

import org.microg.safeparcel.AutoSafeParcelable;

public class BeginSignInResult extends AutoSafeParcelable {
    @Field(1)
    public PendingIntent pendingIntent;

    public static final Creator<BeginSignInResult> CREATOR = new AutoCreator<>(BeginSignInResult.class);
}
