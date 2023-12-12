package com.google.android.gms.auth.credentials.assistedsignin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;
import com.google.android.gms.common.api.Status;

import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.Constants;
import org.microg.gms.common.Utils;
import org.microg.safeparcel.AutoSafeParcelable;
import org.microg.tools.AccountSelectionActivity;


public class GoogleSignInActivity extends Activity {
    private static final String TAG = "GoogleSignInActivity";
    private static final int REQUEST_CODE_LOGIN = 1000;
    private static final int REQUEST_CODE_ACCOUNT_PICK = 1002;

    private static final String STATUS = "status";
    private static final String SIGN_IN_CREDENTIAL = "sign_in_credential";
    private GetSignInIntentRequest mGetSignInIntentRequest;
    private String clientPackageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initData();
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(AssistedSignInServiceImpl.GET_SIGN_IN_INTENT_REQUEST)) {
                AutoSafeParcelable safeParcelable = Utils.bytesArrayToSafeParcelableInstance(
                        extras.getByteArray(AssistedSignInServiceImpl.GET_SIGN_IN_INTENT_REQUEST),
                        GetSignInIntentRequest.CREATOR);
                if (safeParcelable != null) {
                    this.mGetSignInIntentRequest = (GetSignInIntentRequest) safeParcelable;
                }
            }
            if (extras.containsKey(AssistedSignInServiceImpl.CLIENT_PACKAGE_NAME)) {
                this.clientPackageName = extras.getString(AssistedSignInServiceImpl.CLIENT_PACKAGE_NAME);
            }
        }
        if (!AccountManagerUtils.getInstance(this).isLogin()) {
            Intent intent = new Intent("com.google.android.gms.auth.login.LOGIN");
            intent.setPackage(Constants.GMS_PACKAGE_NAME);
            startActivityForResult(intent, REQUEST_CODE_LOGIN);
        } else {
            goAccountPick();
        }
    }

    public void goAccountPick() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestServerAuthCode(this.mGetSignInIntentRequest.clientId)
                .build();
        SignInConfiguration config = new SignInConfiguration(this.clientPackageName, googleSignInOptions);

        Intent intent = new Intent();
        intent.putExtra("config", config);
        intent.setClass(this, AccountSelectionActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ACCOUNT_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=>" + requestCode + " resultCode=>" + resultCode + " data=>" + data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN:
                    goAccountPick();
                    break;
                case REQUEST_CODE_ACCOUNT_PICK:
                    SignInAccount signInAccount = data.getParcelableExtra("signInAccount");
                    Bundle bundle = new Bundle();
                    if (signInAccount != null) {
                        GoogleSignInAccount googleSignInAccount = signInAccount.getGoogleSignInAccount();
                        if (googleSignInAccount != null) {
                            SignInCredential signInCredential = new SignInCredential();
                            signInCredential.setEmail(googleSignInAccount.getEmail());
                            signInCredential.setAccountName(googleSignInAccount.getDisplayName());
                            signInCredential.setFirstName(googleSignInAccount.getFamilyName());
                            signInCredential.setLastName(googleSignInAccount.getGivenName());
                            signInCredential.setAuthToken(googleSignInAccount.getIdToken());
                            bundle.putByteArray(SIGN_IN_CREDENTIAL, Utils.safeParcelableInstanceToBytesArray(signInCredential));

                            bundle.putByteArray(STATUS, Utils.safeParcelableInstanceToBytesArray(Status.SUCCESS));
                        } else {
                            bundle.putByteArray(STATUS, Utils.safeParcelableInstanceToBytesArray(Status.CANCELED));
                        }
                    } else {
                        bundle.putByteArray(STATUS, Utils.safeParcelableInstanceToBytesArray(Status.CANCELED));
                    }

                    Intent retData = new Intent();
                    retData.putExtras(bundle);
                    setResult(Activity.RESULT_OK, retData);
                    finish();
                    break;
            }
        } else {
            if (requestCode == REQUEST_CODE_LOGIN && AccountManagerUtils.getInstance(this).isLogin()) {
                goAccountPick();
                return;
            }
            finish();
        }
    }
}
