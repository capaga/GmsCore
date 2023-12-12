package com.google.android.gms.proxy;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProxyUtil {
    public static final String TAG = "ProxyUtil";
    public static void proxyBase(Context context) {
        try {
            Class className = context.getClass();
            while (className != ContextWrapper.class) {
                className = className.getSuperclass();
            }
            Class contextWrapperClass = className;
            Field mBaseField = contextWrapperClass.getDeclaredField("mBase");
            mBaseField.setAccessible(true);
            Object contextWrapperObject = mBaseField.get(context);

            ContextThemeWrapperNew contextThemeWrapperNew = new ContextThemeWrapperNew((Application) context.getApplicationContext());
            Method attachBaseContextMethod = contextThemeWrapperNew.getClass().getSuperclass().getDeclaredMethod("attachBaseContext", Context.class);
            attachBaseContextMethod.setAccessible(true);
            attachBaseContextMethod.invoke(contextThemeWrapperNew, contextWrapperObject);

            mBaseField.set(context, contextThemeWrapperNew);
        } catch (Exception e) {
            Log.e("lelelele", "onCreate: " + e);
        }
    }
}
