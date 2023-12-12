package org.microg.gms.reminders;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.reminders.UpdateRecurrenceOptions;
import com.google.android.gms.reminders.internal.IRemindersCallbacks;
import com.google.android.gms.reminders.model.TaskEntity;

import org.microg.gms.reminders.provider.RemindersProvider;
import org.microg.gms.reminders.sync.ChangeRecurrenceRequest;
import org.microg.gms.reminders.sync.RecurrenceData;
import org.microg.gms.reminders.sync.RecurrenceIdInfo;
import org.microg.gms.reminders.sync.RecurrenceInfo;
import org.microg.gms.reminders.sync.RecurrenceOptions;
import org.microg.gms.reminders.sync.ReminderInfo;
import org.microg.gms.reminders.sync.RemindersSyncAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class ChangeRecurrence extends CreateRecurrenceImpl {
    private final String recurrenceId;
    private final UpdateRecurrenceOptions UpdateRecurOption;

    public ChangeRecurrence(Context context, Account account, IRemindersCallbacks callbacks, String recurrenceId, TaskEntity task, UpdateRecurrenceOptions options) {
        super(context, account, callbacks, task);
        this.recurrenceId = recurrenceId;
        this.UpdateRecurOption = options;
    }

    @Override
    public void operation(ArrayList<ContentProviderOperation> list) {
        ChangeRecurrenceRequest.Builder builder = new ChangeRecurrenceRequest.Builder();
        RecurrenceIdInfo changeRecurrenceIdInfo = createRecurrenceIdInfo(recurrenceId);
        if (changeRecurrenceIdInfo != null) {
            builder.changeRecurrenceIdInfo(changeRecurrenceIdInfo);
        }

        RecurrenceIdInfo taskRecurrenceIdInfo = createRecurrenceIdInfo(task.recurrenceInfoEntity.recurrenceId);
        if (taskRecurrenceIdInfo != null) {
            builder.taskRecurrenceIdInfo(taskRecurrenceIdInfo);
        }

        ReminderInfo.Builder reminderInfoBuilder;
        try {
            reminderInfoBuilder = createReminderInfo(task).newBuilder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RecurrenceInfo recurrenceInfo = reminderInfoBuilder.recurrenceInfo;
        if (recurrenceInfo == null) {
            recurrenceInfo = new RecurrenceInfo();
        }

        builder.recurrenceData(recurrenceInfo.recurrenceData == null ? new RecurrenceData(): recurrenceInfo.recurrenceData);
        clearReminderInfo(reminderInfoBuilder);
        builder.reminderInfo(reminderInfoBuilder.build());

        RecurrenceOptions recurrenceOptions = getRecurrenceOptions(this.UpdateRecurOption);
        if (recurrenceOptions != null) {
            builder.recurrenceOptions(recurrenceOptions);
        }

        builder.httpHeaderInfo(getHttpHeaderInfo());

        list.add(ContentProviderOperation.newInsert(RemindersProvider.OPERATIONS_URI)
                .withValue("account_id", accountId).withValue("operation_api", RemindersSyncAdapter.OPERATION_API_UPDATE_RECURRENCE_REMINDER)
                .withValue("operation_request", builder.build().encode()).build());

    }

    @Override
    public void remindersOperations(ArrayList<ContentProviderOperation> list) {
        list.add(ContentProviderOperation.newUpdate(RemindersProvider.REMINDERS_URI).withValue("deleted", 1)
                .withSelection(getSelectionByOptions(this.UpdateRecurOption), getSelectionArgs(this.recurrenceId, this.UpdateRecurOption)).build());
        updateRecurrenceById(list, this.recurrenceId, this.UpdateRecurOption);
        super.remindersOperations(list);
    }
}
