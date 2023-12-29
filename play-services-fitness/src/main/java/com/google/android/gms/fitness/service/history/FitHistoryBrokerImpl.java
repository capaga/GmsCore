package com.google.android.gms.fitness.service.history;

import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.fitness.internal.IGoogleFitHistoryApi;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataInsertRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.GetSyncInfoRequest;

public class FitHistoryBrokerImpl extends IGoogleFitHistoryApi.Stub {
    private static final String TAG = FitHistoryBrokerImpl.class.getSimpleName();
    @Override
    public void a(DataDeleteRequest dataDeleteRequest) throws RemoteException {
        Log.d(TAG, "a: ");
    }

    @Override
    public void b(GetSyncInfoRequest getSyncInfoRequest) throws RemoteException {
        Log.d(TAG, "b: " + getSyncInfoRequest);
    }

    @Override
    public void g(DataInsertRequest dataInsertRequest) throws RemoteException {
        Log.d(TAG, "g: ");
    }

    @Override
    public void h(DataReadRequest dataReadRequest) throws RemoteException {
        Log.d(TAG, "h: ");
    }
}
