/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.gms.backup;

import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * 通过反射的方式调用BackupDataOutput类下的相关方法
 */
public class BackupDataOutputWrapper {
    private static final String TAG = "BackupDataOutputWrapper";
    private Object instance;
    private Class<?> aClass;
    public BackupDataOutputWrapper(FileDescriptor fd) {
        try {
            aClass = Class.forName("android.app.backup.BackupDataOutput");
            Constructor<?> constructor = aClass.getConstructor(FileDescriptor.class);
            instance = constructor.newInstance(fd);
        }catch (Exception e){
            Log.e(TAG, "constructor error", e);
        }
    }

    public BackupDataOutputWrapper(FileDescriptor fd, long quota) {
        try {
            aClass = Class.forName("android.app.backup.BackupDataOutput");
            Constructor<?> constructor = aClass.getConstructor(FileDescriptor.class, long.class);
            instance = constructor.newInstance(fd, quota);
        }catch (Exception e){
            Log.e(TAG, "constructor error",  e);
        }
    }

    public BackupDataOutputWrapper(FileDescriptor fd, long quota, int transportFlags) {
        try {
            aClass = Class.forName("android.app.backup.BackupDataOutput");
            Constructor<?> constructor = aClass.getConstructor(FileDescriptor.class, long.class, int.class);
            instance = constructor.newInstance(fd, quota, transportFlags);
        }catch (Exception e){
            Log.e(TAG, "BackupDataOutputWrapper: error", e);
        }
    }


    public long getQuota() {
        try {
            Method method = aClass.getDeclaredMethod("getQuota");
            method.setAccessible(true);
            return (long) method.invoke(instance);
        } catch (Exception e){
            Log.e(TAG, "getQuota: error", e);
        }
        return 0L;
    }

    public int getTransportFlags() {
        try {
            Method method = aClass.getDeclaredMethod("getTransportFlags");
            method.setAccessible(true);
            return (int) method.invoke(instance);
        } catch (Exception e){
            Log.e(TAG, "getTransportFlags: error", e);
        }
        return 0;
    }

    public int writeEntityHeader(String key, int dataSize) {
        try {
            Method method = aClass.getDeclaredMethod("writeEntityHeader", String.class, int.class);
            method.setAccessible(true);
            return (int) method.invoke(instance, key, dataSize);
        } catch (Exception e){
            Log.e(TAG, "writeEntityHeader: error", e);
        }
        return 0;
    }

    public int writeEntityData(byte[] data, int size) throws IOException {
        try {
            Method method = aClass.getDeclaredMethod("writeEntityHeader", byte[].class, int.class);
            method.setAccessible(true);
            return (int) method.invoke(instance, data, size);
        } catch (Exception e){
            Log.e(TAG, "writeEntityData: error", e);
        }
        return 0;
    }

    public void setKeyPrefix(String keyPrefix) {
        try {
            Method method = aClass.getDeclaredMethod("setKeyPrefix", String.class);
            method.setAccessible(true);
            method.invoke(instance, keyPrefix);
        } catch (Exception e){
            Log.e(TAG, "setKeyPrefix: error", e);
        }
    }


    @Override
    protected void finalize() {
        try {
            Method method = aClass.getDeclaredMethod("finalize");
            method.setAccessible(true);
            method.invoke(instance);
        } catch (Exception e){
            Log.e(TAG, "finalize: error", e);
        }
    }
}

