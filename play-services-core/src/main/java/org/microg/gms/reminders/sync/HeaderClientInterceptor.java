package org.microg.gms.reminders.sync;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderClientInterceptor implements Interceptor {

    private final String token;
    private final String spatula;

    public HeaderClientInterceptor(String token,String spatula) {
        this.token = token;
        this.spatula = spatula;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder newRequestBuilder = originalRequest.newBuilder();

        if (!TextUtils.isEmpty(spatula)) {
            newRequestBuilder.addHeader("X-Goog-Spatula", spatula);
        }
        if (!TextUtils.isEmpty(token)) {
            newRequestBuilder.addHeader("Authorization", token);
        }

        return chain.proceed(newRequestBuilder.build());
    }
}
