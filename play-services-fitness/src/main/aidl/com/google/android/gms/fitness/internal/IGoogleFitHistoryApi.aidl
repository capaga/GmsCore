// IGoogleFitHistoryApi.aidl
package com.google.android.gms.fitness.internal;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.GetSyncInfoRequest;
import com.google.android.gms.fitness.request.DataInsertRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
// Declare any non-default types here with import statements

interface IGoogleFitHistoryApi {
    void a(in DataDeleteRequest dataDeleteRequest) = 0;
    void b(in GetSyncInfoRequest getSyncInfoRequest) = 1;
    void g(in DataInsertRequest dataInsertRequest) = 2;
    void h(in DataReadRequest dataReadRequest) = 3;
}