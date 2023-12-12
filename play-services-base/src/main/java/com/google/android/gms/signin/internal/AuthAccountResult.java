package com.google.android.gms.signin.internal;

import android.content.Intent;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

import org.microg.safeparcel.AutoSafeParcelable;

public class AuthAccountResult extends AutoSafeParcelable implements Result {
    private static final int VERSION_CODE = 2;
    @Field(2)
    private int mConnectionResultCode;
    @Field(3)
    private Intent mRawAuthResultionIntent;
    @Field(1)
    private int mVersionCode;

    AuthAccountResult(int versionCode, int connectionResultCode, Intent rawAuthResultionIntent) {
        this.mVersionCode = versionCode;
        this.mConnectionResultCode = connectionResultCode;
        this.mRawAuthResultionIntent = rawAuthResultionIntent;
    }

    public AuthAccountResult() {
        this(0, null);
    }

    public AuthAccountResult(int connectionResultCode, Intent rawAuthResolutionIntent) {
        this(VERSION_CODE, connectionResultCode, rawAuthResolutionIntent);
    }

    public int getConnectionResultCode() {
        return this.mConnectionResultCode;
    }

    public Intent getRawAuthResolutionIntent() {
        return this.mRawAuthResultionIntent;
    }

    public Status getStatus() {
        if (this.mConnectionResultCode == 0) {
            return Status.SUCCESS;
        }
        return Status.CANCELED;
    }

    public static final Creator<AuthAccountResult> CREATOR = new AutoCreator<>(AuthAccountResult.class);
}