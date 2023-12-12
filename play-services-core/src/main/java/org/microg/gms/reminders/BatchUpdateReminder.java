package org.microg.gms.reminders;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;

import com.google.android.gms.reminders.internal.IRemindersCallbacks;
import com.google.android.gms.reminders.model.TaskEntity;

import org.microg.gms.reminders.provider.RemindersProvider;
import org.microg.gms.reminders.sync.AssignInfo;
import org.microg.gms.reminders.sync.BatchParam;
import org.microg.gms.reminders.sync.ReminderInfo;
import org.microg.gms.reminders.sync.RemindersSyncAdapter;
import org.microg.gms.reminders.sync.UpdateTaskRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchUpdateReminder extends CreateReminder {
    private final List<TaskEntity> tasks;

    public BatchUpdateReminder(Context mcontext, Account maccount, IRemindersCallbacks callbacks, List<TaskEntity> tasks) {
        super(mcontext,maccount,callbacks,null,null);
        this.tasks = tasks;
    }

    @Override
    public void remindersOperations(ArrayList<ContentProviderOperation> list) {
        for (TaskEntity taskEntity : this.tasks) {
            ContentValues contentValues = new ContentValues();
            handleTask(contentValues,taskEntity);
            contentValues.remove("due_date_millis");
            contentValues.remove("fired_time_millis");
            contentValues.put("dirty_sync_bit", Boolean.FALSE);

            contentValues.remove("client_assigned_id");
            contentValues.remove("client_assigned_thread_id");
            contentValues.remove("task_list");
            contentValues.remove("created_time_millis");
            contentValues.put("snoozed", snoozedCheck(taskEntity, false));

            String[] ids = new String[]{taskEntity.taskId.clientAssignedId, String.valueOf(this.accountId)};

            list.add(ContentProviderOperation.newAssertQuery(RemindersProvider.REMINDERS_URI)
                    .withSelection("client_assigned_id=? AND account_id=? AND deleted=0", ids).withExpectedCount(1).build());
            list.add(ContentProviderOperation.newAssertQuery(RemindersProvider.REMINDERS_URI)
                    .withSelection("(client_assigned_id=? AND account_id=? AND deleted=0) AND (recurrence_master=1)", ids).withExpectedCount(0).build());
            list.add(ContentProviderOperation.newUpdate(RemindersProvider.REMINDERS_MARK_EXCEPTIONAL_URI)
                    .withValues(contentValues).withSelection("client_assigned_id=? AND account_id=? AND deleted=0", ids).build());
        }
    }

    @Override
    public void operation(ArrayList<ContentProviderOperation> list) {
        for (TaskEntity task : tasks) {
            UpdateTaskRequest.Builder builder = InstanceRegistry.getInstance(UpdateTaskRequest.class).newBuilder();
            ReminderInfo reminderInfo;
            try {
                reminderInfo = createReminderInfo(task);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(reminderInfo != null) {
                builder.reminderInfo(reminderInfo);
            }

            reminderInfo = builder.reminderInfo;
            AssignInfo assignInfo = (reminderInfo == null || reminderInfo.assignInfo == null) ?
                    InstanceRegistry.getInstance(AssignInfo.class) : reminderInfo.assignInfo;
            builder.assignInfo(assignInfo);
            builder.httpHeaderInfo(getHttpHeaderInfo());
            builder.batchParam(InstanceRegistry.getInstance(BatchParam.class).newBuilder()
                    .batchIntList(new ArrayList<>((Arrays.asList(0,1,3,4,5,6,7,8,10,11,13,14,15,16))))
                    .build());
            builder.e(false);
            list.add(ContentProviderOperation.newInsert(RemindersProvider.OPERATIONS_URI)
                    .withValue("account_id", accountId).withValue("operation_api", RemindersSyncAdapter.OPERATION_API_UPDATE_REMINDER)
                    .withValue("operation_request", builder.build().encode()).build());
        }
    }

}
