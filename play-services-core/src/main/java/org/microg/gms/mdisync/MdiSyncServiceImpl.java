package org.microg.gms.mdisync;

import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.mdisync.CallerInfo;
import com.google.android.gms.mdisync.internal.IMdiSyncCallbacks;
import com.google.android.gms.mdisync.internal.IMdiSyncService;
import com.google.android.gms.mdisync.internal.SyncRequest;
import com.google.android.gms.mdisync.internal.SyncResult;
import com.google.android.gms.mdisync.internal.TeleportingSyncRequest;

public class MdiSyncServiceImpl extends IMdiSyncService.Stub {
    private static final String TAG = "MdiSyncServiceImpl";
    @Override
    public void a(IMdiSyncCallbacks callback, SyncRequest syncRequest, CallerInfo callerInfo) throws RemoteException {
        Log.w(TAG, "Method 'a' not yet implement.");
        callback.onPostResult(Status.CANCELED, new SyncResult());
    }

    @Override
    public void b(IMdiSyncCallbacks callback, TeleportingSyncRequest teleportingSyncRequest, CallerInfo callerInfo) throws RemoteException {
        Log.w(TAG, "Method 'b' not yet implement.");
        callback.onPostResult(Status.CANCELED, new SyncResult());
    }
}
