package com.google.android.gms.auth.api.identity;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.ArrayList;


public class GoogleIdTokenRequestOptions extends AutoSafeParcelable {
    @Field(1)
    public boolean a;
    @Field(2)
    public String clientId;
    @Field(3)
    public String requestToken;
    @Field(4)
    public boolean d;
    @Field(5)
    public String e;
    @Field(6)
    public ArrayList<?> f;
    @Field(7)
    public boolean g;

    public static final Creator<GoogleIdTokenRequestOptions> CREATOR = new AutoCreator<>(GoogleIdTokenRequestOptions.class);
}
