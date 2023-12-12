package org.microg.gms.reminders.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RemindersDatabaseHelper extends SQLiteOpenHelper {
    private static RemindersDatabaseHelper Inst;

    private static final int DB_VERSION = 42;
    private static final String DB_NAME = "reminders.db";
    public static final String ACCOUNT_TABLE = "account";
    public static final String NOTIFICATION_TABLE = "notification";
    public static final String OPERATION_TABLE = "operation";
    public static final String PLACE_ALIAS_TABLE = "place_alias";
    public static final String REMINDERS_TABLE = "reminders";
    public RemindersDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static RemindersDatabaseHelper getInst(Context context){
        if (Inst == null) {
            Inst = new RemindersDatabaseHelper(context);
        }
        return Inst;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE account (_id INTEGER PRIMARY KEY AUTOINCREMENT,account_name TEXT NOT NULL,storage_version TEXT,sync_status TEXT,morning_customized_time INTEGER,afternoon_customized_time INTEGER,evening_customized_time INTEGER,account_state INTEGER,need_sync_snooze_preset INTEGER NOT NULL DEFAULT 1,was_last_sync_error INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE reminders (_id INTEGER PRIMARY KEY AUTOINCREMENT,account_id INTEGER NOT NULL,reminder_type INTEGER NOT NULL,client_assigned_id TEXT,server_assigned_id INTEGER,client_assigned_thread_id TEXT,task_list INTEGER,title TEXT,created_time_millis INTEGER,archived_time_ms INTEGER,archived INTEGER NOT NULL DEFAULT 0,deleted INTEGER NOT NULL DEFAULT 0,pinned INTEGER NOT NULL DEFAULT 0,snoozed INTEGER NOT NULL DEFAULT 0,snoozed_time_millis INTEGER,location_snoozed_until_ms INTEGER,due_date_year INTEGER,due_date_month INTEGER,due_date_day INTEGER,due_date_hour INTEGER,due_date_minute INTEGER,due_date_second INTEGER,due_date_period INTEGER,due_date_absolute_time_ms INTEGER,due_date_date_range INTEGER,due_date_unspecified_future_time INTEGER,due_date_all_day INTEGER,due_date_millis INTEGER,event_date_year INTEGER,event_date_month INTEGER,event_date_day INTEGER,event_date_hour INTEGER,event_date_minute INTEGER,event_date_second INTEGER,event_date_period INTEGER,event_date_absolute_time_ms INTEGER,event_date_date_range INTEGER,event_date_unspecified_future_time INTEGER,event_date_all_day INTEGER,lat REAL,lng REAL,name TEXT,radius_meters INTEGER,location_type INTEGER,display_address TEXT,address_country TEXT,address_locality TEXT,address_region TEXT,address_street_address TEXT,address_street_number TEXT,address_street_name TEXT,address_postal_code TEXT,address_name TEXT,location_cell_id INTEGER,location_fprint INTEGER,location_alias_id TEXT,location_query TEXT,location_query_type INTEGER,chain_name TEXT,chain_id_cell_id INTEGER,chain_id_fprint INTEGER,category_id TEXT,display_name TEXT,recurrence_id TEXT,recurrence_master INTEGER,recurrence_exceptional INTEGER,recurrence_frequency INTEGER,recurrence_every INTEGER,recurrence_start_year INTEGER,recurrence_start_month INTEGER,recurrence_start_day INTEGER,recurrence_start_hour INTEGER,recurrence_start_minute INTEGER,recurrence_start_second INTEGER,recurrence_start_period INTEGER,recurrence_start_absolute_time_ms INTEGER,recurrence_start_date_range INTEGER,recurrence_start_unspecified_future_time INTEGER,recurrence_start_all_day INTEGER,recurrence_end_year INTEGER,recurrence_end_month INTEGER,recurrence_end_day INTEGER,recurrence_end_hour INTEGER,recurrence_end_minute INTEGER,recurrence_end_second INTEGER,recurrence_end_period INTEGER,recurrence_end_absolute_time_ms INTEGER,recurrence_end_num_occurrences INTEGER,recurrence_end_auto_renew INTEGER,recurrence_end_date_range INTEGER,recurrence_end_unspecified_future_time INTEGER,recurrence_end_all_day INTEGER,recurrence_end_auto_renew_until_year INTEGER,recurrence_end_auto_renew_until_month INTEGER,recurrence_end_auto_renew_until_day INTEGER,recurrence_end_auto_renew_until_hour INTEGER,recurrence_end_auto_renew_until_minute INTEGER,recurrence_end_auto_renew_until_second INTEGER,recurrence_end_auto_renew_until_period INTEGER,recurrence_end_auto_renew_until_absolute_time_ms INTEGER,recurrence_end_auto_renew_until_date_range INTEGER,recurrence_end_auto_renew_until_unspecified_future_time INTEGER,recurrence_end_auto_renew_until_all_day INTEGER,daily_pattern_hour INTEGER,daily_pattern_minute INTEGER,daily_pattern_second INTEGER,daily_pattern_period INTEGER,daily_pattern_all_day INTEGER,weekly_pattern_weekday TEXT,monthly_pattern_month_day TEXT,monthly_pattern_week_day INTEGER,monthly_pattern_week_day_number INTEGER,yearly_pattern_year_month TEXT,yearly_pattern_monthly_pattern_month_day TEXT,yearly_pattern_monthly_pattern_week_day INTEGER,yearly_pattern_monthly_pattern_week_day_number INTEGER,experiment INTEGER,assistance BLOB,extensions BLOB,link_application INTEGER,link_id TEXT,fired_time_millis INTEGER,dirty_sync_bit INTEGER NOT NULL DEFAULT 0,place_types TEXT);");
        db.execSQL("CREATE TABLE notification (_id INTEGER PRIMARY KEY AUTOINCREMENT,state INTEGER NOT NULL DEFAULT 0,trigger_time INTEGER,create_time INTEGER,schedule_time INTEGER,fire_time INTEGER,snooze_time INTEGER,dismiss_time INTEGER);");
        db.execSQL("CREATE TABLE operation (_id INTEGER PRIMARY KEY AUTOINCREMENT,account_id INTEGER NOT NULL,operation_api INTEGER NOT NULL,operation_request BLOB,error_count INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE place_alias (_id INTEGER PRIMARY KEY AUTOINCREMENT,account_id INTEGER NOT NULL,alias_id INTEGER,alias_name TEXT NOT NULL,place_id TEXT NOT NULL);");
        createIndex(db, REMINDERS_TABLE, "account_id");
        createIndex(db, REMINDERS_TABLE, "reminder_type");
        createIndex(db, REMINDERS_TABLE, "due_date_millis");
        createIndex(db, REMINDERS_TABLE, "client_assigned_id");
        createIndex(db, REMINDERS_TABLE, "task_list");
        createIndex(db, REMINDERS_TABLE, "created_time_millis");
        createIndex(db, REMINDERS_TABLE, "archived");
        createIndex(db, REMINDERS_TABLE, "deleted");
        createIndex(db, REMINDERS_TABLE, "recurrence_id");
        createIndex(db, REMINDERS_TABLE, "recurrence_master");
        createIndex(db, REMINDERS_TABLE, "recurrence_exceptional");
        createIndex(db, NOTIFICATION_TABLE, "state");
        createIndex(db, OPERATION_TABLE, "account_id");
        db.execSQL("DROP TRIGGER IF EXISTS notification_create_trigger;");
        db.execSQL("CREATE TRIGGER notification_create_trigger AFTER INSERT  ON reminders BEGIN  INSERT INTO notification ( _id) VALUES ( NEW._id); END");
        db.execSQL("DROP TRIGGER IF EXISTS notification_delete_trigger;");
        db.execSQL("CREATE TRIGGER notification_delete_trigger AFTER DELETE  ON reminders BEGIN  DELETE FROM notification WHERE _id =  OLD._id; END");
        db.execSQL("DROP TRIGGER IF EXISTS notification_update_fire_trigger;");
        db.execSQL("CREATE TRIGGER notification_update_fire_trigger AFTER UPDATE  ON reminders BEGIN  UPDATE notification SET state = 0 WHERE (_id =  NEW._id AND state = 2 AND NEW. pinned != 1) ;END");

    }

    private static void createIndex(SQLiteDatabase db, String tableName, String paramName) {
        String indexName = paramName + tableName + "_" + "_index";
        db.execSQL("DROP INDEX IF EXISTS ".concat(indexName));
        db.execSQL(createIndexStatement(paramName, tableName, indexName));
    }

    private static String createIndexStatement(String paramName, String tableName, String indexName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE INDEX ");
        stringBuilder.append(indexName);
        stringBuilder.append(" ON ");
        stringBuilder.append(tableName);
        stringBuilder.append(" (");
        stringBuilder.append(paramName);
        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
