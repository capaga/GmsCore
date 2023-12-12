package com.google.android.gms.signin.internal;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.signin.internal.AuthAccountResult;
import com.google.android.gms.signin.internal.SignInResponse;

interface ISignInCallbacks {
    void onAuthAccountComplete(in ConnectionResult connectionResult, in AuthAccountResult authAccountResult) = 2;
    void onGetCurrentAccountComplete(in Status status, in GoogleSignInAccount googleSignInAccount) = 6;
    void onRecordConsentComplete(in Status status) = 5;
    void onSaveAccountToSessionStoreComplete(in Status status) = 3;
    void onSignIn(in SignInResponse response) = 7;
}