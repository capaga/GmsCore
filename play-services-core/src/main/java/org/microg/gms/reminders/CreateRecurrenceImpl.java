package org.microg.gms.reminders;


import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.reminders.internal.IRemindersCallbacks;
import com.google.android.gms.reminders.model.CustomizedSnoozePresetEntity;
import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.RecurrenceEndEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;
import com.google.android.gms.reminders.model.TaskEntity;
import com.google.android.gms.reminders.model.TaskIdEntity;
import com.google.android.gms.reminders.model.TimeEntity;
import com.google.protobuf.InvalidProtocolBufferException;

import org.microg.gms.reminders.provider.RemindersProvider;
import org.microg.gms.reminders.sync.CreateRecurrenceRequest;
import org.microg.gms.reminders.sync.RecurrenceInfo;
import org.microg.gms.reminders.sync.ReminderInfo;
import org.microg.gms.reminders.sync.RemindersSyncAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CreateRecurrenceImpl extends CreateReminder {
    private static final String TAG = CreateRecurrenceImpl.class.getSimpleName();
    private CustomizedSnoozePresetEntity customizedSnoozePreset;

    private static final int DAILY_LOCAL_EXPANSION_DAYS = 31;
    private static final int WEEKLY_LOCAL_EXPANSION_DAYS = 62;
    private static final int MONTHLY_LOCAL_EXPANSION_DAYS = 62;
    private static final int YEARLY_LOCAL_EXPANSION_DAYS = 730;

    public CreateRecurrenceImpl(Context context, Account account, IRemindersCallbacks callbacks, TaskEntity task) {
        super(context, account, callbacks, task, null);
    }

    @Override
    public void exe() throws InvalidProtocolBufferException {
        List<CustomizedSnoozePresetEntity> customizedSnoozePresetEntityList = getCustomizedSnoozePresetInternal();
        if (customizedSnoozePresetEntityList.size() > 0) {
            this.customizedSnoozePreset = customizedSnoozePresetEntityList.get(0);
        }
        Log.d(TAG, "CreateRecurrenceImpl exe: ");
        super.exe();
    }

    @Override
    public void operation(ArrayList<ContentProviderOperation> list) {
        Log.d(TAG, "operation: ");
        CreateRecurrenceRequest.Builder builder = new CreateRecurrenceRequest.Builder();
        ReminderInfo reminderInfo = null;
        try {
            reminderInfo = createReminderInfo(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ReminderInfo.Builder reminderInfoBuilder = reminderInfo.newBuilder();

        RecurrenceInfo recurrenceInfo = reminderInfoBuilder.recurrenceInfo;

        if (recurrenceInfo != null) {
            builder.recurrenceData(recurrenceInfo.recurrenceData);
            builder.recurrenceIdInfo(recurrenceInfo.recurrenceIdInfo);
        }


        builder.taskListInfo(reminderInfoBuilder.taskListInfo);

        builder.httpHeaderInfo(getHttpHeaderInfo());

        clearReminderInfo(reminderInfoBuilder);

        builder.reminderInfo(reminderInfoBuilder.build());
        list.add(ContentProviderOperation.newInsert(RemindersProvider.OPERATIONS_URI).withValue("account_id", accountId)
                .withValue("operation_api", 2).withValue("operation_request", builder.build().encode()).build());
    }

    protected void clearReminderInfo(ReminderInfo.Builder builder) {
        builder.recurrenceInfo(null);
        builder.assignInfo(null);
        builder.taskListInfo(null);
        builder.dueDate(null);
        builder.locationAddress(null);
        builder.deleted(null);
    }

    @Override
    public void remindersOperations(ArrayList<ContentProviderOperation> list) {
        Log.d(TAG, "remindersOperations: ");
        task.createdTimeMillis = System.currentTimeMillis();

        RecurrenceEntity recurrenceEntity = task.recurrenceInfoEntity.recurrenceEntity;

        RecurrenceEndEntity recurrenceEndEntity = recurrenceEntity.recurrenceEndEntity;
        if (recurrenceEndEntity != null && recurrenceEndEntity.num != null) {
            int recurrenceNum = detailRecurrenceNum(recurrenceEndEntity.num, recurrenceEntity.frequency);
            Log.d(TAG, "remindersOperations recurrence num :" + recurrenceNum);
            recurrenceEntity.recurrenceEndEntity = new RecurrenceEndEntity(null, recurrenceNum, null, null);
        } else {
            //Get the latest end time
            DateTimeEntity dateTimeEntity = CalendarUtils.calculateDateWithOffset(recurrenceEntity.recurrenceStartEntity.dateTimeEntity, getLocalExpansionDaysByFrequency(recurrenceEntity.frequency));
            if (recurrenceEndEntity != null && recurrenceEndEntity.dateTimeEntity != null
                    && recurrenceEntity.recurrenceEndEntity.dateTimeEntity.compare(dateTimeEntity) < 0) {
                dateTimeEntity = recurrenceEntity.recurrenceEndEntity.dateTimeEntity;
            }

            recurrenceEntity.recurrenceEndEntity = new RecurrenceEndEntity(dateTimeEntity, null, null, null);
        }

        RecurrenceIterator recurrenceIterator = initRecurrenceIterator(recurrenceEntity);


        List<DateTimeEntity> recurrenceDateTimeList = getRecurrenceDateTimes(recurrenceIterator, list);

        if (!recurrenceDateTimeList.isEmpty()) {
            TaskEntity tempTaskEntity = new TaskEntity(task);
            tempTaskEntity.dueDate = recurrenceDateTimeList.get(0);

            String recurrenceId = tempTaskEntity.recurrenceInfoEntity.recurrenceId;
            ContentValues contentValues = putTaskEntity(tempTaskEntity);
            contentValues.put("account_id", accountId);
            contentValues.put("snoozed", task.snoozed == null || task.snoozed);
            contentValues.put("client_assigned_id", recurrenceId + "/master");
            contentValues.put("recurrence_master", 1);
            list.add(ContentProviderOperation.newAssertQuery(RemindersProvider.REMINDERS_URI).withSelection("client_assigned_id=? AND account_id=? AND deleted=?", new String[]{recurrenceId + "/master", String.valueOf(accountId), "0"}).withExpectedCount(0).build());
            list.add(ContentProviderOperation.newInsert(RemindersProvider.REMINDERS_URI).withValues(contentValues).build());
        }

    }

    private List<DateTimeEntity> getRecurrenceDateTimes(RecurrenceIterator recurrenceIterator, ArrayList<ContentProviderOperation> list) {
        ArrayList<DateTimeEntity> recurrenceDateTimeList = new ArrayList<>();

        while (recurrenceIterator.hasNext()) {
            DateTimeEntity dateTimeEntity = recurrenceIterator.next();
            Log.d(TAG, "remindersOperations: " + dateTimeEntity.toString());
            if (!checkFutureTimeValidity(dateTimeEntity)) {
                continue;
            }
            Log.d(TAG, "remindersOperations qualified: " + dateTimeEntity);
            String recurrenceId = task.recurrenceInfoEntity.recurrenceId;
            DateTimeEntity byvz0 = new DateTimeEntity(dateTimeEntity);
            if (Boolean.TRUE.equals(dateTimeEntity.allDay)) {
                byvz0.timeEntity = null;
                byvz0.period = null;
            }

            String clientAssignedId = recurrenceId + "/" + dateTimeToMillis(byvz0);
            TaskEntity tempTaskEntity = new TaskEntity(task);
            tempTaskEntity.taskId = new TaskIdEntity(clientAssignedId, null);
            tempTaskEntity.dueDate = dateTimeEntity;
            ContentValues contentValues = putTaskEntity(new TaskEntity(tempTaskEntity));
            contentValues.put("account_id", accountId);
            contentValues.put("snoozed", task.snoozed == null || task.snoozed);
            list.add(ContentProviderOperation.newInsert(RemindersProvider.REMINDERS_URI).withValues(contentValues).build());
            recurrenceDateTimeList.add(dateTimeEntity);
        }
        return recurrenceDateTimeList;
    }

    private RecurrenceIterator initRecurrenceIterator(RecurrenceEntity recurrenceEntity) {
        switch (recurrenceEntity.frequency) {
            case 0:
                return new DailyRecurrenceIterator(recurrenceEntity);
            case 1:
                return new WeeklyRecurrenceIterator(recurrenceEntity);
            case 2:
                return new MonthlyRecurrenceIterator(recurrenceEntity);
            case 3:
                return new YearlyRecurrenceIterator(recurrenceEntity);
            default:
                throw new IllegalStateException("Unrecognized frequency: " + recurrenceEntity.frequency);
        }
    }

    private int detailRecurrenceNum(int recurrenceNum, int frequency) {
        //used to limit the maximum number of repetitions
        switch (frequency) {
            case 0: {
                return Math.min(recurrenceNum, (getLocalExpansionDaysByFrequency(frequency)));
            }
            case 1: {
                return (int) Math.min(recurrenceNum, (getLocalExpansionDaysByFrequency(frequency) / 7d));
            }
            case 2: {
                return (int) Math.min(recurrenceNum, (getLocalExpansionDaysByFrequency(frequency) / 31d));
            }
            case 3: {
                return (int) Math.min(recurrenceNum, (getLocalExpansionDaysByFrequency(frequency) / 365d));
            }
            default: {
                throw new IllegalStateException("Unrecognized frequency: " + frequency);
            }
        }
    }

    private ContentValues putTaskEntity(TaskEntity taskEntity) {
        Log.d(TAG, "putTaskEntity: ");
        ContentValues contentValues = new ContentValues();
        try {
            RemindersSyncAdapter.putReminderInfo(contentValues, createReminderInfo(taskEntity));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        contentValues.remove("due_date_millis");
        contentValues.remove("fired_time_millis");
        contentValues.put("dirty_sync_bit", Boolean.FALSE);
        return contentValues;
    }

    private boolean checkFutureTimeValidity(DateTimeEntity dateTime) {
        DateTimeEntity currentDateTime = createDateTimeEntityWithOffset(System.currentTimeMillis(), TimeZone.getDefault());
        if (dateTime.timeEntity == null && dateTime.period != null) {
            dateTime = new DateTimeEntity(dateTime);
            dateTime.timeEntity = (getCustomizedTimeByPeriod(this.customizedSnoozePreset, dateTime.period));
        }

        return (Boolean.TRUE.equals(dateTime.unspecifiedFutureTime)) || dateTimeToMillis(dateTime) > dateTimeToMillis(currentDateTime);
    }

    public long dateTimeToMillis(DateTimeEntity dateTime) {
        if (dateTime.absoluteTimeMs != null) {
            return dateTime.absoluteTimeMs;
        }
        int minute = 0;
        int hourOfDay = 0;
        int second = 0;
        if (dateTime.timeEntity != null) {
            second = dateTime.timeEntity.second;
            hourOfDay = dateTime.timeEntity.hourOfDay;
            minute = dateTime.timeEntity.minute;
        } else if (dateTime.period != null) {
            hourOfDay = getHourOfDayByPeriod(dateTime.period);
        }

        Calendar calendar = CalendarUtils.getCalendarUTC();
        calendar.set(dateTime.year, dateTime.month - 1, dateTime.day, hourOfDay, minute, second);
        return calendar.getTimeInMillis();
    }

    private TimeEntity getCustomizedTimeByPeriod(CustomizedSnoozePresetEntity customizedSnoozePreset, Integer period) {
        if (customizedSnoozePreset != null) {
            Log.d(TAG, "byyy_c: CustomizedSnoozePresetEntity " + customizedSnoozePreset);
            switch (period) {
                case 1: {
                    return customizedSnoozePreset.morningCustomizedTime;
                }
                case 2: {
                    return customizedSnoozePreset.afternoonCustomizedTime;
                }
                case 3: {
                    return customizedSnoozePreset.eveningCustomizedTime;
                }
                case 4: {
                    break;
                }
                default: {
                    return new TimeEntity(0, 0, 0);
                }
            }

            return new TimeEntity(20, 0, 0);
        }

        return new TimeEntity(getHourOfDayByPeriod(period), 0, 0);
    }

    private int getHourOfDayByPeriod(Integer period) {
        switch (period) {
            case 1: {
                return 9;
            }
            case 2: {
                return 13;
            }
            case 3: {
                return 17;
            }
            case 4: {
                return 20;
            }
            default: {
                return 0;
            }
        }
    }

    private int getLocalExpansionDaysByFrequency(int frequency) {
        switch (frequency) {
            case 0:
                return DAILY_LOCAL_EXPANSION_DAYS;
            case 1:
                return WEEKLY_LOCAL_EXPANSION_DAYS;
            case 2:
                return MONTHLY_LOCAL_EXPANSION_DAYS;
            case 3:
                return YEARLY_LOCAL_EXPANSION_DAYS;
            default: {
                throw new IllegalStateException("Unrecognized frequency: " + frequency);
            }
        }
    }
}
