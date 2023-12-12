/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package com.google.android.gms.signin.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;

import org.microg.gms.common.Hide;
import org.microg.safeparcel.AutoSafeParcelable;

@Hide
public class SignInResponse extends AutoSafeParcelable {
    private static final int VERSION_CODE = 1;
    @Field(2)
    private ConnectionResult mConnectionResult;
    @Field(3)
    private ResolveAccountResponse mResolveAccountResponse;
    @Field(1)
    private int mVersionCode = VERSION_CODE;

    public SignInResponse() {
        this(new ConnectionResult(8, null), null);
    }

    public SignInResponse(ConnectionResult result, ResolveAccountResponse resolveAccountResponse) {
        this(VERSION_CODE, result, resolveAccountResponse);
    }

    public SignInResponse(int versionCode, ConnectionResult connectionResult, ResolveAccountResponse resolveAccountResponse) {
        this.mVersionCode = versionCode;
        this.mConnectionResult = connectionResult;
        this.mResolveAccountResponse = resolveAccountResponse;
    }

    public ConnectionResult getConnectionResult() {
        return this.mConnectionResult;
    }

    public ResolveAccountResponse getResolveAccountResponse() {
        return this.mResolveAccountResponse;
    }

    public static final Creator<SignInResponse> CREATOR = new AutoCreator<>(SignInResponse.class);
}
