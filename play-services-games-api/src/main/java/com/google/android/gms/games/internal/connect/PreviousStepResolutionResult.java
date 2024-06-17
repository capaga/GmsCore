package com.google.android.gms.games.internal.connect;

import android.content.Intent;

import org.microg.safeparcel.AutoSafeParcelable;

public class PreviousStepResolutionResult extends AutoSafeParcelable {
    @Field(1)
    public Intent resultData;

    public static final Creator<PreviousStepResolutionResult> CREATOR = new AutoCreator<>(PreviousStepResolutionResult.class);
}
