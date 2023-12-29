package org.microg.gms.backup.G1Backup;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;

import androidx.work.ListenableWorker;

import com.google.android.gms.backup.BackupOptInSettings;
import com.google.android.gms.backup.IBackUpMmsClientCallbacks;
import com.google.android.gms.backup.internal.IBackUpNowCallbacks;
import com.google.android.gms.backup.internal.IG1BackupService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.common.internal.GetServiceRequest;

import org.microg.common.beans.PeopleResponseInfo;
import org.microg.gms.backup.BackupAccountsManager;
import org.microg.gms.backup.BackupManagerWrapper;
import org.microg.gms.backup.BackupSettingsSharedPrefs;

public class G1BackupServiceImpl extends IG1BackupService.Stub {
    private static final String TAG = "G1BackupServiceImpl";
    private final Context context;
    private final BackupManagerWrapper backupManager;
    private final BackupSettingsSharedPrefs backupSettingsSharedPrefs;
    private final BackupAccountsManager backupAccountsManager;
    public G1BackupServiceImpl(Context context, GetServiceRequest request) {
        this.context = context;
        this.backupManager = BackupManagerWrapper.getInstance(this.context);
        this.backupSettingsSharedPrefs = new BackupSettingsSharedPrefs(this.context.getApplicationContext());
        this.backupAccountsManager = BackupAccountsManager.getInstance(this.context);
    }

    @Override
    public void setMMSBackupState(boolean enable, String accountName) throws RemoteException {
        String key = String.format("%s_%s", BackupSettingsSharedPrefs.KEY_MMS_BACKUP_STATE, accountName);
        this.backupSettingsSharedPrefs.setValue(key, enable);
    }

    @Override
    public boolean isBackupEnabled() throws RemoteException {
        boolean result = this.backupManager.isBackupEnabled();
        Log.d(TAG, "isBackupEnabled: " + result);
        return result;
    }

    @Override
    public boolean isSMSBackupEnable() throws RemoteException {
        Account currentBackupAccount = this.backupAccountsManager.getCurrentBackupAccount();
        boolean result = false;
        if (currentBackupAccount != null) {
            String key = String.format("%s_%s", BackupSettingsSharedPrefs.KEY_MMS_BACKUP_STATE, currentBackupAccount.name);
            result = this.backupSettingsSharedPrefs.getBoolean(key);
        }
        Log.d(TAG, "isSMSBackupEnable: " + result);
        return result;
    }

    @Override
    public boolean isMMSBackupEnabled() throws RemoteException {
        Account currentBackupAccount = this.backupAccountsManager.getCurrentBackupAccount();
        boolean result = false;
        if (currentBackupAccount != null) {
            String key = String.format("%s_%s", BackupSettingsSharedPrefs.KEY_MMS_BACKUP_STATE, currentBackupAccount.name);
            result = this.backupSettingsSharedPrefs.getBoolean(key);
        }
        Log.d(TAG, "isMMSBackupEnabled: " + result);
        return result;
    }

    @Override
    public void starMMSBackup(IBackUpNowCallbacks callback) throws RemoteException {
        Log.d(TAG, "starMMSBackup: called");
        this.backupManager.backupNow();
        callback.setStatus(Status.SUCCESS);
    }

    @Override
    public boolean checkAvailableRestoreToken() throws RemoteException {
        Log.w(TAG, "Method 'checkAvailableRestoreToken' not yet implement.");
        return false;
    }

    @Override
    public void setMMSUesMobileData(boolean useMobileData) throws RemoteException {
        Log.d(TAG, "setMMSUesMobileData: called");
        setUseMobileData(useMobileData);
    }

    @Override
    public void setUseMobileData(boolean useMobileData) throws RemoteException {
        Log.d(TAG, "setUseMobileData: useMobileData=>" + useMobileData);
        this.backupSettingsSharedPrefs.setValue(BackupSettingsSharedPrefs.KEY_USE_MOBILE_DATA, useMobileData);
    }

    @Override
    public boolean isUseMobileDataForMms() throws RemoteException {
        Log.d(TAG, "isUseMobileDataForMms: called");
        return isUseMobileData();
    }

    @Override
    public boolean isUseMobileData() throws RemoteException {
        boolean result = this.backupSettingsSharedPrefs.getBoolean(BackupSettingsSharedPrefs.KEY_USE_MOBILE_DATA);
        Log.d(TAG, "isUseMobileData: " + result);
        return result;
    }

    @Override
    public void putBackUpMmsClientCallbacks(String name, IBackUpMmsClientCallbacks callbacks) throws RemoteException {
        Log.w(TAG, "Method 'putBackUpMmsClientCallbacks' not yet implement.");
    }

    @Override
    public void removeBackUpMmsClientCallbacks(String name) throws RemoteException {
        Log.w(TAG, "Method 'removeBackUpMmsClientCallbacks' not yet implement. Args.name=>" + name);
    }

    @Override
    public int getBackupDeviceState() throws RemoteException {
        Log.w(TAG, "Method 'getBackupDeviceState' not yet implement");
        return 0;
    }

    @Override
    public long getLastFullBackupPassTimeMs() throws RemoteException {
        Log.w(TAG, "Method 'getLastFullBackupPassTimeMs' not yet implement");
        return 0;
    }

    @Override
    public void setBackupEnable(IStatusCallback callback, boolean z, String accountName, BackupOptInSettings backupOptInSettings) throws RemoteException {
        Log.w(TAG, "Method 'setBackupEnable' not yet implement");
    }
}

