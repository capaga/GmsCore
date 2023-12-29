package org.microg.gms.backup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackupAccountsReceiver extends BroadcastReceiver {
    private static final String TAG = "BackupAccountReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION)) {
            PendingResult pendingResult = goAsync();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BackupAccountsManager backupAccountManager = BackupAccountsManager.getInstance(context);
                    Account currentBackupAccount = backupAccountManager.getCurrentBackupAccount();
                    Account[] accounts = backupAccountManager.getAccounts();
                    if (currentBackupAccount == null) {
                        if (accounts != null && accounts.length > 0) {
                            backupAccountManager.setCurrentBackupAccount(accounts[0]);
                            backupAccountManager.initializeTokens();
                        }
                    } else {
                        boolean isExist = false;
                        for (Account account : accounts) {
                            if (currentBackupAccount.equals(account)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            backupAccountManager.removeCurrentAccount();
                            if (accounts != null && accounts.length > 0) {
                                backupAccountManager.setCurrentBackupAccount(accounts[0]);
                                backupAccountManager.initializeTokens();
                            }
                        }
                    }
                    pendingResult.finish();
                }
            }).start();
        }
    }
}
