package org.microg.gms.games.signin.runnables;

import android.content.Context;

import org.microg.gms.games.signin.callback.IUpdateCallback;

public abstract class BaseRunnable implements Runnable {

    protected Context activity;
    protected IUpdateCallback callback;

    public BaseRunnable(Context activity, IUpdateCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    public void run() {
        callback.onSuccess();
    }
    protected  void errorRun(){
        callback.OnError();
    }
}
