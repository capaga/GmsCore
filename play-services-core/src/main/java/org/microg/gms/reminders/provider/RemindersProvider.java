package org.microg.gms.reminders.provider;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.reminders.model.CustomizedSnoozePresetEntity;
import com.google.android.gms.reminders.model.TimeEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RemindersProvider extends ContentProvider {
    private static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gms.reminders");
    public static final String ACTION = "com.google.android.gms.reminders.SEND_NOTIFICATION";
    private Context mContext;
    private BroadcastReceiver mReceiver;
    public static final Uri ACCOUNT_URI = Uri.withAppendedPath(CONTENT_URI, "account");
    public static final Uri REMINDERS_URI = Uri.withAppendedPath(CONTENT_URI, "reminders");
    public static final Uri REMINDERS_EVENTS_URI = Uri.withAppendedPath(CONTENT_URI, "reminder_events");
    public static final Uri REMINDERS_MARK_EXCEPTIONAL_URI = Uri.withAppendedPath(CONTENT_URI, "reminders/mark_exceptional");
    public static final Uri REMINDERS_UPSERT_URI = Uri.withAppendedPath(CONTENT_URI, "reminders/upsert");
    public static final Uri OPERATIONS_URI = Uri.withAppendedPath(CONTENT_URI, "operations");

    private static final String TAG = RemindersProvider.class.getSimpleName();

    private static final String[] accountFields = {
            "_id",
            "account_name",
            "storage_version",
            "sync_status",
            "morning_customized_time",
            "afternoon_customized_time",
            "evening_customized_time",
            "account_state",
            "need_sync_snooze_preset",
            "was_last_sync_error"
    };

    private static final String[] notificationFields = {
            "_id",
            "trigger_time",
            "create_time",
            "schedule_time",
            "fire_time",
            "snooze_time",
            "dismiss_time"
    };
    private static final String[] remindersFields = {
            "_id",
            "account_id",
            "reminder_type",
            "client_assigned_id",
            "server_assigned_id",
            "client_assigned_thread_id",
            "task_list",
            "title",
            "created_time_millis",
            "archived_time_ms",
            "archived",
            "deleted",
            "pinned",
            "snoozed",
            "snoozed_time_millis",
            "location_snoozed_until_ms",
            "due_date_year",
            "due_date_month",
            "due_date_day",
            "due_date_hour",
            "due_date_minute",
            "due_date_second",
            "due_date_period",
            "due_date_absolute_time_ms",
            "due_date_date_range",
            "due_date_unspecified_future_time",
            "due_date_all_day",
            "due_date_millis",
            "event_date_year",
            "event_date_month",
            "event_date_day",
            "event_date_hour",
            "event_date_minute",
            "event_date_second",
            "event_date_period",
            "event_date_absolute_time_ms",
            "event_date_date_range",
            "event_date_unspecified_future_time",
            "event_date_all_day",
            "lat",
            "lng",
            "name",
            "radius_meters",
            "location_type",
            "display_address",
            "address_country",
            "address_locality",
            "address_region",
            "address_street_address",
            "address_street_number",
            "address_street_name",
            "address_postal_code",
            "address_name",
            "location_cell_id",
            "location_fprint",
            "location_alias_id",
            "location_query",
            "location_query_type",
            "chain_id_cell_id",
            "chain_id_fprint",
            "chain_name",
            "category_id",
            "display_name",
            "recurrence_id",
            "recurrence_master",
            "recurrence_exceptional",
            "recurrence_frequency",
            "recurrence_every",
            "recurrence_start_year",
            "recurrence_start_month",
            "recurrence_start_day",
            "recurrence_start_hour",
            "recurrence_start_minute",
            "recurrence_start_second",
            "recurrence_start_period",
            "recurrence_start_absolute_time_ms",
            "recurrence_start_date_range",
            "recurrence_start_unspecified_future_time",
            "recurrence_start_all_day",
            "recurrence_end_year",
            "recurrence_end_month",
            "recurrence_end_day",
            "recurrence_end_hour",
            "recurrence_end_minute",
            "recurrence_end_second",
            "recurrence_end_period",
            "recurrence_end_absolute_time_ms",
            "recurrence_end_date_range",
            "recurrence_end_unspecified_future_time",
            "recurrence_end_all_day",
            "recurrence_end_num_occurrences",
            "recurrence_end_auto_renew",
            "recurrence_end_auto_renew_until_year",
            "recurrence_end_auto_renew_until_month",
            "recurrence_end_auto_renew_until_day",
            "recurrence_end_auto_renew_until_hour",
            "recurrence_end_auto_renew_until_minute",
            "recurrence_end_auto_renew_until_second",
            "recurrence_end_auto_renew_until_period",
            "recurrence_end_auto_renew_until_absolute_time_ms",
            "recurrence_end_auto_renew_until_date_range",
            "recurrence_end_auto_renew_until_unspecified_future_time",
            "recurrence_end_auto_renew_until_all_day",
            "daily_pattern_hour",
            "daily_pattern_minute",
            "daily_pattern_second",
            "daily_pattern_period",
            "daily_pattern_all_day",
            "weekly_pattern_weekday",
            "monthly_pattern_month_day",
            "monthly_pattern_week_day",
            "monthly_pattern_week_day_number",
            "yearly_pattern_year_month",
            "yearly_pattern_monthly_pattern_month_day",
            "yearly_pattern_monthly_pattern_week_day",
            "yearly_pattern_monthly_pattern_week_day_number",
            "experiment",
            "extensions",
            "assistance",
            "link_application",
            "link_id",
            "fired_time_millis",
            "dirty_sync_bit",
            "place_types"
    };
    public static final String AUTHORITY = "com.google.android.gms.reminders";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final Map<String, String> accountMap = new HashMap<>();
    private static final Map<String, String> remindersMap = new HashMap<>();
    private static final Map<String, String> notificationMap = new HashMap<>();
    private static final Map<String, String> Collect = new HashMap<>();
    private static final Map<String, String> reminderEventsMap = new HashMap<>();
    private static final Map<String, String> operationsMap = new HashMap<>();
    private static final Map<String, String> placeAliasesMap = new HashMap<>();

    private static final int MILLISECONDS_PER_DAY = 86400000;
    private static final int MILLISECONDS_PER_HOUR = 3600000;
    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final int MILLISECONDS_PER_SECOND = 1000;

    private static final int CODE_ACCOUNT = 100;
    private static final int CODE_ACCOUNT_WITH_ID = 101;
    private static final int CODE_REMINDERS = 200;
    private static final int CODE_REMINDERS_WITH_ID = 201;
    private static final int CODE_REMINDERS_UPSERT = 202;
    private static final int CODE_REMINDERS_REFRESH_DUE_DATE = 203;
    private static final int CODE_REMINDERS_MARK_EXCEPTIONAL = 204;
    private static final int CODE_REMINDERS_UPDATE_FIRED_WITH_ID = 205;
    private static final int CODE_REMINDERS_UPDATE_BUMPED = 206;
    private static final int CODE_NOTIFICATION = 300;
    private static final int CODE_NOTIFICATION_WITH_ID = 301;
    private static final int CODE_REMINDER_NOTIFICATIONS = 302;
    private static final int CODE_REMINDER_EVENTS = 400;
    private static final int CODE_OPERATIONS = 600;
    private static final int CODE_OPERATIONS_WITH_ID = 601;
    private static final int CODE_PLACE_ALIASES = 700;
    private static final int CODE_PLACE_ALIASES_WITH_ID = 701;

    static {
        uriMatcher.addURI(AUTHORITY, "account", CODE_ACCOUNT);
        uriMatcher.addURI(AUTHORITY, "account/#", CODE_ACCOUNT_WITH_ID);
        uriMatcher.addURI(AUTHORITY, "reminders", CODE_REMINDERS);
        uriMatcher.addURI(AUTHORITY, "reminders/#", CODE_REMINDERS_WITH_ID);
        uriMatcher.addURI(AUTHORITY, "reminders/upsert", CODE_REMINDERS_UPSERT);
        uriMatcher.addURI(AUTHORITY, "reminders/refresh_due_date", CODE_REMINDERS_REFRESH_DUE_DATE);
        uriMatcher.addURI(AUTHORITY, "reminders/mark_exceptional", CODE_REMINDERS_MARK_EXCEPTIONAL);
        uriMatcher.addURI(AUTHORITY, "reminders/update_fired/#", CODE_REMINDERS_UPDATE_FIRED_WITH_ID);
        uriMatcher.addURI(AUTHORITY, "reminders/update_bumped", CODE_REMINDERS_UPDATE_BUMPED);
        uriMatcher.addURI(AUTHORITY, "notification", CODE_NOTIFICATION);
        uriMatcher.addURI(AUTHORITY, "notification/#", CODE_NOTIFICATION_WITH_ID);
        uriMatcher.addURI(AUTHORITY, "reminder_notifications", CODE_REMINDER_NOTIFICATIONS);
        uriMatcher.addURI(AUTHORITY, "reminder_events", CODE_REMINDER_EVENTS);
        uriMatcher.addURI(AUTHORITY, "operations", CODE_OPERATIONS);
        uriMatcher.addURI(AUTHORITY, "operations/#", CODE_OPERATIONS_WITH_ID);
        uriMatcher.addURI(AUTHORITY, "place_aliases", CODE_PLACE_ALIASES);
        uriMatcher.addURI(AUTHORITY, "place_aliases/#", CODE_PLACE_ALIASES_WITH_ID);
        // Account
        for (String accountField : accountFields) {
            accountMap.put(accountField, "account.".concat(accountField));
        }

        // Reminders
        for (String remindersField : remindersFields) {
            remindersMap.put(remindersField, "reminders.".concat(remindersField));
        }
        remindersMap.put("_count", "COUNT(*)");

        // Notification
        for (String notificationField : notificationFields) {
            notificationMap.put(notificationField, "notification.".concat(notificationField));
        }

        // Collect
        for (String remindersField : remindersFields) {
            Collect.put(remindersField, "reminders.".concat(remindersField));
        }
        Collect.putAll(notificationMap);

        //other
        reminderEventsMap.putAll(remindersMap);
        reminderEventsMap.put("_id", "reminders._id");
        reminderEventsMap.put("account_name", "account.account_name");
        operationsMap.put("_id", "operation._id");
        operationsMap.put("operation_api", "operation.operation_api");
        operationsMap.put("operation_request", "operation.operation_request");
        operationsMap.put("error_count", "operation.error_count");
        placeAliasesMap.put("_id", "place_alias._id");
        placeAliasesMap.put("account_id", "place_alias.account_id");
        placeAliasesMap.put("alias_id", "place_alias.alias_id");
        placeAliasesMap.put("alias_name", "place_alias.alias_name");
        placeAliasesMap.put("place_id", "place_alias.place_id");
    }

    private final ArrayList<Long> idList = new ArrayList<>();
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;
    private ArrayList<Long> n;
    private NotificationCalendar notificationCalendar;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "reminders onCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        this.mContext = getContext();
        this.mReceiver = new ReminderAlarmReceiver();
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        return false;
    }

    @Override
    public void shutdown() {
        Log.d(TAG, "reminders shutdown");
        if (this.mContext != null && this.mReceiver != null) {
            this.mContext.unregisterReceiver(mReceiver);
        }
    }

    private class ReminderAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION.equals(action)) {
                long reminderId = intent.getLongExtra("reminderId", -1L);
                if (reminderId != -1L) {
                    new SendNotificationThread(reminderId).start();
                }
            }
        }
    }

    private class SendNotificationThread extends Thread {
        private final long reminderId;

        public SendNotificationThread(long reminderId) {
            this.reminderId = reminderId;
        }

        @Override
        public void run() {
            if (notificationCalendar == null) {
                notificationCalendar = new NotificationCalendar(mContext);
            }
            Log.d(TAG, "sendNotification: reminderId => " + this.reminderId);
            notificationCalendar.sendTaskNotification(this.reminderId);
        }
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "remindersProvider query " + uri);
        if (readableDatabase == null) {
            readableDatabase = RemindersDatabaseHelper.getInst(getContext()).getReadableDatabase();
        }
        int code = uriMatcher.match(uri);
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setStrict(true);

        switch (code) {
            case CODE_ACCOUNT:
            case CODE_ACCOUNT_WITH_ID:
                if (code == CODE_ACCOUNT_WITH_ID) {
                    selection = "_id=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }

                sqLiteQueryBuilder.setTables(RemindersDatabaseHelper.ACCOUNT_TABLE);
                sqLiteQueryBuilder.setProjectionMap(accountMap);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            case CODE_REMINDERS:
            case CODE_REMINDERS_WITH_ID: {
                if (code == CODE_REMINDERS_WITH_ID) {
                    selection = "reminders._id=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }
                sqLiteQueryBuilder.setTables(RemindersDatabaseHelper.REMINDERS_TABLE);
                sqLiteQueryBuilder.setProjectionMap(remindersMap);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case CODE_NOTIFICATION:
            case CODE_NOTIFICATION_WITH_ID: {
                if (code == CODE_NOTIFICATION_WITH_ID) {
                    selection = "notification._id=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }

                sqLiteQueryBuilder.setTables(RemindersDatabaseHelper.NOTIFICATION_TABLE);
                sqLiteQueryBuilder.setProjectionMap(notificationMap);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case CODE_REMINDER_NOTIFICATIONS: {
                sqLiteQueryBuilder.setTables("reminders LEFT OUTER JOIN notification ON reminders._id = notification._id");
                sqLiteQueryBuilder.setProjectionMap(Collect);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case CODE_REMINDER_EVENTS: {
                sqLiteQueryBuilder.setTables("reminders LEFT OUTER JOIN account ON reminders.account_id = account._id");
                sqLiteQueryBuilder.setProjectionMap(reminderEventsMap);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, null);
            }
            case CODE_OPERATIONS:
            case CODE_OPERATIONS_WITH_ID: {
                if (code == CODE_OPERATIONS_WITH_ID) {
                    selection = "_id=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }

                sqLiteQueryBuilder.setTables(RemindersDatabaseHelper.OPERATION_TABLE);
                sqLiteQueryBuilder.setProjectionMap(operationsMap);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case CODE_PLACE_ALIASES:
            case CODE_PLACE_ALIASES_WITH_ID: {
                if (code == CODE_PLACE_ALIASES_WITH_ID) {
                    selection = "_id=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }

                sqLiteQueryBuilder.setTables(RemindersDatabaseHelper.PLACE_ALIAS_TABLE);
                sqLiteQueryBuilder.setProjectionMap(placeAliasesMap);
                return sqLiteQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "reminders insert " + uri);
        if (writableDatabase == null) {
            writableDatabase = RemindersDatabaseHelper.getInst(getContext()).getWritableDatabase();
        }
        writableDatabase.beginTransactionWithListener(this.notificationCalendar);
        Uri res = insertImpl(uri, values);
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        return res;
    }

    private void createAlarm(long reminderId) {
        Uri uri = ContentUris.withAppendedId(REMINDERS_URI, reminderId);
        Log.d(TAG, "createOrUpdateReminderAlarm: uri=>" + uri);
        Cursor cursor = query(uri,
                null,
                null,
                null,
                null);
        if (cursor == null) {
            return;
        }
        DataHolder dataHolder = new DataHolder(cursor, 0, null);
        if (dataHolder.getCount() == 0) {
            return;
        }
        createOrUpdateAlarm(dataHolder);
    }

    private void updateAlarm(Uri uri, String selection, String[] selectionArgs) {
        String clientAssignedId = null;
        if (uri.toString().equals("content://com.google.android.gms.reminders/reminders/mark_exceptional")
                && selection.equals("client_assigned_id=? AND account_id=? AND deleted=0")) {
            clientAssignedId = selectionArgs[0];
        }
        if (clientAssignedId == null) {
            return;
        }
        Cursor cursor = query(RemindersProvider.REMINDERS_URI,
                null,
                "reminders.client_assigned_id=?",
                new String[]{clientAssignedId}, null);
        if (cursor == null) {
            return;
        }
        DataHolder dataHolder = new DataHolder(cursor, 0, null);
        if (dataHolder.getCount() == 0) {
            return;
        }
        createOrUpdateAlarm(dataHolder);
    }

    private void createOrUpdateAlarm(DataHolder dataHolder) {
        long reminderId = dataHolder.getLong("_id", 0, 0);
        int dueDateYear = dataHolder.getInteger("due_date_year", 0, 0);
        int dueDateMonth = dataHolder.getInteger("due_date_month", 0, 0);
        int dueDateDay = dataHolder.getInteger("due_date_day", 0, 0);
        int dueDateHour = dataHolder.getInteger("due_date_hour", 0, 0);
        int dueDateMinute = dataHolder.getInteger("due_date_minute", 0, 0);
        int dueDateSecond = dataHolder.getInteger("due_date_second", 0, 0);
        int due_date_all_day = dataHolder.getInteger("due_date_all_day", 0, 0);
        int deleted = dataHolder.getInteger("deleted", 0, 0);
        int archived = dataHolder.getInteger("archived", 0, 0);

        Log.d(TAG, "createOrUpdateAlarm: reminderId=>" + reminderId);
        Log.d(TAG, "createOrUpdateAlarm: due_date_year=>" + dueDateYear);
        Log.d(TAG, "createOrUpdateAlarm: due_date_month=>" + dueDateMonth);
        Log.d(TAG, "createOrUpdateAlarm: due_date_day=>" + dueDateDay);
        Log.d(TAG, "createOrUpdateAlarm: due_date_hour=>" + dueDateHour);
        Log.d(TAG, "createOrUpdateAlarm: due_date_minute=>" + dueDateMinute);
        Log.d(TAG, "createOrUpdateAlarm: due_date_second=>" + dueDateSecond);
        Log.d(TAG, "createOrUpdateAlarm: due_date_all_day=>" + due_date_all_day);
        Log.d(TAG, "createOrUpdateAlarm: deleted=>" + deleted);
        Log.d(TAG, "createOrUpdateAlarm: pinned=>" + archived);

        if (deleted == 1 || due_date_all_day == 1 || archived == 1) {
            Log.d(TAG, "createOrUpdateAlarm: reminderId=> " + reminderId + " no need to create or update alarm");
            cancelAlarm(reminderId);
            return;
        }
        if (dueDateYear == 0 || dueDateMonth == 0 || dueDateDay == 0) {
            Log.d(TAG, "createOrUpdateAlarm: date error");
        }
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dueDateYear);
        calendar.set(Calendar.MONTH, dueDateMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dueDateDay);
        calendar.set(Calendar.HOUR_OF_DAY, dueDateHour);
        calendar.set(Calendar.MINUTE, dueDateMinute);
        calendar.set(Calendar.SECOND, dueDateSecond);
        Intent intent = new Intent(ACTION);
        intent.putExtra("reminderId", reminderId);
        // 以reminderId为requestCode，确保每个闹钟都是唯一的
        PendingIntent broadcast = PendingIntent.getBroadcast(this.mContext, (int) reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "createOrUpdateAlarm: create alarm, reminderId=>" + reminderId + "  timestamp=>" + calendar.getTimeInMillis());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
    }

    private void cancelAlarm(String selection, String[] selectionArgs) {
        Cursor cursor = query(RemindersProvider.REMINDERS_URI,
                null,
                selection,
                selectionArgs, null);
        if (cursor == null) {
            return;
        }
        DataHolder dataHolder = new DataHolder(cursor, 0, null);
        if (dataHolder.getCount() == 0) {
            return;
        }
        long reminderId = dataHolder.getLong("_id", 0, 0);
        cancelAlarm(reminderId);
    }

    private void cancelAlarm(long reminderId) {
        Log.d(TAG, "cancelAlarm: reminderId=>" + reminderId);
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION);
        intent.putExtra("reminderId", reminderId);
        PendingIntent broadcast = PendingIntent.getBroadcast(this.mContext, (int) reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(broadcast);
    }

    public Uri insertImpl(@NonNull Uri uri, @Nullable ContentValues values) {
        this.notificationCalendar = new NotificationCalendar(getContext());
        Integer accountId = values.getAsInteger("account_id");
        switch (uriMatcher.match(uri)) {
            case CODE_ACCOUNT: {
                long insertedRowId = writableDatabase.insertOrThrow(RemindersDatabaseHelper.ACCOUNT_TABLE, null, values);
                return insertedRowId == -1 ? null : ContentUris.withAppendedId(uri, insertedRowId);
            }
            case CODE_REMINDERS: {
                validateAndUpdateTimeValues(values);
                if (accountId != null) {
                    checkDueDate(writableDatabase, accountId, values);
                    if (values.getAsLong("created_time_millis") == null) {
                        values.put("created_time_millis", System.currentTimeMillis());
                    }
                    long insertedRowId = writableDatabase.insertOrThrow(RemindersDatabaseHelper.REMINDERS_TABLE, null, values);
                    if (insertedRowId == -1L) {
                        return null;
                    } else {
                        Uri retUri = ContentUris.withAppendedId(uri, insertedRowId);
                        createAlarm(insertedRowId);
                        return retUri;
                    }
                }
                return null;
            }
            case CODE_REMINDERS_UPSERT: {
                String[] selectionArgs = {values.getAsString("client_assigned_id"), String.valueOf(values.getAsInteger("account_id"))};
                @SuppressLint("Recycle") Cursor cursor = writableDatabase.query(RemindersDatabaseHelper.REMINDERS_TABLE, new String[]{"_id"}, "client_assigned_id=? AND account_id=?", selectionArgs, null, null, null);
                if (cursor.getCount() == 0) {
                    checkDueDate(writableDatabase, values.getAsInteger("account_id"), values);
                    if (values.getAsLong("created_time_millis") == null) {
                        values.put("created_time_millis", System.currentTimeMillis());
                    }
                    long row = writableDatabase.insert(RemindersDatabaseHelper.REMINDERS_TABLE, null, values);
                    return ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, row);
                }

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    long id = cursor.getLong(0);
                    @SuppressLint("Recycle") Cursor acccursor = writableDatabase.query(true, RemindersDatabaseHelper.REMINDERS_TABLE, new String[]{"account_id"}, "_id=?", new String[]{String.valueOf(id)}, null, null, null, null);
                    acccursor.moveToFirst();
                    if ((Boolean.TRUE.equals(values.getAsBoolean("archived"))) && values.getAsLong("archived_time_ms") == null) {
                        ContentValues archivedTimeMsContent = new ContentValues();
                        archivedTimeMsContent.put("archived_time_ms", System.currentTimeMillis());
                        writableDatabase.update(RemindersDatabaseHelper.REMINDERS_TABLE, archivedTimeMsContent, "(_id=?)And(archived=0)", selectionArgs);
                        values.remove("archived_time_ms");
                    }
                    long insertedRowId = writableDatabase.update(RemindersDatabaseHelper.REMINDERS_TABLE, values, "(client_assigned_id=?)AND(account_id=?)", selectionArgs);
                    if (insertedRowId == -1) {
                        return null;
                    }
                    return ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, insertedRowId);
                }

                return ContentUris.withAppendedId(uri, accountId);
            }
            case CODE_OPERATIONS: {
                long insertedRowId = writableDatabase.insertOrThrow(RemindersDatabaseHelper.OPERATION_TABLE, null, values);
                return insertedRowId == -1L ? null : ContentUris.withAppendedId(uri, insertedRowId);
            }
            default: {
                return null;
            }

        }
    }

    private Map<Integer, CustomizedSnoozePresetEntity> getSnoozed(SQLiteDatabase db) {
        @SuppressLint("Recycle") Cursor cursor = db.query("account", new String[]{"_id", "morning_customized_time", "afternoon_customized_time", "evening_customized_time"}, null, null, null, null, null);
        Map<Integer, CustomizedSnoozePresetEntity> snoozeList = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                snoozeList.put(cursor.getInt(0), new CustomizedSnoozePresetEntity(convertTimestampToTime(cursor.getInt(1)), convertTimestampToTime(cursor.getInt(2)), convertTimestampToTime(cursor.getInt(3))));
            }
        }
        return snoozeList;
    }

    private TimeEntity convertTimestampToTime(int timestamp) {
        if (timestamp < 0 || timestamp >= MILLISECONDS_PER_DAY) {
            return null;
        }

        int hours = timestamp / MILLISECONDS_PER_HOUR;
        int remainingMillis = timestamp % MILLISECONDS_PER_HOUR;

        int minutes = remainingMillis / MILLISECONDS_PER_MINUTE;
        int seconds = (remainingMillis % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND;

        return new TimeEntity(hours, minutes, seconds);
    }

    private void checkDueDate(SQLiteDatabase db, Integer uid, ContentValues values) {
        if (values.containsKey("due_date_year")
                || values.containsKey("due_date_month")
                || values.containsKey("due_date_day")
                || values.containsKey("due_date_hour")
                || values.containsKey("due_date_minute")
                || values.containsKey("due_date_second")
                || values.containsKey("due_date_period")
                || values.containsKey("due_date_absolute_time_ms")) {

            CustomizedSnoozePresetEntity snooze = getSnoozed(db).get(uid);
            Long millis = convertDueDateToMillis(
                    values.getAsInteger("due_date_year"),
                    values.getAsInteger("due_date_month"),
                    values.getAsInteger("due_date_day"),
                    values.getAsInteger("due_date_hour"),
                    values.getAsInteger("due_date_minute"),
                    values.getAsInteger("due_date_second"),
                    values.getAsInteger("due_date_period"),
                    values.getAsLong("due_date_absolute_time_ms"),
                    snooze
            );
            values.put("due_date_millis", millis);
        }
    }


    private Long convertDueDateToMillis(Integer year, Integer month, Integer day, Integer hourOfDay,
                                        Integer minute, Integer second, Integer period, Long absoluteTimeMs,
                                        CustomizedSnoozePresetEntity customizedSnoozePresetEntity) {
        if (absoluteTimeMs != null) {
            return absoluteTimeMs;
        }

        if (year != null && month != null && day != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();

            if (hourOfDay != null && minute != null && second != null) {
                calendar.set(year, month - 1, day, hourOfDay, minute, second);
                return calendar.getTimeInMillis();
            }

            if (period != null) {
                TimeEntity timeEntity = snoozeSelect(customizedSnoozePresetEntity, period);
                calendar.set(year, month - 1, day, timeEntity.hourOfDay, timeEntity.minute, timeEntity.second);
                return calendar.getTimeInMillis();
            }

            calendar.set(year, month - 1, day, 0, 0, 0);
            return calendar.getTimeInMillis();
        }
        return null;
    }

    private TimeEntity snoozeSelect(CustomizedSnoozePresetEntity customizedSnoozePreset, Integer period) {
        if (customizedSnoozePreset != null) {
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
        return null;
    }

    private static void validateAndUpdateTimeValues(ContentValues values) {
        if (values.getAsInteger("due_date_hour") != null) {
            validateTimeValues(values, "due_date_hour");
        }
        if (values.getAsInteger("due_date_minute") != null) {
            validateTimeValues(values, "due_date_minute");
        }
        if (values.getAsInteger("due_date_second") != null) {
            validateTimeValues(values, "due_date_second");
        }

        if (values.getAsInteger("event_date_hour") != null) {
            validateTimeValues(values, "event_date_hour");
        }
        if (values.getAsInteger("event_date_minute") != null) {
            validateTimeValues(values, "event_date_minute");
        }
        if (values.getAsInteger("event_date_second") != null) {
            validateTimeValues(values, "event_date_second");
        }
    }

    private static void validateTimeValues(ContentValues content, String values) {
        if (content.getAsInteger(values) == null) {
            content.put(values, 0);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "reminders delete " + uri);
        if (writableDatabase == null) {
            writableDatabase = RemindersDatabaseHelper.getInst(getContext()).getWritableDatabase();
        }
        writableDatabase.beginTransactionWithListener(this.notificationCalendar);
        int res = deleteImpl(uri, selection, selectionArgs);
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        return res;
    }

    public int deleteImpl(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = uriMatcher.match(uri);
        try {
            switch (code) {
                case CODE_ACCOUNT:
                case CODE_ACCOUNT_WITH_ID: {
                    if (code == CODE_ACCOUNT_WITH_ID) {
                        selection = "_id=?";
                        selectionArgs = new String[]{uri.getLastPathSegment()};
                    }

                    return writableDatabase.delete(RemindersDatabaseHelper.ACCOUNT_TABLE, selection, selectionArgs);
                }
                case CODE_REMINDERS:
                case CODE_REMINDERS_WITH_ID: {
                    if (code == CODE_REMINDERS_WITH_ID) {
                        selectionArgs = new String[]{uri.getLastPathSegment()};
                        selection = "_id=?";
                    }
                    cancelAlarm(selection, selectionArgs);
                    return writableDatabase.delete(RemindersDatabaseHelper.REMINDERS_TABLE, selection, selectionArgs);
                }
                case CODE_NOTIFICATION:
                case CODE_NOTIFICATION_WITH_ID: {
                    if (code == CODE_NOTIFICATION_WITH_ID) {
                        selectionArgs = new String[]{uri.getLastPathSegment()};
                        selection = "_id=?";
                    }

                    return writableDatabase.delete(RemindersDatabaseHelper.NOTIFICATION_TABLE, selection, selectionArgs);
                }
                case CODE_OPERATIONS:
                case CODE_OPERATIONS_WITH_ID: {
                    if (code == CODE_OPERATIONS_WITH_ID) {
                        selectionArgs = new String[]{uri.getLastPathSegment()};
                        selection = "_id=?";
                    }

                    return writableDatabase.delete(RemindersDatabaseHelper.OPERATION_TABLE, selection, selectionArgs);
                }
                case CODE_PLACE_ALIASES:
                case CODE_PLACE_ALIASES_WITH_ID: {
                    if (code == CODE_PLACE_ALIASES_WITH_ID) {
                        selectionArgs = new String[]{uri.getLastPathSegment()};
                        selection = "_id=?";
                    }

                    return writableDatabase.delete(RemindersDatabaseHelper.PLACE_ALIAS_TABLE, selection, selectionArgs);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "reminders update " + uri);
        if (writableDatabase == null) {
            writableDatabase = RemindersDatabaseHelper.getInst(getContext()).getWritableDatabase();
        }
        writableDatabase.beginTransactionWithListener(this.notificationCalendar);
        int res = updateImpl(uri, values, selection, selectionArgs);
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        updateAlarm(uri, selection, selectionArgs);
        return res;
    }

    private int updateImpl(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = uriMatcher.match(uri);
        switch (code) {
            case CODE_ACCOUNT:
            case CODE_ACCOUNT_WITH_ID: {
                if (code == CODE_ACCOUNT_WITH_ID) {
                    selection = "_id=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }

                int v1 = writableDatabase.update("account", values, selection, selectionArgs);
//                if((values.containsKey("morning_customized_time")) || (values.containsKey("afternoon_customized_time")) || (values.containsKey("evening_customized_time"))) {
//                    Cursor cursor = writableDatabase.query("account", new String[]{ "_id" }, selection, selectionArgs, null, null, null);
//                    if(cursor != null) {
//                        ArrayList arrayList0 = new ArrayList();
//                        try {
//                            while(cursor.moveToNext()) {
//                                arrayList0.add(cursor.getLong(0));
//                            }
//                        }
//                        catch(Throwable ignored) {
//                        }
//                        cursor.close();
//                    }
//                }

                return v1;
            }
            case CODE_REMINDERS:
            case CODE_REMINDERS_WITH_ID: {
                if (code == CODE_REMINDERS_WITH_ID) {
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                    selection = "_id=?";
                }

                return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
            }
            case CODE_REMINDERS_REFRESH_DUE_DATE: {
                Cursor cursor = writableDatabase.query(RemindersDatabaseHelper.REMINDERS_TABLE, new String[]{"_id", "account_id", "due_date_year", "due_date_month", "due_date_day", "due_date_hour", "due_date_minute", "due_date_second", "due_date_period", "due_date_absolute_time_ms"}, null, null, null, null, null);
                if (cursor == null) {
                    return 0;
                }

                try {
                    cursor.moveToFirst();
                    while (cursor.moveToNext()) {

                        long id = cursor.getLong(0);
                        int accountId = cursor.getInt(1);
                        Long millis = convertDueDateToMillis(
                                cursor.getInt(2),
                                cursor.getInt(3),
                                cursor.getInt(4),
                                cursor.getInt(5),
                                cursor.getInt(6),
                                cursor.getInt(7),
                                cursor.getInt(8),
                                cursor.getLong(9),
                                getSnoozed(writableDatabase).get(accountId));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("due_date_millis", millis);
                        cursor.close();
                        return writableDatabase.update(RemindersDatabaseHelper.REMINDERS_TABLE, contentValues, "_id=?", new String[]{String.valueOf(id)});
                    }
                } catch (Throwable e) {
                    Log.w(TAG, e);
                }

            }
            case CODE_REMINDERS_MARK_EXCEPTIONAL: {
                Boolean exceptional = values.getAsBoolean("recurrence_exceptional");
                if (exceptional == null || !exceptional) {
                    Cursor cursor = writableDatabase.query(RemindersDatabaseHelper.REMINDERS_TABLE, null, selection, selectionArgs, null, null, null);
                    if (cursor == null) {
                        return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                    }

                    try {
                        if (cursor.getCount() == 1) {
                            cursor.moveToFirst();
                            int recurrenceExceptionalColumnIndex = cursor.getColumnIndex("recurrence_exceptional");
                            int recurrenceIdColumnIndex = cursor.getColumnIndex("recurrence_id");
                            int recurrenceMasterColumnIndex = cursor.getColumnIndex("recurrence_master");
                            if (cursor.getInt(Math.max(recurrenceExceptionalColumnIndex, 0)) != 1 && cursor.getString(Math.max(recurrenceIdColumnIndex, 0)) != null && cursor.getInt(Math.max(recurrenceMasterColumnIndex, 0)) != 1) {
                                Boolean dueDateUnspecifiedFutureTime = values.getAsBoolean("due_date_unspecified_future_time");
                                if (dueDateUnspecifiedFutureTime == null || dueDateUnspecifiedFutureTime == Boolean.FALSE) {
                                    Long dueDateMillis = values.getAsLong("due_date_millis");
                                    if (dueDateMillis != null) {
                                        if (dueDateMillis >= System.currentTimeMillis()) {
                                            values.put("recurrence_exceptional", Boolean.TRUE);
                                            cursor.close();
                                            return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                                        }
                                    }

                                    for (String remindersField : remindersFields) {
                                        if (!remindersField.equals("recurrence_exceptional") && !remindersField.equals("recurrence_id") && !remindersField.equals("recurrence_master") && !remindersField.equals("archived") && !remindersField.equals("archived_time_ms") && !remindersField.equals("deleted")) {
                                            int remindersFieldColumnIndex = cursor.getColumnIndex(remindersField);
                                            switch (cursor.getType(remindersFieldColumnIndex)) {
                                                case Cursor.FIELD_TYPE_NULL: {
                                                    if (values.get(remindersField) == null) {
                                                        continue;
                                                    }

                                                    values.put("recurrence_exceptional", Boolean.TRUE);
                                                    cursor.close();
                                                    return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                                                }
                                                case Cursor.FIELD_TYPE_INTEGER: {
                                                    Object object = values.get(remindersField);
                                                    Long longValue;
                                                    if (((object instanceof Boolean))) {
                                                        longValue = (Boolean) object ? 1L : 0L;
                                                    } else {
                                                        longValue = values.getAsLong(remindersField);
                                                    }

                                                    if (longValue == null || (longValue.equals(cursor.getLong(remindersFieldColumnIndex)))) {
                                                        continue;
                                                    }

                                                    values.put("recurrence_exceptional", Boolean.TRUE);
                                                    cursor.close();
                                                    return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                                                }
                                                case Cursor.FIELD_TYPE_FLOAT: {
                                                    Float floatValue = values.getAsFloat(remindersField);
                                                    if (floatValue == null || (floatValue.equals(cursor.getFloat(remindersFieldColumnIndex)))) {
                                                        continue;
                                                    }

                                                    values.put("recurrence_exceptional", Boolean.TRUE);
                                                    cursor.close();
                                                    return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                                                }
                                                case Cursor.FIELD_TYPE_STRING: {
                                                    String stringValue = values.getAsString(remindersField);
                                                    if (stringValue == null || (stringValue.equals(cursor.getString(remindersFieldColumnIndex)))) {
                                                        continue;
                                                    }

                                                    values.put("recurrence_exceptional", Boolean.TRUE);
                                                    cursor.close();
                                                    return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                                                }
                                                case Cursor.FIELD_TYPE_BLOB: {
                                                    byte[] arrayValue = values.getAsByteArray(remindersField);
                                                    if (arrayValue != null && (Arrays.equals(arrayValue, cursor.getBlob(remindersFieldColumnIndex)))) {
                                                        values.put("recurrence_exceptional", Boolean.TRUE);
                                                        cursor.close();
                                                        return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    values.put("recurrence_exceptional", Boolean.TRUE);
                                }
                            }
                        }
                    } catch (Throwable throwable2) {
                        cursor.close();
                        throw throwable2;
                    }

                    cursor.close();
                    return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
                }

                return checkAndUpdate(values, selection, selectionArgs, writableDatabase);
            }
            case CODE_REMINDERS_UPDATE_FIRED_WITH_ID: {
                String notificationId = uri.getLastPathSegment();
                Cursor cursor = writableDatabase.query("reminders LEFT OUTER JOIN notification ON reminders._id = notification._id", new String[]{"state"}, "notification._id=?", new String[]{notificationId}, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            int anInt = cursor.getInt(0);
                            if (anInt != 2) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("state", 2);
                                contentValues.put("fire_time", System.currentTimeMillis());
                                writableDatabase.update(RemindersDatabaseHelper.NOTIFICATION_TABLE, contentValues, "_id=?", new String[]{notificationId});
                                this.n.add(Long.valueOf(notificationId));
                                return m("_id=?", new String[]{notificationId}, writableDatabase);
                            }
                        }
                    } catch (Throwable throwable3) {
                        Log.w(TAG, throwable3);
                    } finally {
                        cursor.close();
                    }
                }
                return 0;
            }

            case CODE_REMINDERS_UPDATE_BUMPED: {
                return this.m(selection, selectionArgs, writableDatabase);
            }
            case CODE_NOTIFICATION:
            case CODE_NOTIFICATION_WITH_ID: {
                if (code == CODE_NOTIFICATION_WITH_ID) {
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                    selection = "_id=?";
                }

                if (values.containsKey("state")) {
                    values.remove("state");
                }

                return values.size() == 0 ? 0 : writableDatabase.update(RemindersDatabaseHelper.NOTIFICATION_TABLE, values, selection, selectionArgs);
            }
            case CODE_OPERATIONS:
            case CODE_OPERATIONS_WITH_ID: {
                if (code == CODE_OPERATIONS_WITH_ID) {
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                    selection = "_id=?";
                }

                return writableDatabase.update(RemindersDatabaseHelper.OPERATION_TABLE, values, selection, selectionArgs);
            }
            default: {
                return 0;
            }
        }
    }

    private int m(String selection, String[] selectionArgs, SQLiteDatabase db) {
        Cursor cursor = db.query(RemindersDatabaseHelper.REMINDERS_TABLE, new String[]{"recurrence_id", "recurrence_exceptional", "due_date_millis", "account_id"}, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String recurrenceId = cursor.getString(0);
                if (recurrenceId != null && cursor.getInt(1) != 1) {
                    long dueDateMillis = cursor.getLong(2);
                    String accountId = cursor.getString(3);
                    selection = "((recurrence_id=? AND account_id=?) AND (due_date_millis>" + dueDateMillis + ") AND (recurrence_exceptional IS NULL OR recurrence_exceptional!=1)";
                    selectionArgs = new String[]{recurrenceId, accountId};

                    Cursor remindersCursor = db.query(RemindersDatabaseHelper.REMINDERS_TABLE, new String[]{"due_date_year", "due_date_month", "due_date_day", "due_date_hour", "due_date_minute", "due_date_second", "due_date_period", "due_date_absolute_time_ms"}, selection, selectionArgs, null, null, "due_date_millis ASC", "1");

                    if (remindersCursor != null) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("due_date_year", remindersCursor.getInt(0));
                        contentValues.put("due_date_month", remindersCursor.getInt(1));
                        contentValues.put("due_date_day", remindersCursor.getInt(2));
                        contentValues.put("due_date_hour", remindersCursor.getInt(3));
                        contentValues.put("due_date_minute", remindersCursor.getInt(4));
                        contentValues.put("due_date_second", remindersCursor.getInt(5));
                        contentValues.put("due_date_period", remindersCursor.getInt(6));
                        contentValues.put("due_date_absolute_time_ms", remindersCursor.getLong(7));
                        checkAndUpdate(contentValues, "(recurrence_id=? AND account_id=?) AND (recurrence_master=1)", selectionArgs, db);
                        remindersCursor.close();
                    }
                }
            }
            cursor.close();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("snoozed", Boolean.FALSE);
        contentValues.put("pinned", Boolean.TRUE);
        contentValues.put("fired_time_millis", System.currentTimeMillis());
        return checkAndUpdate(contentValues, selection, selectionArgs, db);
    }

    private int checkAndUpdate(ContentValues contentValues, String selection, String[] selectionArgs, SQLiteDatabase db) {
        validateAndUpdateTimeValues(contentValues);

        Cursor cursor;
        cursor = db.query(RemindersDatabaseHelper.REMINDERS_TABLE,
                new String[]{"_id"},
                selection,
                selectionArgs,
                null,
                null,
                null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    Long longValue = cursor.getLong(0);
                    this.idList.add(longValue);
                }
            } catch (Throwable e) {
                throw e;
            } finally {
                cursor.close();
            }
        }

        cursor = db.query(true,
                RemindersDatabaseHelper.REMINDERS_TABLE,
                new String[]{"account_id"},
                selection,
                selectionArgs,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            ArrayList<Integer> accountIdList = new ArrayList<>();
            try {
                while (cursor.moveToNext()) {
                    accountIdList.add(cursor.getInt(0));
                }
            } catch (Throwable e) {
                throw e;
            } finally {
                cursor.close();
            }
            for (Integer accountId : accountIdList) {
                contentValues.remove("due_date_millis");
                checkDueDate(db, accountId, contentValues);
                if ((Boolean.TRUE.equals(contentValues.getAsBoolean("archived"))) && contentValues.getAsLong("archived_time_ms") == null) {
                    ContentValues archivedTimeMs = new ContentValues();
                    archivedTimeMs.put("archived_time_ms", System.currentTimeMillis());
                    db.update(RemindersDatabaseHelper.REMINDERS_TABLE, archivedTimeMs, "(" + selection + ") AND (" + "archived=1)", selectionArgs);
                    archivedTimeMs.remove("archived_time_ms");
                }

                return db.update(RemindersDatabaseHelper.REMINDERS_TABLE, contentValues, selection, selectionArgs);
            }
        }
        return 0;
    }
}
