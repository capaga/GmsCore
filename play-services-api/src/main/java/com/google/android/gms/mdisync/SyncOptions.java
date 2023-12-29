package com.google.android.gms.mdisync;

import org.microg.safeparcel.AutoSafeParcelable;

public class SyncOptions extends AutoSafeParcelable {
    public static final Creator<SyncOptions> CREATOR = new AutoCreator<>(SyncOptions.class);
}
