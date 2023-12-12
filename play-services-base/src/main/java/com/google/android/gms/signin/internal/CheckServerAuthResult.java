/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package com.google.android.gms.signin.internal;

import com.google.android.gms.common.api.Scope;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class CheckServerAuthResult extends AutoSafeParcelable {
    private static final int VERSION_CODE = 1;
    @Field(3)
    private List<Scope> mAdditionalScopes;
    @Field(2)
    private boolean mNewAuthCodeRequired;
    @Field(1)
    private int mVersionCode;

    public static final Creator<CheckServerAuthResult> CREATOR = new AutoCreator<>(CheckServerAuthResult.class);
}
