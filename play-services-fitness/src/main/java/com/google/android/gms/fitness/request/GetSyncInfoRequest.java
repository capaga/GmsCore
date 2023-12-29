package com.google.android.gms.fitness.request;

import org.microg.safeparcel.AutoSafeParcelable;
import com.google.android.gms.fitness.internal.ISyncInfoCallback;
public class GetSyncInfoRequest extends AutoSafeParcelable {
    public static final Creator<GetSyncInfoRequest> CREATOR = new AutoCreator<>(GetSyncInfoRequest.class);
    @Field(1)
    public ISyncInfoCallback callback;
}

