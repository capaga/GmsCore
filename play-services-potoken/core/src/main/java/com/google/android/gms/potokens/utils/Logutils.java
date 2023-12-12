package com.google.android.gms.potokens.utils;

import android.util.Log;

public class Logutils {

    public static String Tag = "dao";

    public static boolean isEnable = true;

    public static void errorMsg(String data){
        if (!isEnable) return;
        Log.e(Tag,data);
    }

    public static void i(String data){
        if (!isEnable) return;
        Log.i(Tag,data);
    }

}
