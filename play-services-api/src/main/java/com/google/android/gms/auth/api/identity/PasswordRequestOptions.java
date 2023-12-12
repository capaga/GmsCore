package com.google.android.gms.auth.api.identity;

import org.microg.safeparcel.AutoSafeParcelable;

public class PasswordRequestOptions extends AutoSafeParcelable {
    @Field(1)
    public boolean a;
    public static final Creator<PasswordRequestOptions> CREATOR = new AutoCreator<>(PasswordRequestOptions.class);
}
