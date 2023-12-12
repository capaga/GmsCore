package org.microg.gms.games.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;

public class GamesSignInActivity extends Activity {
    public static final String TAG = GamesSignInActivity.class.getSimpleName();
    public static final String KEY_RESULT_RECEIVER = "resultReceiver";
    public static final String KEY_RESULT_DATA = "resultData";
    public static final String KEY_SIGN_IN_CONFIGURATION = "config";
    public static final String KEY_SIGN_IN_ACCOUNT = "signInAccount";
    private static final int SIGN_IN_REQUEST_CODE = 3002;
    private ResultReceiver resultReceiver;
    private SignInConfiguration signInConfiguration;

    public static void start(Context context, SignInConfiguration signInConfiguration, ResultReceiver resultReceiver) {
        Log.d(TAG, "GamesSignInActivity start");
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(context.getPackageName());
        intent.setClass(context, GamesSignInActivity.class);
        intent.putExtra(KEY_RESULT_RECEIVER, resultReceiver);
        intent.putExtra(KEY_SIGN_IN_CONFIGURATION, signInConfiguration);
        context.startActivity(intent);
    }

    private SignInAccount getCachedAccount() {
        return null;
    }

    private boolean startSignIn() {
        Intent startIntent = getIntent();
        if (startIntent == null) {
            return false;
        }
        signInConfiguration = startIntent.getParcelableExtra(KEY_SIGN_IN_CONFIGURATION);
        if (signInConfiguration == null) {
            return false;
        }
        resultReceiver = startIntent.getParcelableExtra(KEY_RESULT_RECEIVER);
        if (resultReceiver == null) {
            return false;
        }
        SignInAccount signInAccount = getCachedAccount();
        if (signInAccount != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_RESULT_DATA, signInAccount);
            resultReceiver.send(1, bundle);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra(KEY_SIGN_IN_CONFIGURATION, signInConfiguration);
//            intent.setClassName(this, "org.microg.gms.auth.signin.AuthSignInActivity");
            intent.setClassName(this,"org.microg.common.activitys.SignInActivity");
            startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TextView(this));
        Log.d(TAG, "GamesSignInActivity onCreate: ");
        if (!startSignIn()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultReceiver == null) {
                return;
            }
            if(data == null){
                resultReceiver.send(0, Bundle.EMPTY);
                finish();
                return;
            }

            SignInAccount signInAccount = data.getParcelableExtra(KEY_SIGN_IN_ACCOUNT);
            Log.d(TAG, "onActivityResult: " + signInAccount);
            if (signInAccount == null) {
                resultReceiver.send(0, Bundle.EMPTY);
                return;
            }
            //GoogleAccountCache.getInstance(this).save(signInConfiguration.getPackageName(), signInAccount.getGoogleSignInAccount());
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_RESULT_DATA, signInAccount);
            resultReceiver.send(1, bundle);
            Log.d(TAG, "onActivityResult");
        }
        finish();
    }
}
