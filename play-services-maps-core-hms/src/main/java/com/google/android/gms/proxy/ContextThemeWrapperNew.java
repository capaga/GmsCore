package com.google.android.gms.proxy;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import androidx.appcompat.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ContextThemeWrapperNew extends ContextThemeWrapper {
    public static final String TAG = "ContextThemeWrapperNew";
    private ApplicationProxy applicationProxy;

    public ContextThemeWrapperNew(Application application) {
        applicationProxy = new ApplicationProxy();

        try {

            Class className = application.getClass();
            while (className != ContextWrapper.class) {
                className = className.getSuperclass();
            }

            Log.d(TAG, "ContextThemeWrapperNew: " + className);
            Field mBaseField = className.getDeclaredField("mBase");
            mBaseField.setAccessible(true);
            Object mBase = mBaseField.get(application);

            Method attachBaseContextMethod = applicationProxy.getClass().getSuperclass().getSuperclass().getDeclaredMethod("attachBaseContext", Context.class);
            attachBaseContextMethod.setAccessible(true);
            attachBaseContextMethod.invoke(applicationProxy, mBase);

        } catch (Exception e) {
            Log.d(TAG, "ContextThemeWrapperNew: " + e);
        }
    }

    @Override
    public Context getApplicationContext() {
        Log.d(TAG, "ContextThemeWrapperNew getApplicationContext: ");
        return applicationProxy;
    }
}
