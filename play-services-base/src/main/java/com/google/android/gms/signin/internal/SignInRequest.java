/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package com.google.android.gms.signin.internal;

import com.google.android.gms.common.internal.ResolveAccountRequest;
import org.microg.gms.common.Hide;
import org.microg.safeparcel.AutoSafeParcelable;

@Hide
public class SignInRequest extends AutoSafeParcelable {
    private static final int VERSION_CODE = 1;
    @Field(2)
    private ResolveAccountRequest mResolveAccountRequest;
    @Field(1)
    private int mVersionCode = VERSION_CODE;

    public static final Creator<SignInRequest> CREATOR = new AutoCreator<>(SignInRequest.class);
}
