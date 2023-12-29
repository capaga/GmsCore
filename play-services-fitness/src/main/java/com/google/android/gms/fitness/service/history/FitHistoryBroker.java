package com.google.android.gms.fitness.service.history;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class FitHistoryBroker extends BaseService {
    private static final String TAG = FitHistoryBroker.class.getSimpleName();
    public FitHistoryBroker() {
        super(TAG, GmsService.FITNESS_HISTORY);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        FitHistoryBrokerImpl fitHistoryBrokerImpl = new FitHistoryBrokerImpl();
        callback.onPostInitComplete(CommonStatusCodes.SUCCESS, fitHistoryBrokerImpl.asBinder(), new Bundle());
    }
}
