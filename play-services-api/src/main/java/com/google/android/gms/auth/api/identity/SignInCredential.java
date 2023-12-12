package com.google.android.gms.auth.api.identity;

import org.microg.safeparcel.AutoSafeParcelable;

public class SignInCredential extends AutoSafeParcelable {
    @Field(1)
    public String email;
    @Field(2)
    public String accountName;
    @Field(3)
    public String firstName;
    @Field(4)
    public String lastName;
    @Field(5)
    public String avatar;
    @Field(6)
    public String f;
    @Field(7)
    public String authToken;
    @Field(8)
    public String h;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }



    public static final Creator<SignInCredential> CREATOR = new AutoCreator<>(SignInCredential.class);
}
