package com.google.android.gms.mdisync.internal;

import org.microg.safeparcel.AutoSafeParcelable;

public class SyncResult extends AutoSafeParcelable {
    @Field(1)
    public byte[] result;

    public static final Creator<SyncResult> CREATOR = new AutoCreator<>(SyncResult.class);
}
