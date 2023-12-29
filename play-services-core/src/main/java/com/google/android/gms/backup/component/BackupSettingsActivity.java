package com.google.android.gms.backup.component;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.R;

import org.microg.gms.backup.BackupAccountsManager;
import org.microg.gms.backup.BackupHttpRequestUtil;
import org.microg.gms.backup.BackupManagerWrapper;
import org.microg.gms.backup.BackupSettingsSharedPrefs;
import org.microg.gms.backup.photos.PhotosBackupApiClient;

import com.google.android.gms.backup.AppBackupDetail;
import com.google.android.gms.backup.BackupResponse;
import com.google.android.gms.backup.BackupTransportService;
import com.google.android.gms.backup.GetStorageQuotaInfoRequest;
import com.google.android.gms.backup.GetStorageQuotaInfoResponse;
import com.google.android.gms.backup.GrpcAndroidPlatformBackupRestoreServiceClient;
import com.google.android.libraries.photos.backup.api.AutoBackupState;
import com.squareup.wire.GrpcClient;

import org.microg.gms.reminders.sync.HeaderClientInterceptor;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * Google One 会通过类名的方式启动本Activity，因此不能随意移动位置
 */
public class BackupSettingsActivity extends AppCompatActivity {
    private static final String TAG = "BackupSettingsActivity";
    Button btnManageBackupStorage, btnBackupNow;
    Switch sthGoogleOneBackup, sthBackupUsingMobileData;
    TextView tvBackupAccountName, tvCallHistoryBackupDetail,
            tvAppBackupDetail,
            tvStorageUsedInfo,
            tvLastBackupTime,
            tvBackupNetworkTips,
            tvMessagesBackupDetail,
            tvDeviceSettingBackupDetail,
            tvPhotosBackupDetail;

    LinearLayout llAccountSyncSettings, llPhotosBackupSetting, llStorageInfo;

