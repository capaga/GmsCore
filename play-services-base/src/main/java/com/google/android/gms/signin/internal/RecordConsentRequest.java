/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package com.google.android.gms.signin.internal;

import android.accounts.Account;

import com.google.android.gms.common.api.Scope;

import org.microg.safeparcel.AutoSafeParcelable;

public class RecordConsentRequest extends AutoSafeParcelable {
    private static final int VERSION_CODE = 1;
    @Field(2)
    private Account mAccount;
    @Field(3)
    private Scope[] mScopesToConsent;
    @Field(4)
    private String mServerClientId;
    @Field(1)
    private int mVersionCode = VERSION_CODE;

    public RecordConsentRequest() {

    }

    public Account getAccount() {
        return this.mAccount;
    }

    public Scope[] getScopesToConsent() {
        return this.mScopesToConsent;
    }

    public String getServerClientId() {
        return this.mServerClientId;
    }

    public static final Creator<RecordConsentRequest> CREATOR = new AutoCreator<>(RecordConsentRequest.class);
}
