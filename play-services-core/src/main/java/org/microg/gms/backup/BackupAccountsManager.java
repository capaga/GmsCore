package org.microg.gms.backup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.microg.gms.auth.AuthManager;
import org.microg.gms.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BackupAccountsManager {
    private static final String TAG = "BackupAccountsManager";
    private final Context mContext;
    private final String ACCOUNT_TYPE = "com.google";
    private final String OAUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/android_platform_backup_restore";
    private final String AUTH_TOKEN_TYPE = "android";
    private static BackupAccountsManager mBackupAccountsManager = null;
    private final DataStorageUtil mDataStorageUtil;
    private String authToken;
    private String OAuthToken;

    private Object lock = new Object();;

    private BackupAccountsManager(Context context) {
        mContext = context;
        mDataStorageUtil = new DataStorageUtil();
    }

    public static BackupAccountsManager getInstance(Context context) {
        synchronized (BackupAccountsManager.class) {
            if (mBackupAccountsManager == null) {
                mBackupAccountsManager = new BackupAccountsManager(context);
            }
            return mBackupAccountsManager;
        }
    }

    public Account getCurrentBackupAccount() {
        synchronized (mDataStorageUtil) {
            try {
                if (mDataStorageUtil.isExits()) {
                    Map<String, String> loadData = mDataStorageUtil.loadData();
                    String accountName = loadData.get("accountName");
                    String accountType = loadData.get("accountType");
                    if (accountName != null && accountType != null) {
                        Log.d(TAG, "getCurrentBackupAccount: accountName=> " + accountName);
                        return new Account(accountName, accountType);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getCurrentBackupAccount: error", e);
            }
            return null;
        }
    }

    public void setCurrentBackupAccount(Account account) {
        synchronized (mDataStorageUtil) {
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("accountName", account.name);
                hashMap.put("accountType", account.type);
                mDataStorageUtil.storeData(hashMap);
                Log.d(TAG, "setCurrentBackupAccount: setCurrentAccountName=> " + account.name);
            } catch (Exception e) {
                Log.e(TAG, "setCurrentBackupAccount: error", e);
            }
        }
    }

    public Account[] getAccounts() {
        return AccountManager.get(mContext).getAccountsByType(ACCOUNT_TYPE);
    }

    public long getAndroidId() {
        long androidId = getAndroidIdInternal();
        return androidId;
    }

    private long getAndroidIdInternal() {
        try {
            Class LastCheckinInfo = Class.forName("org.microg.gms.checkin.LastCheckinInfo");
            Method declaredFields = LastCheckinInfo.getDeclaredMethod("read", Context.class);
            declaredFields.setAccessible(true);
            Object invoke = declaredFields.invoke(null, mContext);
            Method getAndroidId = LastCheckinInfo.getDeclaredMethod("getAndroidId");
            Object androidID = getAndroidId.invoke(invoke);
            return (Long) androidID;
        } catch (Exception e) {
            Log.e(TAG, "getAndroidId error", e);
        }
        return 0;
    }

    private String getAuthToken(Account account) {
        this.authToken = new AuthManager(mContext, account.name, Constants.GMS_PACKAGE_NAME, AUTH_TOKEN_TYPE, null)
                .getAuthToken();
        if (this.authToken == null || this.authToken.isEmpty()) {
            initToken(account, AUTH_TOKEN_TYPE);
        }
        return this.authToken;
    }

    private class GetTokenRunnable implements Runnable {
        private Account account;
        private String tokenType;
        public GetTokenRunnable(Account account, String authTokenType) {
            this.account = account;
            this.tokenType = authTokenType;
        }

        @Override
        public void run() {
            try {
                synchronized (lock) {
                    AccountManager manager = AccountManager.get(mContext);
                    AccountManagerFuture<Bundle> future = manager.getAuthToken(account, this.tokenType, true, null, null);
                    switch (tokenType){
                        case AUTH_TOKEN_TYPE:
                            authToken = future.getResult(60, TimeUnit.SECONDS).getString(AccountManager.KEY_AUTHTOKEN);
                            break;
                        case OAUTH_TOKEN_TYPE:
                            OAuthToken = future.getResult(60, TimeUnit.SECONDS).getString(AccountManager.KEY_AUTHTOKEN);
                            break;
                    }
                    lock.notify();
                }
            } catch (Exception e) {
                Log.e(TAG, "initAuthToken: error", e);
            }
        }
    }

    private void initToken(Account account, String authTokenType) {
            try {
                Thread thread = new Thread(new GetTokenRunnable(account, authTokenType));
                thread.start();
                synchronized (lock){
                    lock.wait();
                }
            } catch (Exception e) {
                Log.e(TAG, "initToken: error", e);
            }
    }

    private String getOAuthToken(Account account) {
        this.OAuthToken = new AuthManager(mContext, account.name,
                Constants.GMS_PACKAGE_NAME,
                OAUTH_TOKEN_TYPE, null)
                .getAuthToken();
        if (this.OAuthToken == null || this.OAuthToken.isEmpty()) {
            initToken(account, OAUTH_TOKEN_TYPE);
        }
        return this.OAuthToken;
    }



    public String getOAuthToken() {
        String OAuthToken = null;
        Account account = getCurrentBackupAccount();
        if (account != null) {
            OAuthToken = getOAuthToken(account);
        }
        Log.d(TAG, "getOAuthToken=>" + OAuthToken);
        return OAuthToken;
    }

    public String getAuthToken() {
        String authToken = null;
        Account account = getCurrentBackupAccount();
        if (account != null) {
            authToken = getAuthToken(account);
        }
        Log.d(TAG, "getAuthToken=>" + authToken);
        return authToken;
    }

    public void removeCurrentAccount() {
        synchronized (mDataStorageUtil) {
            mDataStorageUtil.deleteFile();
        }
    }

    public void setInitializeState(boolean initialized) {
        synchronized (mDataStorageUtil) {
            try {
                if (initialized) {
                    initializeTokens();
                    mDataStorageUtil.addData("initialized", "true");
                } else {
                    mDataStorageUtil.removeData("initialized");
                }
            } catch (Exception e) {
                Log.e(TAG, "setInitializeState: error", e);
            }
        }
    }

    public boolean getInitializeState() {
        synchronized (mDataStorageUtil) {
            try {
                if (!mDataStorageUtil.isExits()) {
                    return false;
                }
                Map<String, String> loadData = mDataStorageUtil.loadData();
                String initialized = loadData.get("initialized");
                if (initialized != null && initialized.equals("true")) {
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "getInitializeState: error", e);
            }
            return false;
        }
    }

    private boolean getInitializeTokenState() {
        synchronized (mDataStorageUtil) {
            try {
                if (!mDataStorageUtil.isExits()) {
                    return false;
                }
                Map<String, String> loadData = mDataStorageUtil.loadData();
                String initialized = loadData.get("initializedTokens");
                if (initialized != null && initialized.equals("true")) {
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "getInitializeTokenState: error", e);
            }
            return false;
        }
    }


    public void initializeTokens() {
        synchronized (mDataStorageUtil) {
            try {
                if (getInitializeTokenState() == false) {
                    getAndroidId();
                    getOAuthToken();
                    getAuthToken();
                    mDataStorageUtil.addData("initializedTokens", "true");
                }
            } catch (Exception e) {
                Log.e(TAG, "initializeTokens: error", e);
            }
        }
    }

    private class DataStorageUtil {
        private static final String DATA_FILE = "/BackupAccount.txt";

        // 存储Map到文件
        public synchronized void storeData(Map<String, String> data) throws IOException {
            synchronized (DataStorageUtil.class) {
                File file = new File(mContext.getFilesDir() + DATA_FILE);
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(data);
                oos.close();
            }
        }

        // 从文件读取Map
        public synchronized Map<String, String> loadData() throws IOException, ClassNotFoundException {
            File file = new File(mContext.getFilesDir() + DATA_FILE);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map<String, String> data = (Map<String, String>) ois.readObject();
            ois.close();
            return data;
        }

        public synchronized void deleteFile() {

            File file = new File(mContext.getFilesDir() + DATA_FILE);
            file.delete();

        }

        public synchronized boolean isExits() {
            File file = new File(mContext.getFilesDir() + DATA_FILE);
            return file.exists();
        }

        public synchronized void addData(String key, String value) throws IOException, ClassNotFoundException {
            Map<String, String> data = loadData();
            data.put(key, value);
            storeData(data);
        }

        public synchronized void removeData(String key) throws IOException, ClassNotFoundException {
            if (isExits()) {
                Map<String, String> data = loadData();
                if (data.containsKey(key)) {
                    data.remove(key);
                    storeData(data);
                }
            }
        }
    }
}
