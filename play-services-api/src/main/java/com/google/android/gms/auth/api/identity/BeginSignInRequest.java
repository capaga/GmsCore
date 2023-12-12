package com.google.android.gms.auth.api.identity;

import org.microg.safeparcel.AutoSafeParcelable;

public class BeginSignInRequest extends AutoSafeParcelable {
    @Field(1)
    public PasswordRequestOptions passwordRequestOptions;
    @Field(2)
    public GoogleIdTokenRequestOptions googleIdTokenRequestOptions;
    @Field(3)
    public String c;
    @Field(4)
    public boolean d;
    @Field(5)
    public int e;

    public static final Creator<BeginSignInRequest> CREATOR = new AutoCreator<>(BeginSignInRequest.class);
}