    private BackupManagerWrapper mBackupManagerWrapper;
    private PhotosBackupApiClient mPhotosBackupApiClient;
    private BackupAccountsManager mBackupAccountManager;
    private BackupSettingsSharedPrefs mBackupSettingsSharedPrefs;
    private final Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_settings);

        this.mBackupSettingsSharedPrefs = new BackupSettingsSharedPrefs(this.getApplicationContext());

        // UI初始化
        tvBackupAccountName = findViewById(R.id.backup_settings_account_name);
        tvStorageUsedInfo = findViewById(R.id.backup_settings_storage_used_info);
        tvAppBackupDetail = findViewById(R.id.backup_settings_apps_backup_detail);
        tvPhotosBackupDetail = findViewById(R.id.backup_settings_photos_and_videos_backup_detail);
        tvLastBackupTime = findViewById(R.id.backup_settings_last_backup_time);
        tvMessagesBackupDetail = findViewById(R.id.backup_settings_sms_messages_backup_detail);
        tvCallHistoryBackupDetail = findViewById(R.id.backup_settings_call_history_backup_detail);
        tvDeviceSettingBackupDetail = findViewById(R.id.backup_settings_device_settings_backup_detail);

        llAccountSyncSettings = findViewById(R.id.backup_settings_google_account_sync_linear);
        llAccountSyncSettings.setOnClickListener(view -> openAccountSyncSettingsActivity());

        llPhotosBackupSetting = findViewById(R.id.backup_settings_photos_and_videos_linear);
        llPhotosBackupSetting.setOnClickListener(view -> openPhotosBackupSettingsActivity());

        llStorageInfo = findViewById(R.id.backup_settings_storage_info_linear);
        llStorageInfo.setOnClickListener(view -> openChooseBackupAccountDialogs(view));

        // 点击按钮调用默认浏览器打开 https://one.google.com/storage
        btnManageBackupStorage = findViewById(R.id.backup_settings_manage_backups_storage);
        btnManageBackupStorage.setOnClickListener(view -> openUrlWithDefaultBrowser("https://one.google.com/storage"));

        // 通过查询BackingUpFlag的方式确定是否正在备份
        btnBackupNow = findViewById(R.id.backup_settings_backup_now);
        btnBackupNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBackupManagerWrapper.isBackupEnabled()) {
                    btnBackupNow.setClickable(false);
                    btnBackupNow.setText(R.string.backup_settings_backing_up);
                    mBackupManagerWrapper.backupNow();
                    new BackupStateMonitorThread().start();
                }
            }
        });


        // 通过查询系统配置设置 Google One 备份开关的状态
        sthGoogleOneBackup = findViewById(R.id.backup_settings_google_one_backup_switch);
        sthGoogleOneBackup.setOnClickListener(view -> {
            boolean checked = sthGoogleOneBackup.isChecked();
            if (checked) {
                btnBackupNow.setClickable(true);
                mBackupManagerWrapper.setBackupEnabled(true);
                mBackupManagerWrapper.setGmsBackupTransportSelected();
            } else {
                btnBackupNow.setClickable(false);
                mBackupManagerWrapper.setBackupEnabled(false);
                mPhotosBackupApiClient.disableAutoBackup();
                new ClearBackupDataThread().start();
            }
        });

        //　通过SharedPreferences保存的是否使用移动数据进行备份的设置确定 sthBackupUsingMobileData 是否启用
        tvBackupNetworkTips = findViewById(R.id.backup_settings_tips);
        sthBackupUsingMobileData = findViewById(R.id.backup_settings_backup_using_mobile_or_metered_wifi_data_switch);
        sthBackupUsingMobileData.setOnClickListener(view -> {
            boolean isChecked = sthBackupUsingMobileData.isChecked();
            this.mBackupSettingsSharedPrefs.setValue(BackupSettingsSharedPrefs.KEY_USE_MOBILE_DATA, isChecked);
            Log.d(TAG, "onCreate: setUseMobileData=>" + isChecked);
            if (isChecked) {
                tvBackupNetworkTips.setText(R.string.backup_settings_network_tips_use_mobile_data);
            } else {
                tvBackupNetworkTips.setText(R.string.backup_settings_network_tips_use_wifi_data);
            }
            Intent intent = new Intent("com.google.android.gms.backup.ACTION_BACKUP_NETWORK_SETTINGS_CHANGED").setPackage("com.google.android.gms");
            sendBroadcast(intent);
        });
    }


    /**
     * 打开选择备份账号的对话框
     */
    public void openChooseBackupAccountDialogs(View v) {
        Account[] accounts = mBackupAccountManager.getAccounts();
        if (accounts != null || accounts.length > 0) {
            ArrayList<String> strings = new ArrayList<>();
            for (Account account : accounts) {
                strings.add(account.name);
            }
            strings.add(getResources().getString(R.string.backup_settings_add_account));
            CharSequence[] charSequences = strings.toArray(new CharSequence[strings.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(R.string.backup_settings_choose_backup_account)
                    .setItems(charSequences, (dialogInterface, i) -> {
                        if (i == accounts.length) {
                            startActivity(new Intent("com.google.android.gms.auth.login.LOGIN"));
                        } else {
                            tvBackupAccountName.setText(accounts[i].name);
                            mBackupAccountManager.removeCurrentAccount();
                            mBackupAccountManager.setCurrentBackupAccount(accounts[i]);
                            mBackupAccountManager.initializeTokens();
                            new GetAppBackupInfoThread().start();
                            new GetStorageInfoThread().start();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);
        }
    }

    /**
     * 监听备份状态的线程类
     */
    private class BackupStateMonitorThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "BackupStateMonitorThread: enter");
            try {
                //延迟3秒开始循环查询
                Thread.sleep(3000);
                while (BackupTransportService.isBackingUp(BackupSettingsActivity.this)) {
                    Thread.sleep(1000);
                }
                //延迟3秒后重新获取备份信息，避免获取到的信息不是最新的
                Thread.sleep(3000);
                new GetAppBackupInfoThread().start();
                uiHandler.post(() -> {
                    btnBackupNow.setText(R.string.backup_settings_backup_now);
                    btnBackupNow.setClickable(true);
                });
            } catch (Exception e) {
                Log.e(TAG, "BackupStateMonitorThread: error", e);
            }
        }
    }


    /**
     * 清除备份数据的请求线程类
     * 当关闭 Google One 时,需请求清除云端备份的数据.
     */
    private class ClearBackupDataThread extends Thread {
        @Override
        public void run() {
            BackupResponse response;
            try {
                BackupHttpRequestUtil httpRequestUtil = getHttpRequestUtil();
                if (httpRequestUtil == null) {
                    return;
                }
                response = httpRequestUtil.clearBackupData();
                if (response != null) {
                    Log.d(TAG, "clearBackupDataResponse=> " + response.getCode());
                }
                new GetAppBackupInfoThread().start();
            } catch (IOException e) {
                Log.e(TAG, "ClearBackupDataThread error", e);
            }
        }
    }


    private BackupHttpRequestUtil getHttpRequestUtil() {
        long androidId = 0;
        String token = null;
        try {
            for (int i = 0; i < 5; i++) {
                androidId = mBackupAccountManager.getAndroidId();
                token = mBackupAccountManager.getAuthToken();
                if (androidId != 0 && token != null) {
                    break;
                } else {
                    Thread.sleep(3000);
                }
            }
            if (androidId != 0 && token != null) {
                return new BackupHttpRequestUtil(androidId, token);
            }
        } catch (Exception e) {
            Log.e(TAG, "GetAppBackupInfoThread error", e);
        }
        return null;
    }

    /**
     * 获取应用备份信息的请求线程类
     * 所返回的数据包含: 应用备份大小, 应用备份数量, 通话记录备份大小, 短信备份大小, 设备设置备份大小, 最后备份时间
     * 通过uiHandler更新当前Activity中"备份详情"中的内容
     */
    private class GetAppBackupInfoThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "GetAppBackupInfoThread run");
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    tvAppBackupDetail.setText(R.string.backup_settings_check_status);
                    tvPhotosBackupDetail.setText(R.string.backup_settings_check_status);
                    tvMessagesBackupDetail.setText(R.string.backup_settings_check_status);
                    tvCallHistoryBackupDetail.setText(R.string.backup_settings_check_status);
                    tvDeviceSettingBackupDetail.setText(R.string.backup_settings_check_status);
                }
            });

            BackupHttpRequestUtil backupHttpRequestUtil = getHttpRequestUtil();
            if (backupHttpRequestUtil == null) {
                return;
            }
            List<AppBackupDetail> appBackupInfoList = backupHttpRequestUtil.getAppBackupState();
            if (appBackupInfoList == null) {
                return;
            }
            int appNum = 0;
            long appSize = 0;
            long configSize = 0;
            long callLogSize = 0;
            long smsLogSize = 0;
            long lastBackupTimestamp = 0;
            for (AppBackupDetail appBackupInfo : appBackupInfoList) {
                String packageName = appBackupInfo.getPackageName();
                long appLastBackupTime = appBackupInfo.getLastBackupTimestamp();
                if (lastBackupTimestamp < appLastBackupTime) {
                    lastBackupTimestamp = appLastBackupTime;
                }
                Log.d(TAG, "AppBackupDetail: pkgName=>"
                        + packageName + " lastBackupTime=>"
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(appBackupInfo.getLastBackupTimestamp())));
                switch (packageName) {
                    case "@pm@":
                        break;
                    case "android":
                    case "com.android.providers.settings":
                        configSize += appBackupInfo.getKvBackupSize();
                        break;
                    case "com.android.calllogbackup":
                        callLogSize += appBackupInfo.getKvBackupSize();
                        break;
                    case "com.android.providers.telephony":
                        smsLogSize += appBackupInfo.getKvBackupSize();
                    case "com.android.providers.telephony.mms@g1":
                        smsLogSize += appBackupInfo.getKvBackupSize();
                        break;
                    default:
                        appSize += appBackupInfo.getKvBackupSize();
                        appSize += appBackupInfo.getFullBackupSize();
                        appNum += 1;
                }
            }

            Log.d(TAG, "GetAppBackupInfoThread: appSize=>" + appSize);
            Log.d(TAG, "GetAppBackupInfoThread: appNum=>" + appNum);
            Log.d(TAG, "GetAppBackupInfoThread: configSize=>" + configSize);
            Log.d(TAG, "GetAppBackupInfoThread: callLogSize=>" + callLogSize);
            Log.d(TAG, "GetAppBackupInfoThread: smsLogSize=>" + smsLogSize);
            Log.d(TAG, "GetAppBackupInfoThread: lastBackupTimestamp=>" + lastBackupTimestamp);

            String lastBackupTimeInfo;
            if (lastBackupTimestamp == 0) {
                lastBackupTimeInfo = getResources().getString(R.string.backup_settings_no_data_backed);
            } else {
                String info = getResources().getString(R.string.backup_settings_last_backup_time);
                String model = Build.MODEL;
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lastBackupTimestamp));
                lastBackupTimeInfo = String.format(info, model, time);
            }

            String appBackupInfo;
            if (appSize == 0) {
                appBackupInfo = getResources().getString(R.string.backup_settings_no_data_backed);
            } else {
                String size = formatStorageInfo(appSize);
                String num = String.valueOf(appNum);
                appBackupInfo = String.format(getResources().getString(R.string.backup_settings_apps_backup_detail), size, num);
            }

            String callHistoryBackupInfo;
            if (callLogSize == 0) {
                callHistoryBackupInfo = getResources().getString(R.string.backup_settings_no_data_backed);
            } else {
                callHistoryBackupInfo = formatStorageInfo(callLogSize);
            }

            String smsBackupInfo;
            if (smsLogSize == 0) {
                smsBackupInfo = getResources().getString(R.string.backup_settings_no_data_backed);
            } else {
                smsBackupInfo = formatStorageInfo(smsLogSize);
            }

            String configBackupInfo;
            if (configSize == 0) {
                configBackupInfo = getResources().getString(R.string.backup_settings_no_data_backed);
            } else {
                configBackupInfo = formatStorageInfo(configSize);
            }

            int photosApkState = getPhotosApkState();
            Log.d(TAG, "photosApkState=> " + photosApkState);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    tvAppBackupDetail.setText(appBackupInfo);
                    tvMessagesBackupDetail.setText(smsBackupInfo);
                    tvCallHistoryBackupDetail.setText(callHistoryBackupInfo);
                    tvDeviceSettingBackupDetail.setText(configBackupInfo);
                    tvLastBackupTime.setText(lastBackupTimeInfo);
                    switch (photosApkState) {
                        case -1:
                            tvPhotosBackupDetail.setText(R.string.backup_settings_photos_no_apk);
                            break;
                        case -2:
                            tvPhotosBackupDetail.setText(R.string.backup_settings_photos_need_update);
                            break;
                        case 0:
                            tvPhotosBackupDetail.setText(R.string.backup_settings_photos_has_error);
                            break;
                        case 1:
                            AutoBackupState autoBackupState = mPhotosBackupApiClient.getAutoBackupState();
                            if (autoBackupState != null) {
                                Log.d(TAG, "getAutoBackupState: autoBackupState=> " + autoBackupState.account_name);
                                String format = String.format(getResources().getString(R.string.backup_settings_photos_and_videos_backup_detail), autoBackupState.account_name);
                                tvPhotosBackupDetail.setText(format);
                            } else {
                                tvPhotosBackupDetail.setText(R.string.backup_settings_photos_auto_backup_disabled);
                            }
                            break;
                    }
                }
            });

        }
    }

    /**
     * 获取Google One 容量占用信息请求的线程类
     */
    private class GetStorageInfoThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "GetStorageInfoThread run");
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    String title = String.format(getResources().getString(R.string.backup_settings_last_backup_time),
                            Build.MODEL,
                            getResources().getString(R.string.backup_settings_check_status));
                    tvLastBackupTime.setText(title);
                    tvStorageUsedInfo.setText(R.string.backup_settings_check_status);
                }
            });

            try {
                String OAuthToken = null;
                for (int i = 0; i < 5; i++) {
                    OAuthToken = mBackupAccountManager.getOAuthToken();
                    if (OAuthToken != null) {
                        Log.d(TAG, "getOAuthToken=>" + OAuthToken);
                        break;
                    } else {
                        Thread.sleep(3000);
                    }
                }
                if (OAuthToken != null) {
                    GrpcAndroidPlatformBackupRestoreServiceClient apiServStub = getApiServStub(OAuthToken);
                    GetStorageQuotaInfoRequest getStorageQuotaInfoRequest = new GetStorageQuotaInfoRequest.Builder().build();
                    GetStorageQuotaInfoResponse getStorageQuotaInfoResponse;
                    getStorageQuotaInfoResponse = apiServStub.GetStorageQuotaInfo().executeBlocking(getStorageQuotaInfoRequest);
                    Log.d(TAG, "getStorageQuotaInfoResponse=>" + BackupHttpRequestUtil.bytesToHex(getStorageQuotaInfoResponse.encode()));
                    long totalSize;
                    long usedSize;
                    if (getStorageQuotaInfoResponse.totalSize == null) {
                        totalSize = 0L;
                    } else {
                        totalSize = getStorageQuotaInfoResponse.totalSize;
                    }
                    if (getStorageQuotaInfoResponse.totalUsedSize == null) {
                        usedSize = 0L;
                    } else {
                        usedSize = getStorageQuotaInfoResponse.totalUsedSize;
                    }
                    Log.d(TAG, "StorageTotalSize=>" + totalSize);
                    Log.d(TAG, "StorageUsedSize=>" + usedSize);
                    uiHandler.post(() -> {
                        String format = getResources().getString(R.string.backup_settings_storage_info);
                        String totalSizeStr = formatStorageInfo(totalSize);
                        String usedSizeStr = formatStorageInfo(usedSize);
                        String percent = String.format("%.1f", usedSize * 100.0 / totalSize);
                        String formatInfo = String.format(format, totalSizeStr, usedSizeStr, percent);
                        tvStorageUsedInfo.setText(formatInfo);
                        ProgressBar progressBar = findViewById(R.id.backup_settings_storage_used_progress_bar);
                        progressBar.setProgress((int) (usedSize * 100.0 / totalSize));
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "getOAuthToken error", e);
            }
        }
    }


    /**
     * 启动浏览器打开URL
     */
    private void openUrlWithDefaultBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }


    /**
     * Grpc 请求获取存储容量的占用信息
     */
    private GrpcAndroidPlatformBackupRestoreServiceClient getApiServStub(String token) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .callTimeout(1, TimeUnit.MINUTES)
                .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                .addInterceptor(new HeaderClientInterceptor("Bearer " + token, null))
                .build();

        GrpcClient grpcClient = new GrpcClient.Builder()
                .client(okHttpClient)
                .baseUrl("https://androidplatformbackuprestore-pa.googleapis.com:443")
                .minMessageToCompress(Long.MAX_VALUE)
                .build();

        return new GrpcAndroidPlatformBackupRestoreServiceClient(grpcClient);
    }

    /**
     * 启动设置中的账号同步页面
     * 兼容原生安卓及华为鸿蒙OS
     */
    private void openAccountSyncSettingsActivity() {
        Intent intent = new Intent("android.settings.ACCOUNT_SYNC_SETTINGS");
        intent.setPackage("com.android.settings");
        // 原生安卓
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", mBackupAccountManager.getCurrentBackupAccount());
        intent.putExtra(":settings:show_fragment_args", bundle);
        // 鸿蒙
        intent.putExtra("account", mBackupAccountManager.getCurrentBackupAccount());
        startActivity(intent);
    }

    /**
     * 通过 PendingIntent 启动 Photos Apk 设置中的备份页面
     */
    private void openPhotosBackupSettingsActivity() {
        PendingIntent photosBackupSettingsPreference = this.mPhotosBackupApiClient.getBackupPreferenceSettings();
        if (photosBackupSettingsPreference != null) {
            try {
                photosBackupSettingsPreference.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, "openPhotosBackupSettingsActivity: error", e);
            }
        }
    }


    /**
     * 检查PhotosApk版本是否符合最低版本需求
     */
    private boolean checkPhotosApkVersion(PackageInfo pkgInfo) {
        if (pkgInfo.versionCode < 40089058) {
            Log.d(TAG, "Google Photos is running an outdated version.");
            return false;
        }
        return true;
    }

    /**
     * 检查当前Google Photos版本号     *
     * @return 返回值: -1: 未安装Google Photos; -2: Google Photos版本过低; 0: 异常; 1: Google Photos版本符合最低版本需求
     */
    private int getPhotosApkState() {
        try {
            PackageInfo photosApkInfo = getPackageManager().getPackageInfo("com.google.android.apps.photos", 0x1000);
            if (photosApkInfo == null || photosApkInfo.requestedPermissions == null) {
                Log.e(TAG, "getPhotosApkState: Unable to get photos apk or its permissions info.");
                return -1;
            }

            if (!checkPhotosApkVersion(photosApkInfo)) {
                return -2;
            }
            return 1;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPhotosApkState: ", e);
        }
        return 0;
    }

    /**
     * 格式化存储容量信息
     */
    private String formatStorageInfo(long size) {
        int length = String.valueOf(size).length();
        String format;
        if (length <= 3) {
            format = String.valueOf(size) + " B";
        } else if (length <= 6) {
            format = String.format("%.1f", size / 1024.0) + " KB";
        } else if (length <= 9) {
            format = String.format("%.1f", size / 1024.0 / 1024.0) + " MB";
        } else {
            format = String.format("%.1f", size / 1024.0 / 1024.0 / 1024.0) + " GB";
        }
        return format;
    }


    /**
     * 当前Activity重新获取焦点时刷新相关信息
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: enter");

        mPhotosBackupApiClient = PhotosBackupApiClient.getInstance(this);
        mBackupManagerWrapper = BackupManagerWrapper.getInstance(this);
        mBackupAccountManager = BackupAccountsManager.getInstance(this);

        boolean backupIsEnabled = mBackupManagerWrapper.isBackupEnabled();
        if (backupIsEnabled) {
            sthGoogleOneBackup.setChecked(true);
        } else {
            sthGoogleOneBackup.setChecked(false);
        }

        // 返回当前页面时刷新相关信息
        boolean isBackingUp = BackupTransportService.isBackingUp(this);
        if (isBackingUp) {
            btnBackupNow.setText(R.string.backup_settings_backing_up);
            new BackupStateMonitorThread().start();
        } else {
            btnBackupNow.setText(R.string.backup_settings_backup_now);
        }

        if (backupIsEnabled == true && isBackingUp == false) {
            btnBackupNow.setClickable(true);
        } else {
            btnBackupNow.setClickable(false);
        }

        boolean isBackupUsingMobileData = this.mBackupSettingsSharedPrefs.getBoolean(BackupSettingsSharedPrefs.KEY_USE_MOBILE_DATA);
        Log.d(TAG, "onResume: " + this.mBackupSettingsSharedPrefs);
        Log.d(TAG, "onResume: isBackupUsingMobileData=>" + isBackupUsingMobileData);
        sthBackupUsingMobileData.setChecked(isBackupUsingMobileData);
        if (isBackupUsingMobileData) {
            tvBackupNetworkTips.setText(R.string.backup_settings_network_tips_use_mobile_data);
        } else {
            tvBackupNetworkTips.setText(R.string.backup_settings_network_tips_use_wifi_data);
        }

        Account currentBackupAccount = mBackupAccountManager.getCurrentBackupAccount();
        if (currentBackupAccount != null) {
            tvBackupAccountName.setText(currentBackupAccount.name);
            new GetAppBackupInfoThread().start();
            new GetStorageInfoThread().start();
        }
    }
}
