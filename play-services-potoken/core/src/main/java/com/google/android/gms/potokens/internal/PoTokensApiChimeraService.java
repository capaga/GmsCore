package com.google.android.gms.potokens.internal;


import android.os.RemoteException;

import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.ConnectionInfo;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class PoTokensApiChimeraService extends BaseService {


    public static String tag = "PoTokensApi";
    private static final Feature[] FEATURES = new Feature[]{new Feature("PO_TOKENS",1)};

    public PoTokensApiChimeraService() {
        super(tag, GmsService.POTOKENS);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.features = FEATURES;
        callback.onPostInitCompleteWithConnectionInfo(CommonStatusCodes.SUCCESS,new PoTokensApiChimeraServiceImpl(getApplicationContext(),request.packageName),connectionInfo);
    }


}

