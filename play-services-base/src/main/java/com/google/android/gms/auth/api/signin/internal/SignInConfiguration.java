package com.google.android.gms.auth.api.signin.internal;

import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.microg.safeparcel.AutoSafeParcelable;


public class SignInConfiguration extends AutoSafeParcelable {
    @Field(2)
    private String packageName;
    @Field(5)
    private GoogleSignInOptions options;

    public SignInConfiguration() {

    }

    public SignInConfiguration(String packageName, GoogleSignInOptions googleSignInOptions) {
        if (TextUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("Given String is empty or null");
        }
        this.packageName = packageName;
        this.options = googleSignInOptions;
    }



    public static final Creator<SignInConfiguration> CREATOR = new AutoCreator<>(SignInConfiguration.class);

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public GoogleSignInOptions getOptions() {
        return options;
    }

    public void setOptions(GoogleSignInOptions options) {
        this.options = options;
    }
}
