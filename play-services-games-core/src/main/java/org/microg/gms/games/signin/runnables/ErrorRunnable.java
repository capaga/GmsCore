package org.microg.gms.games.signin.runnables;

import android.content.Context;
import android.util.Log;

import org.microg.gms.games.signin.callback.IUpdateCallback;

public class ErrorRunnable extends BaseRunnable implements Runnable {
    private static final String TAG = ErrorRunnable.class.getSimpleName();

    private final boolean flage = true; // GetDisplayBrand Can't access using vpn

    public ErrorRunnable(Context activity, IUpdateCallback callback) {
        super(activity, callback);
    }

    @Override
    public void run() {
        Log.d(TAG, "The network request is unreachable, please check the network");
        if (flage) {
            super.run();
        } else {
            errorRun();
        }

    }
}
