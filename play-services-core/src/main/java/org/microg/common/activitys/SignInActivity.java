package org.microg.common.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;

import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.Constants;
import org.microg.tools.AccountSelectionActivity;


public final class SignInActivity extends Activity {
    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 1000;
    private static final int REQUEST_CODE_ACCOUNT_PICK = 1002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initData();
    }

    private void initData() {
        if (!AccountManagerUtils.getInstance(this).isLogin()) {
            Intent intent = new Intent("com.google.android.gms.auth.login.LOGIN");
            intent.setPackage(Constants.GMS_PACKAGE_NAME);
            startActivityForResult(intent, REQUEST_CODE_LOGIN);
        } else {
            goAccountPick();
        }
    }

    public void goAccountPick() {
        Intent intent = new Intent();
        Bundle extras = getIntent().getExtras();
        intent.putExtras(extras);
        intent.setClass(this, AccountSelectionActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ACCOUNT_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN:
                    goAccountPick();
                    break;
                case REQUEST_CODE_ACCOUNT_PICK:
                    setResult(Activity.RESULT_OK, data);
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
