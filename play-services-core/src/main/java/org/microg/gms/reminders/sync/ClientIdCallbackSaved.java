package org.microg.gms.reminders.sync;

import com.google.android.gms.reminders.internal.IRemindersCallbacks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientIdCallbackSaved {
    private static final Map<String,IRemindersCallbacks> savedCallbacks = new ConcurrentHashMap<>();

    public static IRemindersCallbacks get(String key){
        return savedCallbacks.get(key);
    }

    public static void put(String key,IRemindersCallbacks callback){
        savedCallbacks.put(key,callback);
    }
}
