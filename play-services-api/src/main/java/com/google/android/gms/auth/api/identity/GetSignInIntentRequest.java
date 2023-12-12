package com.google.android.gms.auth.api.identity;

import org.microg.safeparcel.AutoSafeParcelable;

public class GetSignInIntentRequest extends AutoSafeParcelable {
    @Field(1)
    public String clientId;
    @Field(2)
    public String b;
    @Field(3)
    public String requestTag;
    @Field(4)
    public String requestToken;
    @Field(5)
    public boolean e;
    @Field(6)
    public int f;


    public static final Creator<GetSignInIntentRequest> CREATOR = new AutoCreator<>(GetSignInIntentRequest.class);
}
