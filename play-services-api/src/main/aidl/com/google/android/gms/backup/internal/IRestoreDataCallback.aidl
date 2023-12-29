package com.google.android.gms.backup.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.backup.RestoreData;

interface IRestoreDataCallback {
    void onPostResult(in Status status, in RestoreData restoreData);
}