package org.microg.gms.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.BuildConfig;
import com.google.android.gms.R;

import org.microg.gms.common.Constants;
import org.microg.gms.common.PackageUtils;

public class SettingsActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private AlertDialog mHintDownloadMircogCompanion;

    private NavController getNavController() {
        return ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navhost)).getNavController();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        NearbyPreferencesIntegration.Companion.preProcessSettingsIntent(intent);

        setContentView(R.layout.settings_root_activity);

        appBarConfiguration = new AppBarConfiguration.Builder(getNavController().getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, getNavController(), appBarConfiguration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BuildConfig.DEBUG && !Constants.GMS_PACKAGE_SIGNATURE_SHA1.equals(PackageUtils.firstSignatureDigest(getPackageManager(), Constants.GP_PACKAGE_NAME))) {
            if (mHintDownloadMircogCompanion == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.hint);
                builder.setMessage(R.string.download_microG_companion);
                builder.setNegativeButton(android.R.string.ok, (dialog, which) -> {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("appmarket://details?id=" + Constants.GP_PACKAGE_NAME));
                    startActivity(intent);
                });
                mHintDownloadMircogCompanion = builder.create();
            }
            mHintDownloadMircogCompanion.show();
        } else {
            if (mHintDownloadMircogCompanion != null) {
                mHintDownloadMircogCompanion.dismiss();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(getNavController(), appBarConfiguration) || super.onSupportNavigateUp();
    }
}
