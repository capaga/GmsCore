package org.microg.gms.games.signin.callback;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.microg.gms.games.signin.runnables.ErrorRunnable;
import org.microg.gms.games.signin.runnables.SuccessRunnable;
import org.microg.gms.games.signin.utils.GrpcUtils;

public class ClientConfigCallBackImpl implements GrpcUtils.HttpCallback {

    public static final String TAG = ClientConfigCallBackImpl.class.getSimpleName();
    private Activity activity;
    private IUpdateCallback callback;

    public ClientConfigCallBackImpl(Activity activity, IUpdateCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    public void onSuccess(byte[] bodyBytes) {
        Log.d(TAG, "onSuccess: " + bodyBytes.length);
        if (bodyBytes.length == 0) {
            activity.runOnUiThread(new ErrorRunnable(activity, callback));
            activity.runOnUiThread(new ErrorRunnable(activity, callback));
            return;
        }
        activity.runOnUiThread(new SuccessRunnable(activity, callback, bodyBytes));

    }

    @Override
    public void onError(String msg) {
        Log.d(TAG, "error:" + msg);
        activity.runOnUiThread(new ErrorRunnable(activity, callback));
    }
}
