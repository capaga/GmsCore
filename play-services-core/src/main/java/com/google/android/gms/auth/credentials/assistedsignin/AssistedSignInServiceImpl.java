package com.google.android.gms.auth.credentials.assistedsignin;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.internal.IBeginSignInCallback;
import com.google.android.gms.auth.api.identity.internal.IGetPhoneNumberHintIntentCallback;
import com.google.android.gms.auth.api.identity.internal.IGetSignInIntentCallback;
import com.google.android.gms.auth.api.identity.internal.ISignInService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.IStatusCallback;

import org.microg.gms.common.Constants;
import org.microg.gms.common.Utils;

public class AssistedSignInServiceImpl extends ISignInService.Stub {
    private static final String TAG = "AssistedSignInSvcImpl";
    private static final String ACTION_SIGN_IN = "com.google.android.gms.auth.api.credentials.GOOGLE_SIGN_IN";
    public static final String GET_SIGN_IN_INTENT_REQUEST = "get_sign_in_intent_request";
    public static final String CLIENT_PACKAGE_NAME = "client_package_name";
    private final Context mContext;
    private final String clientPackageName;

    public AssistedSignInServiceImpl(Context context, String clientPackageName) {
        this.mContext = context;
        this.clientPackageName = clientPackageName;
    }

    @Override
    public void beginSignIn(IBeginSignInCallback callback, BeginSignInRequest request) throws RemoteException {
        Log.w(TAG, "method 'beginSignIn' not fully implemented, return status is CANCELED");
        callback.onResult(Status.CANCELED, null);
    }

    @Override
    public void signOut(IStatusCallback callback, String requestTag) throws RemoteException {
        Log.d(TAG, "method signOut called, requestTag=" + requestTag);
        callback.onResult(Status.SUCCESS);
    }

    @Override
    public void getSignInIntent(IGetSignInIntentCallback callback, GetSignInIntentRequest request) throws RemoteException {
        Intent intent = new Intent(ACTION_SIGN_IN);
        intent.setPackage(Constants.GMS_PACKAGE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(CLIENT_PACKAGE_NAME, this.clientPackageName);
        bundle.putByteArray(GET_SIGN_IN_INTENT_REQUEST, Utils.safeParcelableInstanceToBytesArray(request));
        intent.putExtras(bundle);
        int flags = PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent activity = PendingIntent.getActivity(this.mContext, 0, intent, flags);
        callback.onResult(Status.SUCCESS, activity);
    }

    @Override
    public void getPhoneNumberHintIntent(IGetPhoneNumberHintIntentCallback callback, GetPhoneNumberHintIntentRequest request) throws RemoteException {
        Log.w(TAG, "method 'getPhoneNumberHintIntent' not fully implemented, return status is CANCELED.");
        callback.onResult(Status.CANCELED, null);
    }
}



