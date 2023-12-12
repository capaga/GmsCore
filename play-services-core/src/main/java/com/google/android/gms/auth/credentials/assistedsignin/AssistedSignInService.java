package com.google.android.gms.auth.credentials.assistedsignin;

import android.os.RemoteException;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.internal.ConnectionInfo;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class AssistedSignInService extends BaseService {
    private static final String TAG = "AssistedSignInSvc";

    public AssistedSignInService() {
        super(TAG, GmsService.IDENTITY_SIGN_IN);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.features = new Feature[]{
                new Feature("auth_api_credentials_begin_sign_in", 8L),
                new Feature("auth_api_credentials_sign_out", 2L),
                new Feature("auth_api_credentials_authorize", 1L),
                new Feature("auth_api_credentials_revoke_access", 1L),
                new Feature("auth_api_credentials_save_password", 4L),
                new Feature("auth_api_credentials_get_sign_in_intent", 6L),
                new Feature("auth_api_credentials_save_account_linking_token", 3L),
                new Feature("auth_api_credentials_get_phone_number_hint_intent", 3L),
        };
        callback.onPostInitCompleteWithConnectionInfo(ConnectionResult.SUCCESS,
                new AssistedSignInServiceImpl(this, request.packageName).asBinder(),
                connectionInfo);
    }
}
