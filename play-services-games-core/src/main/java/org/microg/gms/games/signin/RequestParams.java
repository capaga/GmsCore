package org.microg.gms.games.signin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;

import org.microg.gms.common.Constants;
import org.microg.gms.games.signin.utils.BytesUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

public class RequestParams {
    private SignInConfiguration signInConfiguration;
    private String clientId = "";
    private boolean isIdToken = false; // Whether it is isIdToken login
    private boolean oauthToPrompt = false; // Whether it is isIdToken login
    private final Set<Scope> list = new HashSet<>(5);

    // The registered package name
    private String packageName;
    private String clientPackageName;

    // apk signature requesting login
    private String clientPackageNameSignerHex;
    private String clientPackageNameSignerBase;
    // gms signature
    private String currentPackageNameSignerHex;
    private String currentPackageNameSignerBase;

    private String accountType = "";

    private int includeEmail = 0;
    private int includeProfile = 0;


    public void initData(Context activity, Intent intent) {
        signInConfiguration = intent.getParcelableExtra("config");
        initData(activity, signInConfiguration);
    }

    public void initData(Context activity, SignInConfiguration signInConfiguration) {
        this.signInConfiguration = signInConfiguration;
        if (this.signInConfiguration != null) {
            packageName = this.signInConfiguration.getPackageName();
            clientPackageName = this.signInConfiguration.getPackageName();
            list.add(new Scope(Scopes.USERINFO_PROFILE));
            list.add(new Scope(Scopes.USERINFO_EMAIL));
            list.addAll(this.signInConfiguration.getOptions().getScopes());

            boolean profile = this.signInConfiguration.getOptions().getScopes().contains(new Scope(Scopes.PROFILE));
            boolean email = this.signInConfiguration.getOptions().getScopes().contains(new Scope(Scopes.EMAIL));
            if (profile) {
                includeProfile = 1;
            }
            if (email) {
                includeEmail = 1;
            }

            clientPackageNameSignerHex = BytesUtils.bytesToHex(getHexSign(activity, clientPackageName));
            clientPackageNameSignerBase = BytesUtils.bytesToBase64(getHexSign(activity, clientPackageName));

            currentPackageNameSignerHex = Constants.GMS_PACKAGE_SIGNATURE_SHA1;
            if (this.signInConfiguration.getOptions().isIdTokenRequested() || this.signInConfiguration.getOptions().isServerAuthCodeRequested()) {
                isIdToken = true;
                clientId = this.signInConfiguration.getOptions().getServerClientId();
                accountType = String.format("%s:%s:%s:%s?include_email=%d&include_profile=%d",
                        clientPackageName, clientPackageNameSignerHex, "audience:server:client_id",
                        clientId, includeEmail, includeProfile);
            }
            if (this.signInConfiguration.getOptions().isServerAuthCodeRequested()) {
                oauthToPrompt = true;
            }

        }
    }

    private static byte[] getHexSign(Context context, String packageName) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            if (packageInfo.signatures == null || packageInfo.signatures.length != 1) {
                return null;
            }
            return md.digest(packageInfo.signatures[0].toByteArray());

        } catch (PackageManager.NameNotFoundException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public Set<Scope> getList() {
        return list;
    }

    public SignInConfiguration getSignInConfiguration() {
        return signInConfiguration;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isIdToken() {
        return isIdToken;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClientPackageName() {
        return clientPackageName;
    }

    public String getClientPackageNameSignerHex() {
        return clientPackageNameSignerHex;
    }

    public String getClientPackageNameSignerBase() {
        return clientPackageNameSignerBase;
    }

    public String getCurrentPackageNameSignerHex() {
        return currentPackageNameSignerHex;
    }

    public String getCurrentPackageNameSignerBase() {
        return currentPackageNameSignerBase;
    }

    public String getAccountType() {
        return accountType;
    }

    public int getIncludeEmail() {
        return includeEmail;
    }

    public int getIncludeProfile() {
        return includeProfile;
    }

    public boolean isOauthToPrompt() {
        return oauthToPrompt;
    }
}
