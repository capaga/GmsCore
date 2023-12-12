package com.google.android.gms.auth.api.identity.internal;

import com.google.android.gms.common.api.Status;

interface IGetSignInIntentCallback {
    void onResult(in Status status,in PendingIntent pendingIntent);
}