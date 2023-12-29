package com.google.android.gms.audit.service;

import android.os.RemoteException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class AuditApiService extends BaseService {
    private static final String TAG = "AuditApiService";

    public AuditApiService() {
        super(TAG, GmsService.AUDIT);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        callback.onPostInitComplete(ConnectionResult.SUCCESS, new AuditApiServiceImpl().asBinder(), null);
    }
}
