package com.google.android.gms.mdisync.internal;

import com.google.android.gms.mdisync.CallerInfo;
import com.google.android.gms.mdisync.internal.SyncRequest;
import com.google.android.gms.mdisync.internal.TeleportingSyncRequest;
import com.google.android.gms.mdisync.internal.IMdiSyncCallbacks;

interface IMdiSyncService{
    void a(IMdiSyncCallbacks callback, in SyncRequest syncRequest, in CallerInfo callerInfo) = 0;
    void b(IMdiSyncCallbacks callback, in TeleportingSyncRequest teleportingSyncRequest, in CallerInfo callerInfo) = 1;
}