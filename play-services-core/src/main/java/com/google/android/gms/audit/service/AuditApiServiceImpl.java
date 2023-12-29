package com.google.android.gms.audit.service;


import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.audit.LogAuditRecordsRequest;
import com.google.android.gms.audit.internal.IAuditService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.IStatusCallback;

public class AuditApiServiceImpl extends IAuditService.Stub {
    private static final String TAG = "AuditApiServiceImpl";

    @Override
    public void logAuditRecords(LogAuditRecordsRequest logAuditRecordsRequest, IStatusCallback callback) throws RemoteException {
        Log.w(TAG, "method 'logAuditRecords' not fully implemented, only return Status.SUCCESS");
        callback.onResult(Status.SUCCESS);
    }
}
