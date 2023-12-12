package com.google.android.gms.proxy;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.huawei.hms.maps.MapClientIdentify;

import org.microg.gms.common.Constants;

public class ApplicationProxy extends Application {
    public static final String TAG = "ApplicationProxy";

    @Override
    public String getPackageName() {
        Log.d(TAG, "ApplicationProxy getPackageName: ");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            //这里如果从GoogleMap过来的直接返回原来的包名，防止权限申请查看 , setMyLocationEnabled这个接口不返回真是包名会有问题
            if (element.getClassName().contains(GoogleMap.class.getSimpleName())) {
                return super.getPackageName();
            }
            if (element.getClassName().contains(MapClientIdentify.class.getSimpleName()) || element.getMethodName().contains("regestIdentity")) {
                Log.w(TAG, "getPackageName: ");
                Log.e(TAG, "lelele GetIntentSender:" + element.getClassName() + "." + element.getMethodName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")");
                return Constants.GMS_PACKAGE_NAME;
            }
        }
        return super.getPackageName();
    }
}
