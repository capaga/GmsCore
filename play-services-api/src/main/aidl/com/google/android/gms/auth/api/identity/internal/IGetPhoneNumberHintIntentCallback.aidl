package com.google.android.gms.auth.api.identity.internal;

import com.google.android.gms.common.api.Status;

interface IGetPhoneNumberHintIntentCallback{
    void onResult(in Status status, in PendingIntent pendingIntent);
}