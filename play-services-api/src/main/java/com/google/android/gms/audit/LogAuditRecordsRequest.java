package com.google.android.gms.audit;

import org.microg.safeparcel.AutoSafeParcelable;

public class LogAuditRecordsRequest extends AutoSafeParcelable {
    @Field(value = 1, mayNull = true)
    public int writeMode;
    @Field(value = 2,mayNull = true)
    public int componentId;
    @Field(value = 3,mayNull = true)
    public String accountName;
    @Field(value = 4,mayNull = true)
    public byte[][] auditRecords;
    @Field(value = 5,mayNull = true)
    public byte[] traceToken;
    @Field(value = 6,mayNull = true)
    public byte[] auditToken;

    public static final Creator<LogAuditRecordsRequest> CREATOR = new AutoCreator<>(LogAuditRecordsRequest.class);
}
