package org.microg.gms.games.signin.runnables;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.microg.common.beans.ClientAuthConfig;
import org.microg.gms.games.signin.callback.IUpdateCallback;

import java.nio.ByteBuffer;

public class SuccessRunnable extends BaseRunnable {

    public static final String TAG = SuccessRunnable.class.getSimpleName();
    private byte[] bodyBytes; // GetDisplayBrand Can't access using vpn

    public SuccessRunnable(Context activity, IUpdateCallback callback, byte[] data) {
        this(activity, callback);
        this.bodyBytes = data;
    }

    public SuccessRunnable(Context activity, IUpdateCallback callback) {
        super(activity, callback);
    }


    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        System.arraycopy(bodyBytes, 0, byteBuffer.array(), 0, 5);
        // grpc  When the protocol is transmitted, it is transmitted in continuous data bytes.
        // If the data length is not read to truncate, it will cause a parsing error.
        int length = byteBuffer.array()[4];
        byte[] results = new byte[bodyBytes.length - 5];
        System.arraycopy(bodyBytes, 5, results, 0, bodyBytes.length - 5);
        try {
            ClientAuthConfig clientAuthConfig = ClientAuthConfig.ADAPTER.decode(results);
            if (!TextUtils.isEmpty(clientAuthConfig.resultBody.packeAgeName)) {
                callback.onSuccess();
            } else {
                callback.OnError();
                Log.d(TAG, "The current apk does not support google Auth login");
            }
        } catch (Exception e) {
            Log.d(TAG, "run: " + e);
        }
    }
}
