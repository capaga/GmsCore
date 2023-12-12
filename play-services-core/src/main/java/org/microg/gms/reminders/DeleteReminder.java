package org.microg.gms.reminders;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;

import com.google.android.gms.reminders.internal.IRemindersCallbacks;
import com.google.android.gms.reminders.model.TaskIdEntity;

import org.microg.gms.reminders.provider.RemindersProvider;
import org.microg.gms.reminders.sync.AssignInfo;
import org.microg.gms.reminders.sync.DeleteTaskRequest;
import org.microg.gms.reminders.sync.RemindersSyncAdapter;

import java.util.ArrayList;

public class DeleteReminder extends CreateReminder {
    private final TaskIdEntity taskId;

    public DeleteReminder(Context mcontext, Account maccount, IRemindersCallbacks callbacks, TaskIdEntity taskId) {
        super(mcontext, maccount, callbacks, null, null);
        this.taskId = taskId;
    }

    @Override
    public void remindersOperations(ArrayList<ContentProviderOperation> list) {
        String[] selectionArgs = new String[]{taskId.clientAssignedId, String.valueOf(accountId)};
        list.add(ContentProviderOperation.newAssertQuery(RemindersProvider.REMINDERS_URI)
                .withSelection("client_assigned_id=? AND account_id=? AND deleted=0", selectionArgs).withExpectedCount(1).build());
        list.add(ContentProviderOperation.newAssertQuery(RemindersProvider.REMINDERS_URI)
                .withSelection(("(client_assigned_id=? AND account_id=? AND deleted=0)AND(recurrence_master=1)"), selectionArgs).withExpectedCount(0).build());

        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted", 1);
        list.add(ContentProviderOperation.newUpdate(RemindersProvider.REMINDERS_URI).withValues(contentValues)
                .withSelection("client_assigned_id=? AND account_id=? AND deleted=0", selectionArgs).build());
    }

    @Override
    public void operation(ArrayList<ContentProviderOperation> list) {
        DeleteTaskRequest.Builder builder = InstanceRegistry.getInstance(DeleteTaskRequest.class).newBuilder();
        if (taskId != null) {
            AssignInfo assignInfo = createAssignInfo(taskId);
            if (builder.assignInfo.isEmpty()) {
                builder.assignInfo = new ArrayList<>();
            }
            builder.assignInfo.add(assignInfo);
        }
        builder.httpHeaderInfo(getHttpHeaderInfo());

        list.add(ContentProviderOperation.newInsert(RemindersProvider.OPERATIONS_URI)
                .withValue("account_id", accountId).withValue("operation_api", RemindersSyncAdapter.OPERATION_API_DELETE_REMINDER)
                .withValue("operation_request", builder.build().encode()).build());
    }
}
