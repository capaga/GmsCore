package org.microg.gms.games.signin.callback;

import android.accounts.Account;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.microg.gms.games.signin.RequestParams;
import org.microg.gms.games.signin.ResponseParams;

public class UpdateCallbackImpl implements IUpdateCallback {

    private final RequestParams requestParams;
    private final Account account;
    private final Activity activity;

    public UpdateCallbackImpl(Activity activity, RequestParams requestParams, Account account) {
        this.requestParams = requestParams;
        this.account = account;
        this.activity = activity;
    }

    @Override
    public void onSuccess() {
        ResponseParams responseParams = new ResponseParams();
        responseParams.requestSdkGoogleSignInfo(activity, account, requestParams, null);
    }

    @Override
    public void OnError() {
        activity.runOnUiThread(() -> Toast.makeText(activity, "HTTP ERROR", Toast.LENGTH_SHORT).show());
    }
}
