package com.google.android.gms.auth.api.identity.internal;

import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.common.api.Status;

interface IBeginSignInCallback {
    void onResult(in Status status, in BeginSignInResult beginSignInResult);
}