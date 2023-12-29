package com.google.android.gms.backup.internal;

import com.google.android.gms.common.api.Status;

interface IRestoreNowCallbacks {
    void onPostResult(in Status status);
}