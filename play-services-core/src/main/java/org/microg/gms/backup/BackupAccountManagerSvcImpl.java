package org.microg.gms.backup;

import android.accounts.Account;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.backup.IBackupAccountManagerService;

public class BackupAccountManagerSvcImpl extends IBackupAccountManagerService.Stub {
    private static final String TAG = "BackupAccountManagerSvc";

    private final BackupAccountsManager accountsManager;
    private final Context context;

    public BackupAccountManagerSvcImpl(Context context) {
        this.context = context;
        this.accountsManager =  BackupAccountsManager.getInstance(context);
    }

    @Override
    public Account getAccount() throws RemoteException {
        Log.d(TAG, "getAccount: called");
        return accountsManager.getCurrentBackupAccount();
    }

    @Override
    public void setAccount(Account account) throws RemoteException {
        Log.d(TAG, "setAccount: called");
        accountsManager.setCurrentBackupAccount(account);
    }

    @Override
    public boolean isServiceEnabled() throws RemoteException {
        Log.d(TAG, "isServiceEnabled: called");
        BackupManagerWrapper backupManager = BackupManagerWrapper.getInstance(this.context);
        return backupManager.isBackupEnabled();
    }
}
