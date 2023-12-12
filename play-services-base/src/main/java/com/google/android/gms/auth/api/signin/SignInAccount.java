package com.google.android.gms.auth.api.signin;

import org.microg.safeparcel.AutoSafeParcelable;


public class SignInAccount extends AutoSafeParcelable {
    @Field(4)
    private String accountName;
    @Field(7)
    private GoogleSignInAccount googleSignInAccount;
    @Field(8)
    private String userId;

    public SignInAccount() {

    }

    public SignInAccount(String accountName, GoogleSignInAccount googleSignInAccount, String userId) {
        this.accountName = accountName;
        this.googleSignInAccount = googleSignInAccount;
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static final Creator<SignInAccount> CREATOR = new AutoCreator<>(SignInAccount.class);
}
