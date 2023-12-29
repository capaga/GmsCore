package com.google.android.gms.mdisync.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.mdisync.internal.SyncResult;

interface IMdiSyncCallbacks {
    void onPostResult(in Status status, in SyncResult syncResult);
}