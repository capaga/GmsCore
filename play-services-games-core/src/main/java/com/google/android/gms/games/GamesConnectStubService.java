package com.google.android.gms.games;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;
import com.google.android.gms.games.internal.connect.GamesSignInRequest;
import com.google.android.gms.games.internal.connect.GamesSignInResult;
import com.google.android.gms.games.internal.connect.IGamesConnectCallbacks;
import com.google.android.gms.games.internal.connect.IGamesConnectService;

import org.microg.gms.BaseService;
import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.GmsService;
import org.microg.gms.games.signin.GamesSignInActivity;
import org.microg.gms.games.signin.GamesSignInManager;

import java.util.ArrayList;
import java.util.List;

public class GamesConnectStubService extends BaseService {
    private static final String TAG = GamesConnectStubService.class.getSimpleName();
    private String srcPackageName;
    private Account account;

    public GamesConnectStubService() {
        super(TAG, GmsService.GAMES);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        Log.d(TAG, "GamesConnectChimeraService handleServiceRequest: " + request);
        srcPackageName = request.packageName;
        account = GamesSignInManager.checkAccount(this, srcPackageName, request.account);
        callback.onPostInitComplete(CommonStatusCodes.SUCCESS, new GamesConnectStubService.GamesConnectServiceImpl(this), Bundle.EMPTY);
    }

    class GamesConnectServiceImpl extends IGamesConnectService.Stub {
        private final Context context;

        GamesConnectServiceImpl(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public void signIn(IGamesConnectCallbacks callback, GamesSignInRequest gamesSignInRequest) throws RemoteException {
            Log.d(TAG, String.format("GamesConnectServiceImpl.signIn(gamesSignInRequest=%s)", ""));
            if (gamesSignInRequest.getSignInType() == 0) {
                Log.d(TAG, "signIn when signInType is 0");
                List<Scope> scopes = new ArrayList<>();
                scopes.add(new Scope(Scopes.GAMES_LITE));
                GoogleSignInOptions options = new GoogleSignInOptions.Builder().requestScopes(scopes).setAccount(account)
                        .requestIdToken(true).requestServerAuthCode(false).forceCodeForRefreshToken(false).build();
                SignInConfiguration signInConfiguration = new SignInConfiguration(srcPackageName, options);
                GamesSignInActivity.start(context, signInConfiguration, new ResultReceiver(null) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        try {
                            Log.e(TAG, "GamesConnectServiceImpl.signIn onReceiveResult resultCode=" + resultCode);
                            if (resultCode == 1)
                                callback.onSiginInComplete(Status.SUCCESS, new GamesSignInResult(""));
                            else
                                callback.onSiginInComplete(Status.SIGN_IN_REQUIRED, null);
                        } catch (RemoteException e) {
                            Log.e(TAG, "GamesConnectServiceImpl.signIn", e);
                        }
                    }
                });
            } else {
                Log.d(TAG, "signIn");
                if (AccountManagerUtils.getInstance(context).getDefaultAccount(srcPackageName) != null) {
                    callback.onSiginInComplete(Status.SUCCESS, new GamesSignInResult(""));
                } else {
                    callback.onSiginInComplete(Status.SIGN_IN_REQUIRED, null);
                }
            }
        }
    }
}
