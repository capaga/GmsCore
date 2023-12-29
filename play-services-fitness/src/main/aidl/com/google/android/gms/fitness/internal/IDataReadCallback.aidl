// IDataReadCallback.aidl
package com.google.android.gms.fitness.internal;
import com.google.android.gms.fitness.request.DataReadResult;
// Declare any non-default types here with import statements

interface IDataReadCallback {
    void onPostResult(in DataReadResult dataReadResult) = 0;
}