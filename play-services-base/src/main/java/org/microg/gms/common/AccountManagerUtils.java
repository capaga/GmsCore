package org.microg.gms.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AccountManagerUtils {
    private static volatile AccountManagerUtils instance;
    private final AccountManager accountManager;
    private static final String PREF_NAME = "google_account_cache";
    private static final String KEY_ACCOUNT_PREFIX = "default_google_account_";
    public static final String GOOGLE_ACCOUNT_TYPE = "com.google";

    public static final String FIRST_NAME = "firstName";

    public static final String LAST_NAME = "lastName";
    public static final String GOOGLE_USER_ID = "GoogleUserId";
    private final SharedPreferences sharedPreferences;

    private AccountManagerUtils(Context context) {
        accountManager = AccountManager.get(context);
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static AccountManagerUtils getInstance(Context context) {

        if (instance == null) {
            synchronized (AccountManagerUtils.class) {
                instance = new AccountManagerUtils(context);
            }
        }
        return instance;
    }

    public void saveDefaultAccount(String pkgName, Account account) {
        if (TextUtils.isEmpty(pkgName) || account == null || !account.type.equals(GOOGLE_ACCOUNT_TYPE))
            return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCOUNT_PREFIX + pkgName, account.name);
        editor.apply();
    }

    public Account getDefaultAccount(String pkgName) {
        if (TextUtils.isEmpty(pkgName))
            return null;
        String name = sharedPreferences.getString(KEY_ACCOUNT_PREFIX + pkgName, null);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Account[] accounts = accountManager.getAccountsByType(GOOGLE_ACCOUNT_TYPE);
        for (Account account : accounts) {
            if (account.name.equals(name))
                return account;
        }
        return null;
    }

    public boolean isLogin() {
        Account[] accountsByType = getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accountsByType != null && accountsByType.length > 0) {
            return true;
        }
        return false;
    }


    public Account[] getAccountsByType(String accountType) {
        Account[] accountsByType = accountManager.getAccountsByType(accountType);
        return accountsByType;
    }

    public String peekAuthToken(Account account, String accountType) {
        String token = accountManager.peekAuthToken(account, accountType);
        return token;
    }


    public String getUserData(Account account, String type) {
        String accountsByType = accountManager.getUserData(account, type);
        return accountsByType;
    }


}
