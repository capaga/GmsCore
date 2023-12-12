package org.microg.gms.reminders.sync;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.microg.gms.common.Constants;
import org.microg.gms.reminders.InstanceRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class RemindersRequestHelper {
    public static final String TAG = RemindersRequestHelper.class.getSimpleName();
    private static final HashMap<String, String> httpHeaders = new HashMap<>();

    public static @Nullable CreateTaskRequest getCreateTaskRequest(byte[] operationRequest) {
        try {
            CreateTaskRequest.Builder createBuild = CreateTaskRequest.ADAPTER.decode(operationRequest).newBuilder();
            createBuild.httpHeaderInfo(getHttpHeaderInfo(createBuild.httpHeaderInfo));
            return createBuild.build();
        } catch (IOException e) {
            Log.w(TAG, "getCreateTaskRequest: ", e);
        }
        return null;
    }

    public static @Nullable CreateRecurrenceRequest getCreateRecurrenceRequest(byte[] operationRequest) {
        try {
            CreateRecurrenceRequest.Builder builder = CreateRecurrenceRequest.ADAPTER.decode(operationRequest).newBuilder();
            builder.httpHeaderInfo(getHttpHeaderInfo(builder.httpHeaderInfo));
            return builder.build();
        } catch (IOException e) {
            Log.w(TAG, "getCreateRecurrenceRequest: ", e);
        }
        return null;
    }

    public static @Nullable DeleteTaskRequest getDeleteTaskRequest(byte[] operationRequest) {
        try {
            DeleteTaskRequest.Builder builder = DeleteTaskRequest.ADAPTER.decode(operationRequest).newBuilder();
            builder.httpHeaderInfo(getHttpHeaderInfo(builder.httpHeaderInfo));
            return builder.build();
        } catch (IOException e) {
            Log.w(TAG, "getDeleteTaskRequest: ", e);
        }
        return null;
    }

    public static @Nullable DeleteRecurrenceRequest getDeleteRecurrenceRequest(byte[] operationRequest) {
        try {
            DeleteRecurrenceRequest.Builder builder = DeleteRecurrenceRequest.ADAPTER.decode(operationRequest).newBuilder();
            builder.httpHeaderInfo(getHttpHeaderInfo(builder.httpHeaderInfo));
            return builder.build();
        } catch (IOException e) {
            Log.w(TAG, "getDeleteRecurrenceRequest: ", e);
        }

        return null;
    }

    public static @Nullable UpdateTaskRequest getUpdateTaskRequest(byte[] operationRequest) {
        try {
            UpdateTaskRequest.Builder upateTaskBuilder = UpdateTaskRequest.ADAPTER.decode(operationRequest).newBuilder();
            upateTaskBuilder.httpHeaderInfo(getHttpHeaderInfo(upateTaskBuilder.httpHeaderInfo));
            return upateTaskBuilder.build();
        } catch (IOException e) {
            Log.w(TAG, "getUpdateTaskRequest: ", e);
        }
        return null;
    }

    public static @Nullable ChangeRecurrenceRequest getChangeRecurrenceRequest(byte[] operationRequest) {
        try {
            ChangeRecurrenceRequest.Builder builder = ChangeRecurrenceRequest.ADAPTER.decode(operationRequest).newBuilder();
            builder.httpHeaderInfo(getHttpHeaderInfo(builder.httpHeaderInfo));
            return builder.build();
        } catch (IOException e) {
            Log.w(TAG, "getChangeRecurrenceRequest: ", e);
        }
        return null;
    }

    private static HttpHeaderInfo getHttpHeaderInfo(HttpHeaderInfo httpHeaderInfo) {
        if (httpHeaderInfo == null) {
            httpHeaderInfo = InstanceRegistry.getInstance(HttpHeaderInfo.class);
        }
        String key;
        if (httpHeaderInfo == null) {
            key = null;
        } else {
            key  = httpHeaderInfo.httpHeader;
        }
        HttpHeaderInfo.Builder builder = InstanceRegistry.getInstance(HttpHeaderInfo.class).newBuilder();
        String httpHeader = getHttpHeader((TextUtils.isEmpty(key) ? "Reminders-Android" : "Reminders-Android_".concat(key)));
        builder.httpHeader(httpHeader);

        TimeZoneId.Builder timeZoneIdBuilder = InstanceRegistry.getInstance(TimeZoneId.class).newBuilder();
        timeZoneIdBuilder.id(TimeZone.getDefault().getID());
        builder.timeZoneId(timeZoneIdBuilder.build());
        return builder.build();
    }

    public static String getHttpHeader(String key) {
        String httpHeader;
        StringBuilder stringBuilder;
        if (TextUtils.isEmpty(key)) {
            key = "GMS/1.0";
        }

        httpHeader = httpHeaders.get(key);
        if (httpHeader == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Mozilla 5.0 (Linux; U; Android ");
            stringBuilder.append(Build.VERSION.RELEASE);
            stringBuilder.append("; ");
            stringBuilder.append(Locale.getDefault().getLanguage());
            stringBuilder.append("; ");
            stringBuilder.append(Build.MODEL);
            stringBuilder.append("; Build/");
            stringBuilder.append(Build.ID);
            stringBuilder.append("); ");
            stringBuilder.append(Constants.GMS_PACKAGE_NAME);
            stringBuilder.append('/');
            stringBuilder.append(Constants.GMS_VERSION_CODE);
            stringBuilder.append("; FastParser/1.1; ");
            stringBuilder.append(key);
            stringBuilder.append("; (gzip)");
            httpHeader = stringBuilder.toString();
            httpHeaders.put(key, httpHeader);

            return httpHeader;
        }

        return httpHeader;
    }
}
