package org.microg.gms.reminders.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.wire.GrpcClient;

import org.microg.gms.reminders.InstanceRegistry;
import org.microg.gms.reminders.RemindersServiceImpl;
import org.microg.gms.reminders.provider.RemindersProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class RemindersSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = RemindersSyncAdapter.class.getSimpleName();
    private final Context mContext;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static final int OPERATION_API_CREATE_REMINDER = 1;
    public static final int OPERATION_API_CREATE_RECURRENCE_REMINDER = 2;
    public static final int OPERATION_API_DELETE_REMINDER = 3;
    public static final int OPERATION_API_DELETE_RECURRENCE_REMINDER = 4;
    public static final int OPERATION_API_UPDATE_REMINDER = 5;
    public static final int OPERATION_API_UPDATE_RECURRENCE_REMINDER = 9;
    public RemindersSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync: ");
        ContentResolver contentResolver = mContext.getContentResolver();
        @SuppressLint("Recycle")
        Cursor cursor = contentResolver.query(RemindersProvider.OPERATIONS_URI, new String[]{"_id", "operation_api",
                "operation_request", "error_count"}, "account_id=?",
                new String[]{String.valueOf(RemindersServiceImpl.getAccountId(mContext, account.name))}, "_id");
        if (cursor == null) {
            return;
        }
        
        AccountManager manager = AccountManager.get(mContext);
        final String[] token = {""};
        manager.getAuthToken(account, "oauth2:https://www.googleapis.com/auth/reminders", true, future -> {
            try {
                token[0] = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                executor.execute(() -> executeTask(cursor, account, contentResolver, syncResult, token[0]));
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            }

        }, null);
    }
    
    private void executeTask(Cursor cursor, Account account, ContentResolver contentResolver, SyncResult syncResult, String token) {
        Log.d(TAG, "executeTask:");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            int operationApi = cursor.getInt(1);
            byte[] operationRequest = cursor.getBlob(2);
            Object operationObject;
            try {
                operationObject = decodeWireProtoBuf(operationApi, operationRequest);
            } catch (InvalidProtocolBufferException e) {
                Log.w(TAG, Arrays.toString(e.getStackTrace()));
                continue;
            }
            if (operationObject == null) {
                continue;
            }
            switch (operationApi) {
                case 0:
                    Log.d(TAG, "unimplemented synchronous operation 0");
                    break;
                case OPERATION_API_CREATE_REMINDER:
                    try {
                        CreateTaskResponse createTaskResponse = getTasksApiServStub(token).CreateTask().executeBlocking((CreateTaskRequest) operationObject);
                        String info = "";
                        if (createTaskResponse.synthesizedVersionInfo != null && createTaskResponse.synthesizedVersionInfo.info != null) {
                            info = createTaskResponse.synthesizedVersionInfo.info;
                        }

                        try {
                            if (createTaskResponse.assignInfo != null && ClientIdCallbackSaved.get(createTaskResponse.assignInfo.clientAssignedId) != null) {
                                ClientIdCallbackSaved.get(createTaskResponse.assignInfo.clientAssignedId).onString(createTaskResponse.assignInfo.clientAssignedId, info);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ++syncResult.stats.numInserts;
                    break;
                case OPERATION_API_CREATE_RECURRENCE_REMINDER:
                    try{
                        getTasksApiServStub(token).CreateRecurrence().executeBlocking((CreateRecurrenceRequest) operationObject);
                        ++syncResult.stats.numInserts;
                    }catch (Exception e){
                        Log.w(TAG, "executeTask: ", e);
                    }
                    break;
                case OPERATION_API_DELETE_REMINDER:
                    try {
                        getTasksApiServStub(token).DeleteTask().executeBlocking((DeleteTaskRequest) operationObject);
                        ++syncResult.stats.numDeletes;
                        for (AssignInfo item : ((DeleteTaskRequest) operationObject).assignInfo) {
                            contentResolver.delete(
                                    RemindersProvider.REMINDERS_URI, "client_assigned_id=?", new String[]{item.clientAssignedId}
                            );
                        }
                    } catch (Exception e) {
                        Log.w(TAG, e);
                    }
                    break;
                case OPERATION_API_DELETE_RECURRENCE_REMINDER:
                    try {
                        getTasksApiServStub(token).DeleteRecurrence().executeBlocking((DeleteRecurrenceRequest) operationObject);
                        ++syncResult.stats.numDeletes;
                    }catch (Exception e){
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                    break;
                case OPERATION_API_UPDATE_REMINDER:
                    try {
                        getTasksApiServStub(token).UpdateTask().executeBlocking((UpdateTaskRequest) operationObject);
                        ++syncResult.stats.numUpdates;
                    } catch (Exception e) {
                        Log.w(TAG, e);
                    }
                    break;
                case OPERATION_API_UPDATE_RECURRENCE_REMINDER:
                    try{
                        getTasksApiServStub(token).ChangeRecurrence().executeBlocking((ChangeRecurrenceRequest) operationObject);
                        ++syncResult.stats.numUpdates;
                    }catch (Exception e){
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                    break;
                default:
                    return;
            }
            contentResolver.delete(ContentUris.withAppendedId(RemindersProvider.OPERATIONS_URI, id), null, null);
        }
        initialSync(account, token);
        cursor.close();
    }

    private Object decodeWireProtoBuf(int operationApi, byte[] operationRequest) throws InvalidProtocolBufferException {
        switch (operationApi) {
            case 0:
                break;
            case OPERATION_API_CREATE_REMINDER://createTask
                return RemindersRequestHelper.getCreateTaskRequest(operationRequest);
            case OPERATION_API_CREATE_RECURRENCE_REMINDER:
                return RemindersRequestHelper.getCreateRecurrenceRequest(operationRequest);
            case OPERATION_API_DELETE_REMINDER:
                return RemindersRequestHelper.getDeleteTaskRequest(operationRequest);
            case OPERATION_API_DELETE_RECURRENCE_REMINDER:
                return RemindersRequestHelper.getDeleteRecurrenceRequest(operationRequest);
            case OPERATION_API_UPDATE_REMINDER:
                return RemindersRequestHelper.getUpdateTaskRequest(operationRequest);
            case OPERATION_API_UPDATE_RECURRENCE_REMINDER:
                return RemindersRequestHelper.getChangeRecurrenceRequest(operationRequest);
        }
        return null;
    }


    private void initialSync(Account account, String token) {
        Log.d(TAG, "initialSync: ");
        TasksApiServiceClient taskStub = getTasksApiServStub(token);

        TaskRequest request = InstanceRegistry.getInstance(TaskRequest.class).newBuilder()
                .httpHeaderInfo(InstanceRegistry.getInstance(HttpHeaderInfo.class).newBuilder()
                        .httpHeader(RemindersRequestHelper.getHttpHeader("Reminders-Android")).build())
                .g(true)
                .build();

        TaskResponse response;
        try {
            response = taskStub.ListTasks().executeBlocking(request);
            Log.d(TAG, "initialSync response: " + response);
        } catch (Exception e) {
            return;
        }

        ArrayList<ContentProviderOperation> insertList = new ArrayList<>();
        long accountId = RemindersServiceImpl.getAccountId(mContext, account.name);
        if (accountId <= 0) {
            return;
        }
        ContentResolver resolver = mContext.getContentResolver();
        for (ReminderInfo item : response.reminderInfo) {
            ContentValues contentValues = new ContentValues();
            putReminderInfo(contentValues, item);
            contentValues.put("account_id", accountId);
            insertList.add(ContentProviderOperation.newInsert(RemindersProvider.REMINDERS_UPSERT_URI).withValues(contentValues).build());
        }
        insertList.add(0, ContentProviderOperation.newAssertQuery(RemindersProvider.OPERATIONS_URI)
                .withSelection("account_id=?", new String[]{String.valueOf(accountId)})
                .withExpectedCount(0).build());
        insertList.add(ContentProviderOperation.newUpdate(ContentUris.withAppendedId(RemindersProvider.ACCOUNT_URI, accountId))
                .withValue("storage_version", response.storageVersion).build());

        try {
            String authority =  insertList.get(0).getUri().getAuthority();
            if (authority != null) {
                resolver.applyBatch(authority, insertList);
            }
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private GrpcTasksApiServiceClient getTasksApiServStub(String token) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .callTimeout(10, TimeUnit.MINUTES)
                .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                .addInterceptor(new HeaderClientInterceptor("Bearer " + token, null))
                .build();

        GrpcClient grpcClient = new GrpcClient.Builder()
                .client(okHttpClient)
                .baseUrl("https://reminders-pa.googleapis.com:443")
                .minMessageToCompress(Long.MAX_VALUE)
                .build();


        return new GrpcTasksApiServiceClient(grpcClient);
    }

    public static void putReminderInfo(ContentValues contentValues, ReminderInfo reminderInfo) {
        Log.d(TAG, "putReminderInfo: " + reminderInfo);
        AssignInfo assignInfo = reminderInfo.assignInfo;

        if (assignInfo != null) {
            contentValues.put("client_assigned_id", assignInfo.clientAssignedId);
            contentValues.put("client_assigned_thread_id", assignInfo.clientAssignedThreadId);
        }

        if (reminderInfo.taskListInfo != null && reminderInfo.taskListInfo.taskList != null) {
            TaskListInfo taskListInfo = reminderInfo.taskListInfo;
            contentValues.put("task_list", taskListInfo.taskList == 0 ? 16 : taskListInfo.taskList);
        }

        contentValues.put("title", reminderInfo.title);
        contentValues.put("created_time_millis", reminderInfo.createdTimeMillis);
        contentValues.put("archived_time_ms", reminderInfo.archivedTimeMs);
        contentValues.put("snoozed_time_millis", reminderInfo.snoozedTimeMillis);
        contentValues.put("location_snoozed_until_ms", reminderInfo.locationSnoozedUntilMs);
        pubBooleanValue(contentValues, "archived", reminderInfo.archived);
        pubBooleanValue(contentValues, "deleted", reminderInfo.deleted);
        pubBooleanValue(contentValues, "pinned", reminderInfo.pinned);
        pubBooleanValue(contentValues, "snoozed", reminderInfo.snoozed);

        putDateTimeEntity(contentValues, reminderInfo.dueDate, "due_date_");

        putDateTimeEntity(contentValues, reminderInfo.eventDate, "event_date_");

        putLocationAddress(contentValues, reminderInfo.locationAddress);

        putLocationGroup(contentValues, reminderInfo.locationGroup);

        putRecurrenceInfo(contentValues, reminderInfo.recurrenceInfo);

        if (reminderInfo.assistance != null) {
            contentValues.put("assistance", reminderInfo.assistance.encode());
        }

        if (reminderInfo.extensions != null) {
            contentValues.put("extensions", reminderInfo.extensions.encode());
        }

        putReminderType(contentValues, reminderInfo);

        putExternalApplicationLink(contentValues, reminderInfo.applicationLink);

        contentValues.put("fired_time_millis", reminderInfo.firedTimeMillis);
        contentValues.put("dirty_sync_bit", Boolean.FALSE);
    }

    private static void putLocationAddress(ContentValues contentValues, LocationAddress locationAddress) {
        if (locationAddress == null) {
            contentValues.putNull("lat");
            contentValues.putNull("lng");
            contentValues.putNull("name");
            contentValues.putNull("radius_meters");
            contentValues.putNull("location_type");
            contentValues.putNull("display_address");
            putDetailedAddress(contentValues, null);
            putFeatureIdProto(contentValues, null, "location_");
            putLocationAliasId(contentValues, null);
        } else {
            contentValues.put("lat", locationAddress.latitude);
            contentValues.put("lng", locationAddress.longitude);
            contentValues.put("name", locationAddress.name);
            contentValues.put("radius_meters", locationAddress.radiusMeters);
            contentValues.put("location_type", (locationAddress.locationType == null || locationAddress.locationType == 0) ? 1 : locationAddress.locationType);

            contentValues.put("display_address", locationAddress.displayAddress);
            putDetailedAddress(contentValues, locationAddress.detailedAddress);
            putFeatureIdProto(contentValues, locationAddress.featureIdProto, "location_");
            putLocationAliasId(contentValues, locationAddress.j);
        }
    }

    private static void putLocationGroup(ContentValues contentValues, LocationGroup locationGroup) {
        if (locationGroup == null) {
            contentValues.putNull("location_query");
            contentValues.putNull("location_query_type");
            putChain(contentValues, null);
            putPlaceCategory(contentValues, null);
        } else {
            contentValues.put("location_query", locationGroup.locationQuery);

            if (locationGroup.locationQueryType == null) {
                contentValues.putNull("location_query_type");
            } else {
                contentValues.put("location_query_type", locationGroup.locationQueryType == 0 ? 1 : locationGroup.locationQueryType);
            }

            putChain(contentValues, locationGroup.chain);
            putPlaceCategory(contentValues, locationGroup.placeCategory);
        }
    }

    private static void putRecurrenceInfo(ContentValues contentValues, RecurrenceInfo recurrenceInfo) {
        if (recurrenceInfo == null) {
            putRecurrenceEntity(contentValues, null);
            putRecurrenceId(contentValues, null);
            contentValues.putNull("recurrence_master");
            contentValues.putNull("recurrence_exceptional");
        } else {
            RecurrenceData recurrenceData = recurrenceInfo.recurrenceData;

            putRecurrenceEntity(contentValues, recurrenceData);

            putRecurrenceId(contentValues, recurrenceInfo.recurrenceIdInfo);
            pubBooleanValue(contentValues, "recurrence_master", recurrenceInfo.recurrenceMaster);
            pubBooleanValue(contentValues, "recurrence_exceptional", recurrenceInfo.recurrenceExceptional);
        }
    }

    private static void putReminderType(ContentValues contentValues, ReminderInfo reminderInfo) {
        if (reminderInfo.dueDate != null) {
            contentValues.put("reminder_type", 1);
        } else if (reminderInfo.locationAddress != null && reminderInfo.locationGroup != null) {
            contentValues.put("reminder_type", 0);
        } else {
            contentValues.put("reminder_type", 2);
        }
    }

    private static void putExternalApplicationLink(ContentValues contentValues, ExternalApplicationLink externalApplicationLink) {
        if (externalApplicationLink == null) {
            contentValues.putNull("link_application");
            contentValues.putNull("link_id");
        } else {
            if (externalApplicationLink.linkApplication != null) {
                contentValues.put("link_application", externalApplicationLink.linkApplication);
            }
            if (externalApplicationLink.linkId != null) {
                contentValues.put("link_id", externalApplicationLink.linkId);
            }
        }
    }

    private static void putRecurrenceId(ContentValues contentValues0, RecurrenceIdInfo recurrenceIdInfo) {
        if (recurrenceIdInfo == null) {
            contentValues0.putNull("recurrence_id");
            return;
        }

        contentValues0.put("recurrence_id", recurrenceIdInfo.recurrenceId);
    }

    private static void putRecurrenceEntity(ContentValues contentValues, RecurrenceData recurrenceEntity) {
        if (recurrenceEntity == null) {
            contentValues.putNull("recurrence_frequency");
            contentValues.putNull("recurrence_every");
            putRecurrenceStart(contentValues, null);
            putRecurrenceEnd(contentValues, null);
            putDailyPattern(contentValues, null);
            putWeeklyPattern(contentValues, null);
            putMonthlyPattern(contentValues, null);
            putYearlyPattern(contentValues, null);
            return;
        }


        contentValues.put("recurrence_frequency", (recurrenceEntity.frequency == null || recurrenceEntity.frequency == 0)
                ? 0 : recurrenceEntity.frequency);

        contentValues.put("recurrence_every", recurrenceEntity.recurrenceEvery);

        putRecurrenceStart(contentValues, recurrenceEntity.recurrenceStart);

        putRecurrenceEnd(contentValues, recurrenceEntity.recurrenceEnd);

        putDailyPattern(contentValues, recurrenceEntity.dailyPattern);

        putWeeklyPattern(contentValues, recurrenceEntity.weeklyPattern);

        putMonthlyPattern(contentValues, recurrenceEntity.monthlyPattern);

        putYearlyPattern(contentValues, recurrenceEntity.yearlyPattern);
    }

    private static void putYearlyPattern(ContentValues contentValues, YearlyPattern yearlyPattern) {
        if (yearlyPattern == null) {
            contentValues.putNull("yearly_pattern_year_month");
            contentValues.putNull("yearly_pattern_monthly_pattern_month_day");
            contentValues.putNull("yearly_pattern_monthly_pattern_week_day");
            contentValues.putNull("yearly_pattern_monthly_pattern_week_day_number");
            return;
        }


        contentValues.put("yearly_pattern_year_month", TextUtils.join(",", yearlyPattern.yearMonth));
        if (yearlyPattern.monthlyPattern != null) {
            MonthlyPattern monthlyPattern = yearlyPattern.monthlyPattern;
            contentValues.put("yearly_pattern_monthly_pattern_month_day", TextUtils.join(",", monthlyPattern.monthDayList));
            if (monthlyPattern.weekDay != null) {
                contentValues.put("yearly_pattern_monthly_pattern_week_day", 1);
            }

            contentValues.put("yearly_pattern_monthly_pattern_week_day_number", monthlyPattern.weekDayNumber);
            return;
        }

        contentValues.putNull("yearly_pattern_monthly_pattern_month_day");
        contentValues.putNull("yearly_pattern_monthly_pattern_week_day");
        contentValues.putNull("yearly_pattern_monthly_pattern_week_day_number");
    }

    private static void putMonthlyPattern(ContentValues contentValues, MonthlyPattern monthlyPattern) {
        if (monthlyPattern == null) {
            contentValues.putNull("monthly_pattern_month_day");
            contentValues.putNull("monthly_pattern_week_day");
            contentValues.putNull("monthly_pattern_week_day_number");
            return;
        }

        contentValues.put("monthly_pattern_month_day", TextUtils.join(",", monthlyPattern.monthDayList));
        if (monthlyPattern.weekDay != null) {
            contentValues.put("monthly_pattern_week_day", monthlyPattern.weekDay);
        }

        contentValues.put("monthly_pattern_week_day_number", monthlyPattern.weekDayNumber);
    }

    private static void putWeeklyPattern(ContentValues contentValues, WeeklyPattern weeklyPattern) {
        if (weeklyPattern == null) {
            contentValues.putNull("weekly_pattern_weekday");
            return;
        }

        contentValues.put("weekly_pattern_weekday", TextUtils.join(",", weeklyPattern.weekday));
    }

    private static void putDailyPattern(ContentValues contentValues, DailyPattern dailyPattern) {
        Boolean isAllDay = null;
        if (dailyPattern == null) {
            putTimeEntity(contentValues, null, "daily_pattern_");
            contentValues.putNull("daily_pattern_period");
            contentValues.putNull("daily_pattern_all_day");
            return;
        }

        putTimeEntity(contentValues, dailyPattern.timeEntity, "daily_pattern_");
        if (dailyPattern.dailyPatternPeriod != null) {
            contentValues.put("daily_pattern_period", (dailyPattern.dailyPatternPeriod == 0 ? 1 : dailyPattern.dailyPatternPeriod));
        }

        if (dailyPattern.isAllDay != null) {
            isAllDay = dailyPattern.isAllDay;
        }

        pubBooleanValue(contentValues, "daily_pattern_all_day", isAllDay);
    }

    private static void putRecurrenceEnd(ContentValues contentValues, RecurrenceEnd recurrenceEnd) {
        Boolean isAutoRenew = null;
        if (recurrenceEnd == null) {
            putDateTimeEntity(contentValues, null, "recurrence_end_");
            contentValues.putNull("recurrence_end_num_occurrences");
            contentValues.putNull("recurrence_end_auto_renew");
            putDateTimeEntity(contentValues, null, "recurrence_end_auto_renew_until_");
            return;
        }

        putDateTimeEntity(contentValues, recurrenceEnd.recurrenceEndTime, "recurrence_end_");
        contentValues.put("recurrence_end_num_occurrences", recurrenceEnd.recurrenceEndNumOccurrences);
        if (recurrenceEnd.isAutoRenew != null) {
            isAutoRenew = recurrenceEnd.isAutoRenew;
        }

        pubBooleanValue(contentValues, "recurrence_end_auto_renew", isAutoRenew);
        putDateTimeEntity(contentValues, recurrenceEnd.recurrenceEndAutoRenewTime, "recurrence_end_auto_renew_until_");
    }

    private static void putRecurrenceStart(ContentValues contentValues, RecurrenceStart recurrenceStart) {
        if (recurrenceStart == null) {
            putDateTimeEntity(contentValues, null, "recurrence_start_");
            return;
        }

        putDateTimeEntity(contentValues, recurrenceStart.protoDateTimeEntity, "recurrence_start_");
    }

    private static void putPlaceCategory(ContentValues contentValues0, PlaceCategory placeCategory) {
        contentValues0.putNull("place_types");
        if (placeCategory == null) {
            contentValues0.putNull("category_id");
            contentValues0.putNull("display_name");
            return;
        }

        contentValues0.put("category_id", placeCategory.categoryId);
        contentValues0.put("display_name", placeCategory.displayName);
        if (placeCategory.placeTypes.size() != 0) {
            contentValues0.put("place_types", TextUtils.join(",", placeCategory.placeTypes));
        }
    }

    private static void putChain(ContentValues contentValues, Chain chain) {
        if (chain == null) {
            contentValues.putNull("chain_name");
            putChainId(contentValues, null);
            return;
        }

        contentValues.put("chain_name", chain.chainName);
        putChainId(contentValues, chain.chainId);
    }


    private static void putLocationAliasId(ContentValues contentValues, LocationAlias locationAlias) {
        if (locationAlias == null) {
            contentValues.putNull("location_alias_id");
            return;
        }

        contentValues.put("location_alias_id", locationAlias.locationAliasId);
    }


    private static void putDetailedAddress(ContentValues contentValues, DetailedAddress detailedAddress) {
        if (detailedAddress == null) {
            contentValues.putNull("address_country");
            contentValues.putNull("address_locality");
            contentValues.putNull("address_region");
            contentValues.putNull("address_street_address");
            contentValues.putNull("address_street_number");
            contentValues.putNull("address_street_name");
            contentValues.putNull("address_postal_code");
            contentValues.putNull("address_name");
            return;
        }

        contentValues.put("address_country", detailedAddress.addressCountry);
        contentValues.put("address_locality", detailedAddress.addressLocality);
        contentValues.put("address_region", detailedAddress.addressRegion);
        contentValues.put("address_street_address", detailedAddress.addressStreetAddress);
        contentValues.put("address_street_number", detailedAddress.addressStreetNumber);
        contentValues.put("address_street_name", detailedAddress.addressStreetName);
        contentValues.put("address_postal_code", detailedAddress.addressPostalCode);
        contentValues.put("address_name", detailedAddress.addressName);

    }

    private static void putDateTimeEntity(ContentValues contentValues, ProtoDateTimeEntity protoDateTimeEntity, String prefix) {
        if (protoDateTimeEntity == null) {
            contentValues.putNull(prefix.concat("year"));
            contentValues.putNull(prefix.concat("month"));
            contentValues.putNull(prefix.concat("day"));
            contentValues.putNull(prefix.concat("period"));
            contentValues.putNull(prefix.concat("absolute_time_ms"));
            putTimeEntity(contentValues, null, prefix);
            contentValues.putNull(prefix.concat("date_range"));
            contentValues.putNull(prefix.concat("unspecified_future_time"));
            contentValues.putNull(prefix.concat("all_day"));
            return;
        }

        contentValues.put(prefix.concat("year"), protoDateTimeEntity.year);
        contentValues.put(prefix.concat("month"), protoDateTimeEntity.month);
        contentValues.put(prefix.concat("day"), protoDateTimeEntity.day);
        if (protoDateTimeEntity.period != null) {
            int period = protoDateTimeEntity.period == 0 ? 1 : protoDateTimeEntity.period;
            contentValues.put(prefix.concat("period"), period);
        }

        contentValues.put(prefix.concat("absolute_time_ms"), protoDateTimeEntity.absoluteTimeMs);
        putTimeEntity(contentValues, protoDateTimeEntity.protoTimeEntity, prefix);
        if (protoDateTimeEntity.dateRange != null) {
            contentValues.put(prefix.concat("date_range"), 1);
        }

        if (protoDateTimeEntity.unspecifiedFutureTime == null) {
            contentValues.putNull(prefix.concat("unspecified_future_time"));
        } else {
            pubBooleanValue(contentValues, prefix.concat("unspecified_future_time"), protoDateTimeEntity.unspecifiedFutureTime);
        }

        if (Boolean.FALSE.equals(protoDateTimeEntity.allDay)) {
            contentValues.putNull(prefix.concat("all_day"));
        } else {
            pubBooleanValue(contentValues, prefix.concat("all_day"), protoDateTimeEntity.allDay);
        }

    }

    private static void putTimeEntity(ContentValues contentValues, ProtoTimeEntity timeEntity, String prefix) {
        if (timeEntity == null) {
            contentValues.putNull(prefix.concat("hour"));
            contentValues.putNull(prefix.concat("minute"));
            contentValues.putNull(prefix.concat("second"));
            return;
        }

        contentValues.put(prefix.concat("hour"), timeEntity.hour);
        contentValues.put(prefix.concat("minute"), timeEntity.minute);
        contentValues.put(prefix.concat("second"), timeEntity.second);
    }

    private static void pubBooleanValue(ContentValues contentValues, String key, Boolean value) {
        contentValues.put(key, Boolean.TRUE.equals(value) ? 1 : 0);
    }


    private static void putChainId(ContentValues contentValues, ChainIdInfo chainIdInfo) {
        if (chainIdInfo == null) {
            putFeatureIdProto(contentValues, null, "chain_id_");
        }
    }

    private static void putFeatureIdProto(ContentValues contentValues, FeatureIdProto featureIdProto, String prefix) {
        if (featureIdProto == null) {
            contentValues.putNull(prefix.concat("cell_id"));
            contentValues.putNull(prefix.concat("fprint"));
            return;
        }

        contentValues.put(prefix.concat("cell_id"), featureIdProto.cellId);
        contentValues.put(prefix.concat("fprint"), featureIdProto.fprint);
    }
}