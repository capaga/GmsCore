// IStatusCallback.aidl
package com.google.android.gms.fitness.internal;

import com.google.android.gms.common.api.Status;

interface IStatusCallback {
   void onPostResult(in Status status) = 0;
}