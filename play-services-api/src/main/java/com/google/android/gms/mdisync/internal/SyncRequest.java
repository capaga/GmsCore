package com.google.android.gms.mdisync.internal;

import org.microg.safeparcel.AutoSafeParcelable;
import com.google.android.gms.mdisync.SyncOptions;

public class SyncRequest extends AutoSafeParcelable {
    @Field(1)
    public int a;
    @Field(2)
    public byte[] b;
    @Field(3)
    public SyncOptions options;

    public static final Creator<SyncRequest> CREATOR = new AutoCreator<>(SyncRequest.class);
}
