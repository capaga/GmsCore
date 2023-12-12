package org.microg.gms.reminders;


import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.PeriodicSync;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.microg.gms.common.Constants;
import org.microg.gms.reminders.provider.RemindersProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccountListener implements OnAccountsUpdateListener {
    private static final String TAG = AccountListener.class.getSimpleName();
    private static final int SECONDS_IN_A_DAY = 86400;

    private final Context mContext;

    public AccountListener(Context context) {
        this.mContext = context;
    }


    @Override
    public void onAccountsUpdated(Account[] accounts) {
        List<Account> accountList = getAccount(mContext);
        boolean flag = false;
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] != null && !TextUtils.isEmpty(accounts[i].name) && ("com.google".equalsIgnoreCase(accounts[i].type))) {
                for (Account account : accountList) {
                    if (!account.name.equalsIgnoreCase(accounts[i].name)) {
                        continue;
                    }
                    flag = true;
                    break;
                }
                if (flag) {
                    flag = false;
                    continue;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("account_name", accounts[i].name);
                contentValues.put("morning_customized_time", 8 * 3600000L);
                contentValues.put("afternoon_customized_time", 13 * 3600000L);
                contentValues.put("evening_customized_time", 18 * 3600000L);
                this.mContext.getContentResolver().insert(RemindersProvider.ACCOUNT_URI, contentValues);


                Bundle bundle = new Bundle();
                bundle.putBoolean("initialize", true);
                ContentResolver.requestSync(accounts[i], RemindersProvider.AUTHORITY, bundle);

                ContentResolver.setIsSyncable(accounts[i], RemindersProvider.AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(accounts[i], RemindersProvider.AUTHORITY, true);

                Iterator<PeriodicSync> iterator = ContentResolver.getPeriodicSyncs(accounts[i], RemindersProvider.AUTHORITY).iterator();
                boolean isPeriodic = false;
                while (iterator.hasNext()) {
                    PeriodicSync periodicSync = iterator.next();
                    if (periodicSync.extras != null && (periodicSync.extras.containsKey("periodic")) && periodicSync.period == SECONDS_IN_A_DAY) {
                        isPeriodic = true;
                        continue;
                    }
                    mContext.getContentResolver();
                    ContentResolver.removePeriodicSync(accounts[i], RemindersProvider.AUTHORITY, periodicSync.extras);
                }
                if (isPeriodic) {
                    bundle.clear();
                    bundle.putBoolean("periodic", true);
                    mContext.getContentResolver();
                    ContentResolver.addPeriodicSync(accounts[i], RemindersProvider.AUTHORITY, bundle, SECONDS_IN_A_DAY);
                }

                bundle.clear();
                bundle.putBoolean("expedited", true);
                bundle.putBoolean("force", true);
                bundle.putBoolean("reminders_initialization_sync", true);
                ContentResolver.requestSync(accounts[i], RemindersProvider.AUTHORITY, bundle);
            }

        }

    }


    private static List<Account> getAccount(Context mcontext) {
        List<Account> accountList = new ArrayList<>();
        Cursor cursor = mcontext.getContentResolver().query(RemindersProvider.ACCOUNT_URI
                , new String[]{"_id", "account_name"}, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    accountList.add(new Account(cursor.getString(1), "com.google"));
                }
            } catch (Throwable throwable) {
                Log.d(TAG, "getAccount: " + throwable);
                throw throwable;
            } finally {
                cursor.close();
            }
            return accountList;
        }

        return accountList;
    }

}
