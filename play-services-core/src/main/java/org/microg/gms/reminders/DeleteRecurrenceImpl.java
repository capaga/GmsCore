package org.microg.gms.reminders;


import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.android.gms.reminders.UpdateRecurrenceOptions;
import com.google.android.gms.reminders.internal.IRemindersCallbacks;

import org.microg.gms.reminders.provider.RemindersProvider;
import org.microg.gms.reminders.sync.DeleteRecurrenceRequest;
import org.microg.gms.reminders.sync.RecurrenceIdInfo;
import org.microg.gms.reminders.sync.RemindersSyncAdapter;

import java.util.ArrayList;

public class DeleteRecurrenceImpl extends CreateReminder {
    private final String recurrenceId;
    private final UpdateRecurrenceOptions options;

    public DeleteRecurrenceImpl(Context context, Account account, IRemindersCallbacks callbacks, String recurrenceId, UpdateRecurrenceOptions options) {
        super(context, account, callbacks, null, null);
        this.recurrenceId = recurrenceId;
        this.options = options;
    }

    @Override
    public void operation(ArrayList<ContentProviderOperation> list){
        DeleteRecurrenceRequest.Builder builder = new DeleteRecurrenceRequest.Builder();
        RecurrenceIdInfo.Builder recurrenceIdBuild = new RecurrenceIdInfo.Builder()
                .recurrenceId(recurrenceId);

        builder.recurrenceIdInfo(recurrenceIdBuild.build());

        if (options != null) {
            builder.recurrenceOptions(getRecurrenceOptions(options));
        }

        builder.httpHeaderInfo(getHttpHeaderInfo());

        list.add(ContentProviderOperation.newInsert(RemindersProvider.OPERATIONS_URI)
                .withValue("account_id", accountId).withValue("operation_api", RemindersSyncAdapter.OPERATION_API_DELETE_RECURRENCE_REMINDER)
                .withValue("operation_request", builder.build().encode()).build());
    }

    @Override
    public void remindersOperations(ArrayList<ContentProviderOperation> list) {
        String selection = getSelectionByOptions(options);
        String[] selectionArgs = getSelectionArgs(recurrenceId, options);
        updateRecurrenceById(list, recurrenceId, options);
        list.add(ContentProviderOperation.newUpdate(RemindersProvider.REMINDERS_URI)
                .withValue("deleted", 1).withSelection(selection, selectionArgs).build());
    }

}
