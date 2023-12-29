package com.google.android.gms.mdisync.internal;

import org.microg.safeparcel.AutoSafeParcelable;

public class TeleportingSyncRequest extends AutoSafeParcelable {
    @Field(1)
    public SyncRequest syncRequest;

    public static final Creator<TeleportingSyncRequest> CREATOR = new AutoCreator<>(TeleportingSyncRequest.class);
}
