package org.microg.gms.reminders;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.reminders.AccountState;
import com.google.android.gms.reminders.CreateReminderOptionsInternal;
import com.google.android.gms.reminders.LoadRemindersOptions;
import com.google.android.gms.reminders.ReindexDueDatesOptions;
import com.google.android.gms.reminders.UpdateRecurrenceOptions;
import com.google.android.gms.reminders.internal.IRemindersCallbacks;
import com.google.android.gms.reminders.internal.IRemindersService;
import com.google.android.gms.reminders.model.CustomizedSnoozePresetEntity;
import com.google.android.gms.reminders.model.TaskEntity;
import com.google.android.gms.reminders.model.TaskIdEntity;
import com.google.protobuf.InvalidProtocolBufferException;

import org.microg.gms.reminders.provider.RemindersProvider;

import java.util.List;

public class RemindersServiceImpl extends IRemindersService.Stub {
    private static final String TAG = RemindersServiceImpl.class.getSimpleName();
    private final Context mContext;
    private final Account mAccount;

    RemindersServiceImpl(Context context, Account account) {
        mContext = context;
        mAccount = account;
    }

    @Override
    public void loadReminders(IRemindersCallbacks callbacks, LoadRemindersOptions options) throws RemoteException {
        Log.d(TAG, "loadReminders ");
        Long accountId = getAccountId(mContext, mAccount.name);
        if (accountId == null) {
            Log.w(TAG, "loadReminders account id is null");
            return;
        }

        DataHolder dataHolder = LoadRemindersHelper.createDataHolder(options, mContext, accountId);

        callbacks.onDataHolder(dataHolder, Status.SUCCESS);
    }

    public static Long getAccountId(Context context, String accountName) {
        Long accountId = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor accountInfo = resolver.query(
                RemindersProvider.ACCOUNT_URI,
                new String[]{"_id", "account_name", "storage_version", "sync_status", "account_state"},
                "account_name=?",
                new String[]{accountName},
                null);

        if (accountInfo != null && accountInfo.getCount() > 0) {
            accountInfo.moveToFirst();
            accountId = accountInfo.getLong(0);
        }

        if (accountInfo != null) {
            accountInfo.close();
        }
        Log.d(TAG, "getAccountId accountId: " + accountId);
        return accountId;
    }


    @Override
    public void addListener(IRemindersCallbacks callbacks) throws RemoteException {
        Log.d(TAG, "unimplemented Method: addListener");

    }

    @Override
    public void createReminder(IRemindersCallbacks callbacks, TaskEntity task) throws RemoteException {
        Log.d(TAG, "unimplemented Method: createReminder");

    }

    @Override
    public void updateReminder(IRemindersCallbacks callbacks, TaskEntity task) throws RemoteException {
        Log.d(TAG, "unimplemented Method: updateReminder");

    }

    @Override
    public void deleteReminder(IRemindersCallbacks callbacks, TaskIdEntity taskId) throws RemoteException {
        Log.d(TAG, "unimplemented Method: deleteReminder");
        try {
            new DeleteReminder(mContext, mAccount, callbacks, taskId).exe();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bumpReminder(IRemindersCallbacks callbacks, TaskIdEntity taskId) throws RemoteException {
        Log.d(TAG, "unimplemented Method: bumpReminder");

    }

    @Override
    public void hasUpcomingReminders(IRemindersCallbacks callbacks) throws RemoteException {
        Log.d(TAG, "unimplemented Method: hasUpcomingReminders");

    }

    @Override
    public void createRecurrence(IRemindersCallbacks callbacks, TaskEntity task) throws RemoteException {
        Log.d(TAG, "unimplemented Method: createRecurrence");
        try {
            new CreateRecurrenceImpl(mContext, mAccount, callbacks, task).exe();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            Log.w(TAG, "createRecurrence: " + e);
        }
    }

    @Override
    public void updateRecurrence(IRemindersCallbacks callbacks, String s1, TaskEntity task, UpdateRecurrenceOptions options) throws RemoteException {
        Log.d(TAG, "unimplemented Method: updateRecurrence");

    }

    @Override
    public void deleteRecurrence(IRemindersCallbacks callbacks, String s1, UpdateRecurrenceOptions options) throws RemoteException {
        Log.d(TAG, "unimplemented Method: deleteRecurrence");
        try {
            new DeleteRecurrenceImpl(mContext, mAccount, callbacks, s1, options).exe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeRecurrence(IRemindersCallbacks callbacks, String s1, TaskEntity task, UpdateRecurrenceOptions options) throws RemoteException {
        Log.d(TAG, "unimplemented Method: changeRecurrence");
        try {
            new ChangeRecurrence(mContext, mAccount, callbacks, s1, task, options).exe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makeTaskRecurring(IRemindersCallbacks callbacks, TaskEntity task) throws RemoteException {
        Log.d(TAG, "unimplemented Method: makeTaskRecurring");

    }

    @Override
    public void makeRecurrenceSingleInstance(IRemindersCallbacks callbacks, String s1, TaskEntity task, UpdateRecurrenceOptions options) throws RemoteException {
        Log.d(TAG, "unimplemented Method: makeRecurrenceSingleInstance");

    }

    @Override
    public void clearListeners() throws RemoteException {
        Log.d(TAG, "unimplemented Method: clearListeners");

    }

    @Override
    public void batchUpdateReminders(IRemindersCallbacks callbacks, List<TaskEntity> tasks) {
        Log.d(TAG, "batchUpdateReminders");
        try {
            new BatchUpdateReminder(mContext, mAccount, callbacks, tasks).exe();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void createReminderWithOptions(IRemindersCallbacks callbacks, TaskEntity task, CreateReminderOptionsInternal options) throws RemoteException {
        Log.d(TAG, "createReminderWithOptions " + options);
        try {
            new CreateReminder(mContext, mAccount, callbacks, task, options).exe();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getCustomizedSnoozePreset(IRemindersCallbacks callbacks) throws RemoteException {
        Log.d(TAG, "unimplemented Method: getCustomizedSnoozePreset");

    }

    @Override
    public void setCustomizedSnoozePreset(IRemindersCallbacks callbacks, CustomizedSnoozePresetEntity preset) throws RemoteException {
        Log.d(TAG, "unimplemented Method: setCustomizedSnoozePreset");

    }

    @Override
    public void setAccountState(IRemindersCallbacks callbacks, AccountState accountState) throws RemoteException {
        Log.d(TAG, "unimplemented Method: setAccountState");

    }

    @Override
    public void getAccountState(IRemindersCallbacks callbacks) throws RemoteException {
        Log.d(TAG, "unimplemented Method: getAccountState");

    }

    @Override
    public void checkReindexDueDatesNeeded(IRemindersCallbacks callbacks, ReindexDueDatesOptions options) throws RemoteException {
        Log.d(TAG, "unimplemented Method: checkReindexDueDatesNeeded");

    }

    @Override
    public void reindexDueDates(IRemindersCallbacks callbacks, ReindexDueDatesOptions options) throws RemoteException {
        Log.d(TAG, "unimplemented Method: reindexDueDates");

    }
}
