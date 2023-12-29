package com.google.android.gms.audit.internal;

import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.audit.LogAuditRecordsRequest;

interface IAuditService {
    void logAuditRecords(in @nullable(heap=true) LogAuditRecordsRequest logAuditRecordsRequest, IStatusCallback callback);
}