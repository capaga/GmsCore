package org.microg.gms.reminders;


import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.reminders.CreateReminderOptionsInternal;
import com.google.android.gms.reminders.UpdateRecurrenceOptions;
import com.google.android.gms.reminders.internal.IRemindersCallbacks;
import com.google.android.gms.reminders.model.AddressEntity;
import com.google.android.gms.reminders.model.CategoryInfoEntity;
import com.google.android.gms.reminders.model.ChainInfoEntity;
import com.google.android.gms.reminders.model.CustomizedSnoozePresetEntity;
import com.google.android.gms.reminders.model.DailyPatternEntity;
import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.ExternalApplicationLinkEntity;
import com.google.android.gms.reminders.model.FeatureIdProtoEntity;
import com.google.android.gms.reminders.model.LocationEntity;
import com.google.android.gms.reminders.model.LocationGroupEntity;
import com.google.android.gms.reminders.model.MonthlyPatternEntity;
import com.google.android.gms.reminders.model.RecurrenceEndEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;
import com.google.android.gms.reminders.model.RecurrenceInfoEntity;
import com.google.android.gms.reminders.model.RecurrenceStartEntity;
import com.google.android.gms.reminders.model.TaskEntity;
import com.google.android.gms.reminders.model.TaskIdEntity;
import com.google.android.gms.reminders.model.TimeEntity;
import com.google.android.gms.reminders.model.WeeklyPatternEntity;
import com.google.android.gms.reminders.model.YearlyPatternEntity;
import com.google.protobuf.InvalidProtocolBufferException;

import org.microg.gms.reminders.provider.RemindersProvider;
import org.microg.gms.reminders.sync.AssignInfo;
import org.microg.gms.reminders.sync.Assistance;
import org.microg.gms.reminders.sync.Chain;
import org.microg.gms.reminders.sync.ChainIdInfo;
import org.microg.gms.reminders.sync.ClientIdCallbackSaved;
import org.microg.gms.reminders.sync.CreateReminderOptions;
import org.microg.gms.reminders.sync.CreateReminderOptionsInfo;
import org.microg.gms.reminders.sync.CreateTaskRequest;
import org.microg.gms.reminders.sync.DailyPattern;
import org.microg.gms.reminders.sync.DetailedAddress;
import org.microg.gms.reminders.sync.Extensions;
import org.microg.gms.reminders.sync.ExternalApplicationLink;
import org.microg.gms.reminders.sync.FeatureIdProto;
import org.microg.gms.reminders.sync.HttpHeaderInfo;
import org.microg.gms.reminders.sync.LocationAddress;
import org.microg.gms.reminders.sync.LocationAlias;
import org.microg.gms.reminders.sync.LocationGroup;
import org.microg.gms.reminders.sync.MonthlyPattern;
import org.microg.gms.reminders.sync.PlaceCategory;
import org.microg.gms.reminders.sync.ProtoDateTimeEntity;
import org.microg.gms.reminders.sync.ProtoTimeEntity;
import org.microg.gms.reminders.sync.RecurrenceData;
import org.microg.gms.reminders.sync.RecurrenceEnd;
import org.microg.gms.reminders.sync.RecurrenceIdInfo;
import org.microg.gms.reminders.sync.RecurrenceInfo;
import org.microg.gms.reminders.sync.RecurrenceOptions;
import org.microg.gms.reminders.sync.RecurrenceStart;
import org.microg.gms.reminders.sync.ReminderInfo;
import org.microg.gms.reminders.sync.RemindersSyncAdapter;
import org.microg.gms.reminders.sync.TaskListInfo;
import org.microg.gms.reminders.sync.WeeklyPattern;
import org.microg.gms.reminders.sync.YearlyPattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CreateReminder {
	public static final String TAG = CreateReminder.class.getSimpleName();
	private static final int MILLISECONDS_PER_DAY = 86400000;
	private static final int MILLISECONDS_PER_HOUR = 3600000;
	private static final int MILLISECONDS_PER_MINUTE = 60000;
	private static final int MILLISECONDS_PER_SECOND = 1000;
	private final CreateReminderOptionsInternal option;
	protected IRemindersCallbacks callbacks;
	protected final TaskEntity task;
	protected final Context context;
	private final Account account;
	protected final Long accountId;
	private String clientAssignedId;


	public CreateReminder(Context context, Account account, IRemindersCallbacks callbacks, TaskEntity task, CreateReminderOptionsInternal option) {
		this.context = context;
		this.account = account;
		this.callbacks = callbacks;
		this.task = task;
		this.option = option;
		this.accountId = RemindersServiceImpl.getAccountId(context, account.name);
	}

	public void exe() throws InvalidProtocolBufferException {
		Log.d(TAG, "exe: ");
		ArrayList<ContentProviderOperation> operations = new ArrayList<>();
		remindersOperations(operations);
		operation(operations);
		String authority = (operations.get(0)).getUri().getAuthority();
		int status = -1;
		try {
			if (authority != null) {
				context.getContentResolver().applyBatch(authority, operations);
				status = 0;
				callbacks.onStatus(new Status(status));
			}
		} catch (RemoteException remoteException0) {
			throw new AssertionError(remoteException0);
		} catch (OperationApplicationException operationApplicationException0) {
			Log.w(TAG, "Error applying batch operation: ".concat(operationApplicationException0.toString()));
		}
		Log.d(TAG, "exe status:" + status);
		if (status == 0) {
			Bundle bundle = new Bundle();
			bundle.putBoolean("reminders_upload_sync", true);
			bundle.putBoolean("expedited", true);
			bundle.putBoolean("ignore_settings", true);
			bundle.putBoolean("ignore_backoff", true);
			bundle.putBoolean("force", true);
			ContentResolver.requestSync(account, "com.google.android.gms.reminders", bundle);
		}
		if (clientAssignedId != null) {
			ClientIdCallbackSaved.put(clientAssignedId, callbacks);
		}

	}

	public void operation(ArrayList<ContentProviderOperation> list) {

		AssignInfo assignInfo = null;
		CreateTaskRequest.Builder builder = InstanceRegistry.getInstance(CreateTaskRequest.class).newBuilder();
		ReminderInfo reminderInfo = null;
		try {
			reminderInfo = createReminderInfo(task);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ReminderInfo.Builder reminderInfoBuilder = reminderInfo.newBuilder();

		TaskListInfo taskListInfo = reminderInfoBuilder.taskListInfo == null ? InstanceRegistry.getInstance(TaskListInfo.class) : reminderInfoBuilder.taskListInfo;
		builder.taskListInfo(taskListInfo);

		HttpHeaderInfo httpHeaderInfo = getHttpHeaderInfo();//getaccount
		builder.httpHeaderInfo(httpHeaderInfo);

		if (reminderInfoBuilder.assignInfo != null) {
			assignInfo = InstanceRegistry.getInstance(AssignInfo.class).newBuilder()
					.clientAssignedId(clientAssignedId)
					.build();
		} else {
			reminderInfoBuilder.assignInfo = InstanceRegistry.getInstance(AssignInfo.class);
		}

		builder.assignInfo(assignInfo);
		reminderInfoBuilder.deleted(false);
		builder.reminderInfo(reminderInfoBuilder.build());


		builder.f(option.d);

		CreateReminderOptions.Builder optionsBuilder = InstanceRegistry.getInstance(CreateReminderOptions.class)
				.newBuilder()
				.b(option.b)
				.c(option.c);

		CreateReminderOptionsInfo.Builder optionsInfoBuilder = InstanceRegistry.getInstance(CreateReminderOptionsInfo.class).newBuilder()
				.options(optionsBuilder.build());
		builder.optionInfo(optionsInfoBuilder.build());

		list.add(ContentProviderOperation.newInsert(RemindersProvider.OPERATIONS_URI)
				.withValue("account_id", accountId).withValue("operation_api", RemindersSyncAdapter.OPERATION_API_CREATE_REMINDER)
				.withValue("operation_request", builder.build().encode()).build());
	}

	public HttpHeaderInfo getHttpHeaderInfo() {
		return InstanceRegistry.getInstance(HttpHeaderInfo.class).newBuilder()
				.httpHeader(account.name).build();
	}

	public void remindersOperations(ArrayList<ContentProviderOperation> list) {
		ContentValues content = new ContentValues();
		handleTask(content, task);
		content.remove("due_date_millis");
		content.remove("fired_time_millis");
		content.put("dirty_sync_bit", false);

		clientAssignedId = content.getAsString("client_assigned_id");
		if (TextUtils.isEmpty(clientAssignedId)) {
			content.put("client_assigned_id", "e4921df8-7df4-46fb-b647-e18c36b19cc8");
		}

		content.put("account_id", accountId);
		content.put("snoozed", snoozedCheck(task, false));
		if (content.getAsLong("created_time_millis") == null) {
			content.put("created_time_millis", System.currentTimeMillis());
		}

		list.add(ContentProviderOperation.newAssertQuery(RemindersProvider.REMINDERS_URI).withSelection("client_assigned_id=? AND account_id=? AND deleted=?", new String[]{clientAssignedId, String.valueOf(accountId), "0"}).withExpectedCount(0).build());
		list.add(ContentProviderOperation.newInsert(RemindersProvider.REMINDERS_URI).withValues(content).build());
	}

	protected String[] getSelectionArgs(String recurrenceId, UpdateRecurrenceOptions options) {
		int v = options.isDateFiltered == 1 ? 1 : 0;
		String[] selectionArgs = options.isRecurrenceExceptional ? mergeSelectionArgs(new String[]{String.valueOf(accountId), recurrenceId}, new String[]{"0"}) : new String[]{String.valueOf(accountId), recurrenceId};
		return v == 0 ? selectionArgs : mergeSelectionArgs(selectionArgs, new String[]{String.valueOf(options.recurrenceEndMillis == null ? System.currentTimeMillis() : options.recurrenceEndMillis)});
	}

	private String[] mergeSelectionArgs(String[] selectionArg, String[] otherSelectionArg) {
		if (selectionArg == null || selectionArg.length == 0) {
			return otherSelectionArg;
		}

		if (otherSelectionArg == null || otherSelectionArg.length == 0) {
			return selectionArg;
		}

		String[] mergeSelectArgs = new String[selectionArg.length + otherSelectionArg.length];
		System.arraycopy(selectionArg, 0, mergeSelectArgs, 0, selectionArg.length);
		System.arraycopy(otherSelectionArg, 0, mergeSelectArgs, selectionArg.length, otherSelectionArg.length);
		return mergeSelectArgs;
	}

	protected void updateRecurrenceById(ArrayList<ContentProviderOperation> list, String recurrenceId, UpdateRecurrenceOptions updateRecurrenceOptions) {
		if(updateRecurrenceOptions.isDateFiltered != 0 && updateRecurrenceOptions.recurrenceEndMillis != null) {
			String[] selectionArgs = {String.valueOf(accountId), recurrenceId, "1", "0"};
			ContentValues contentValues = new ContentValues();
			handleDateTime(contentValues, createDateTimeEntityWithOffset(updateRecurrenceOptions.recurrenceEndMillis, TimeZone.getDefault()), "recurrence_end_");
			list.add(ContentProviderOperation.newUpdate(RemindersProvider.REMINDERS_URI)
					.withValues(contentValues).withSelection("account_id=? AND recurrence_id=? AND recurrence_master=? AND deleted=?", selectionArgs)
					.build());
		}
	}

	protected DateTimeEntity createDateTimeEntityWithOffset(long timeInMillis, TimeZone timeZone) {
		long offsetInMillis = timeZone.getOffset(timeInMillis);
		Calendar calendar = CalendarUtils.getCalendarUTC();
		calendar.setTimeInMillis(timeInMillis + offsetInMillis);
		DateTimeEntity dateTimeEntity = CalendarUtils.extractDateFromCalendar(calendar);
		dateTimeEntity.timeEntity = new TimeEntity(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return dateTimeEntity;
	}

	protected String getSelectionByOptions(UpdateRecurrenceOptions options) {
		String s = options.isRecurrenceExceptional ? "(account_id=? AND recurrence_id=?) And (recurrence_exceptional=?)" : "account_id=? AND recurrence_id=?";
		return options.isDateFiltered == 1 ? "(" + s + ") And (due_date_millis>=?)" : s;
	}

	public AssignInfo createAssignInfo(TaskIdEntity taskIdEntity) {
		if (taskIdEntity == null) {
			return null;
		}

		AssignInfo.Builder builder = InstanceRegistry.getInstance(AssignInfo.class).newBuilder();
		if (!TextUtils.isEmpty(taskIdEntity.clientAssignedId)) {
			builder.clientAssignedId(taskIdEntity.clientAssignedId);
		}

		if (!TextUtils.isEmpty(taskIdEntity.clientAssignedThreadId)) {
			builder.clientAssignedThreadId(taskIdEntity.clientAssignedThreadId);
		}

		return builder.build();
	}

	public ReminderInfo createReminderInfo(TaskEntity taskEntity) throws IOException {
		Log.d(TAG, "createReminderInfo: " + taskEntity);
		if (taskEntity == null) {
			return null;
		}

		ReminderInfo.Builder reminderInfoBuilder = InstanceRegistry.getInstance(ReminderInfo.class).newBuilder();
		AssignInfo assignInfo = createAssignInfo(taskEntity.taskId);
		if (assignInfo != null) {
			reminderInfoBuilder.assignInfo(assignInfo);
		}

		if (taskEntity.taskList != null) {
			if (taskEntity.taskList == 0) {
				TaskListInfo taskListInfo = InstanceRegistry.getInstance(TaskListInfo.class);
				reminderInfoBuilder.taskListInfo(taskListInfo);
			} else {
				TaskListInfo.Builder builder = InstanceRegistry.getInstance(TaskListInfo.class).newBuilder();
				builder.taskList(taskEntity.taskList);
				reminderInfoBuilder.taskListInfo(builder.build());
			}
		}

		ProtoDateTimeEntity dueDateTime = createProtoDateTime(taskEntity.dueDate);
		if (dueDateTime != null) {
			reminderInfoBuilder.dueDate(dueDateTime);
		}

		ProtoDateTimeEntity eventDateTime = createProtoDateTime(taskEntity.eventDate);
		if (eventDateTime != null) {
			reminderInfoBuilder.eventDate(eventDateTime);
		}

		if (!TextUtils.isEmpty(taskEntity.mTitle)) {
			reminderInfoBuilder.title(taskEntity.mTitle);
		}

		if (taskEntity.createdTimeMillis != null) {
			reminderInfoBuilder.createdTimeMillis(taskEntity.createdTimeMillis);
		}

		if (taskEntity.archivedTimeMs != null) {
			reminderInfoBuilder.archivedTimeMs(taskEntity.archivedTimeMs);
		}

		if (taskEntity.archived != null) {
			reminderInfoBuilder.archived(taskEntity.archived);
		}

		if (taskEntity.deleted != null) {
			reminderInfoBuilder.deleted(taskEntity.deleted);
		}

		if (taskEntity.pinned != null) {
			reminderInfoBuilder.pinned(taskEntity.pinned);
		}

		if (taskEntity.snoozed != null) {
			reminderInfoBuilder.snoozed(taskEntity.snoozed);
		}

		if (taskEntity.snoozedTimeMillis != null) {
			reminderInfoBuilder.snoozedTimeMillis(taskEntity.snoozedTimeMillis);
		}

		if (taskEntity.locationSnoozedUntilMs != null) {
			reminderInfoBuilder.locationSnoozedUntilMs(taskEntity.locationSnoozedUntilMs);
		}

		LocationAddress locationAddress = createLocationAddress(taskEntity.locationEntity);
		if (locationAddress != null) {
			reminderInfoBuilder.locationAddress(locationAddress);
		}


		LocationGroup locationGroup = createLocationGroup(taskEntity.locationGroupEntity);
		if (locationGroup != null) {
			reminderInfoBuilder.locationGroup(locationGroup);
		}

		RecurrenceInfo recurrenceInfo = createRecurrenceInfo(taskEntity.recurrenceInfoEntity);
		if (recurrenceInfo != null) {
			reminderInfoBuilder.recurrenceInfo(recurrenceInfo);
		}

		byte[] assistance = taskEntity.assistance;
		if (assistance != null) {
			try {
				reminderInfoBuilder.assistance(Assistance.ADAPTER.decode(assistance));
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}

		byte[] extensions = taskEntity.extensions;
		if (extensions != null) {
			try {
				reminderInfoBuilder.extensions(Extensions.ADAPTER.decode(extensions));
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		ExternalApplicationLink externalApplicationLink = createExternalApplicationLink(taskEntity.externalApplicationLinkEntity);
		if (externalApplicationLink != null) {
			reminderInfoBuilder.applicationLink(externalApplicationLink);
		}

		return reminderInfoBuilder.build();
	}
	private LocationAddress createLocationAddress(LocationEntity locationEntity) {
		if (locationEntity == null) {
			return null;
		}

		LocationAddress.Builder builder = InstanceRegistry.getInstance(LocationAddress.class).newBuilder();
		if (locationEntity.latitude != null) {
			builder.latitude(locationEntity.latitude);
		}

		if (locationEntity.longitude != null) {
			builder.longitude(locationEntity.longitude);
		}

		if (locationEntity.name != null) {
			builder.name(locationEntity.name);
		}

		if (locationEntity.radiusMeters != null) {
			builder.radiusMeters(locationEntity.radiusMeters);
		}

		if (locationEntity.locationType != null && locationEntity.locationType != 0) {
			builder.locationType(locationEntity.locationType);
		}

		if (locationEntity.displayAddress != null) {
			builder.displayAddress(locationEntity.displayAddress);
		}

		DetailedAddress detailedAddress = createDetailedAddress(locationEntity.addressEntity);
		if (detailedAddress != null) {
			builder.detailedAddress(detailedAddress);
		}

		FeatureIdProto featureIdProto = createFeatureIdProto(locationEntity.featureIdProtoEntity);
		if (featureIdProto != null) {
			builder.featureIdProto(featureIdProto);
		}

		if (!TextUtils.isEmpty(locationEntity.locationAliasId)) {
			LocationAlias.Builder locationAliasBuilder = InstanceRegistry.getInstance(LocationAlias.class).newBuilder();
			String s3 = locationEntity.locationAliasId;

			locationAliasBuilder.locationAliasId(s3);
			builder.j(locationAliasBuilder.build());
		}

		return builder.build();
	}

	private LocationGroup createLocationGroup(LocationGroupEntity locationGroupEntity) {
		if (locationGroupEntity == null) {
			return null;
		}
		LocationGroup.Builder builder = InstanceRegistry.getInstance(LocationGroup.class).newBuilder();
		if (locationGroupEntity.locationQuery != null) {
			String s4 = locationGroupEntity.locationQuery;
			builder.locationQuery(s4);
		}

		if (locationGroupEntity.locationQueryType != null) {
			int v7 = locationGroupEntity.locationQueryType;
			if (v7 != 0) {
				builder.locationQueryType(v7);
			}
		}

		Chain chain = createChainInfo(locationGroupEntity.chainInfoEntity);
		if (chain != null) {
			builder.chain(chain);
		}

		PlaceCategory placeCategory = createPlaceCategory(locationGroupEntity.categoryInfoEntity);
		if (placeCategory != null) {
			builder.placeCategory(placeCategory);
		}

		return builder.build();
	}

	private Chain createChainInfo(ChainInfoEntity chainInfoEntity) {
		if (chainInfoEntity == null) {
			return null;
		}
		Chain.Builder builder = InstanceRegistry.getInstance(Chain.class).newBuilder();
		builder.chainName(chainInfoEntity.chainName);

		if (chainInfoEntity.featureIdProtoEntity != null) {
			ChainIdInfo.Builder chainIdInfoBuilder = InstanceRegistry.getInstance(ChainIdInfo.class).newBuilder();
			FeatureIdProto featureIdProto = createFeatureIdProto(chainInfoEntity.featureIdProtoEntity);
			if (featureIdProto != null) {
				chainIdInfoBuilder.featureIdProto(featureIdProto);
			}

			builder.chainId(chainIdInfoBuilder.build());
		}

		return builder.build();
	}

	private RecurrenceInfo createRecurrenceInfo(RecurrenceInfoEntity recurrenceInfoEntity) {
		if (recurrenceInfoEntity == null) {
			return null;
		}
		RecurrenceInfo.Builder builder = InstanceRegistry.getInstance(RecurrenceInfo.class).newBuilder();
		RecurrenceData recurrenceData = createRecurrenceData(recurrenceInfoEntity.recurrenceEntity);
		if (recurrenceData != null) {
			builder.recurrenceData(recurrenceData);
		}

		RecurrenceIdInfo recurrenceIdInfo = createRecurrenceIdInfo(recurrenceInfoEntity.recurrenceId);
		if (recurrenceIdInfo != null) {
			builder.recurrenceIdInfo(recurrenceIdInfo);
		}

		if (recurrenceInfoEntity.isRecurrenceMaster != null) {
			builder.recurrenceMaster(recurrenceInfoEntity.isRecurrenceMaster);
		}

		if (recurrenceInfoEntity.isRecurrenceExceptional != null) {
			builder.recurrenceExceptional(recurrenceInfoEntity.isRecurrenceExceptional);
		}

		return builder.build();
	}

	private RecurrenceData createRecurrenceData(RecurrenceEntity recurrenceEntity) {
		if (recurrenceEntity == null) {
			return null;
		}
		RecurrenceData.Builder builder = InstanceRegistry.getInstance(RecurrenceData.class).newBuilder();
		if (recurrenceEntity.frequency != null) {
			if (recurrenceEntity.frequency >= 0) {
				builder.frequency(recurrenceEntity.frequency);
			}
		}

		if (recurrenceEntity.recurrenceEvery != null) {
			builder.recurrenceEvery(recurrenceEntity.recurrenceEvery);
		}

		RecurrenceStart recurrenceStart = createRecurrenceStart(recurrenceEntity.recurrenceStartEntity);
		if (recurrenceStart != null) {
			builder.recurrenceStart(recurrenceStart);

		}

		RecurrenceEnd recurrenceEnd = createRecurrenceEnd(recurrenceEntity.recurrenceEndEntity);
		if (recurrenceEnd != null) {
			builder.recurrenceEnd(recurrenceEnd);
		}

		DailyPattern dailyPattern = createDailyPattern(recurrenceEntity.dailyPatternEntity);
		if (dailyPattern != null) {
			builder.dailyPattern(dailyPattern);
		}

		WeeklyPattern weeklyPattern = createWeeklyPattern(recurrenceEntity.weeklyPatternEntity);
		if (weeklyPattern != null) {
			builder.weeklyPattern(weeklyPattern);
		}

		MonthlyPattern monthlyPattern = createMonthlyPattern(recurrenceEntity.monthlyPatternEntity);
		if (monthlyPattern != null) {
			builder.monthlyPattern(monthlyPattern);
		}

		YearlyPattern yearlyPattern = createYearlyPattern(recurrenceEntity.yearlyPatternEntity);
		if (yearlyPattern != null) {
			builder.yearlyPattern(yearlyPattern);
		}

		return builder.build();
	}

	private YearlyPattern createYearlyPattern(YearlyPatternEntity yearlyPatternEntity) {
		if (yearlyPatternEntity == null) {
			return null;
		}
		YearlyPattern.Builder builder = InstanceRegistry.getInstance(YearlyPattern.class).newBuilder();
		MonthlyPattern monthlyPattern = createMonthlyPattern(yearlyPatternEntity.monthlyPatternEntity);
		if (monthlyPattern != null) {
			builder.monthlyPattern(monthlyPattern);
		}

		if (yearlyPatternEntity.yearMonth != null) {
			if (builder.yearMonth.isEmpty()) {
				builder.yearMonth = new ArrayList<>();
			}
			builder.yearMonth.addAll(yearlyPatternEntity.yearMonth);
		}

		return builder.build();
	}

	private WeeklyPattern createWeeklyPattern(WeeklyPatternEntity weeklyPatternEntity) {
		if (weeklyPatternEntity == null) {
			return null;
		}
		WeeklyPattern.Builder builder = InstanceRegistry.getInstance(WeeklyPattern.class).newBuilder();
		if (builder.weekday.isEmpty()) {
			builder.weekday = new ArrayList<>();
		}
		builder.weekday.addAll(weeklyPatternEntity.weekday);
		return builder.build();
	}

	private DailyPattern createDailyPattern(DailyPatternEntity dailyPatternEntity) {
		if (dailyPatternEntity == null) {
			return null;
		}
		DailyPattern.Builder builder = InstanceRegistry.getInstance(DailyPattern.class).newBuilder();
		ProtoTimeEntity protoTimeEntity = createProtoTimeEntity(dailyPatternEntity.timeEntity);
		if (protoTimeEntity != null) {
			builder.timeEntity(protoTimeEntity);

		}

		if (dailyPatternEntity.dailyPatternPeriod != 0) {
			builder.dailyPatternPeriod(dailyPatternEntity.dailyPatternPeriod);
		}

		if (dailyPatternEntity.isAllDay != null) {
			builder.isAllDay(dailyPatternEntity.isAllDay);
		}

		return builder.build();
	}

	private RecurrenceEnd createRecurrenceEnd(RecurrenceEndEntity recurrenceEndEntity) {
		if (recurrenceEndEntity == null) {
			return null;
		}
		RecurrenceEnd.Builder builder = InstanceRegistry.getInstance(RecurrenceEnd.class).newBuilder();
		ProtoDateTimeEntity protoDateTime = createProtoDateTime(recurrenceEndEntity.dateTimeEntity);
		if (protoDateTime != null) {
			builder.recurrenceEndTime(protoDateTime);

		}

		if (recurrenceEndEntity.num != null) {
			builder.recurrenceEndNumOccurrences(recurrenceEndEntity.num);
		}

		if (recurrenceEndEntity.isAutoRenew != null) {
			builder.isAutoRenew(recurrenceEndEntity.isAutoRenew);
		}

		ProtoDateTimeEntity protoDateTimeEntity = createProtoDateTime(recurrenceEndEntity.untilDateTimeEntity);
		if (protoDateTimeEntity != null) {
			builder.recurrenceEndAutoRenewTime(protoDateTimeEntity);
		}

		return builder.build();
	}

	private RecurrenceStart createRecurrenceStart(RecurrenceStartEntity recurrenceStartEntity) {
		if (recurrenceStartEntity == null) {
			return null;
		}
		RecurrenceStart.Builder builder = InstanceRegistry.getInstance(RecurrenceStart.class).newBuilder();
		ProtoDateTimeEntity protoDateTimeEntity = createProtoDateTime(recurrenceStartEntity.dateTimeEntity);
		if (protoDateTimeEntity != null) {
			builder.protoDateTimeEntity(protoDateTimeEntity);
		}

		return builder.build();
	}

	private ExternalApplicationLink createExternalApplicationLink(ExternalApplicationLinkEntity externalApplicationLink) {
		if (externalApplicationLink == null) {
			return null;
		}
		ExternalApplicationLink.Builder builder = InstanceRegistry.getInstance(ExternalApplicationLink.class).newBuilder();
		if (externalApplicationLink.applicationLink != null) {
			if (externalApplicationLink.applicationLink != 0) {
				builder.linkApplication(externalApplicationLink.applicationLink);
			}
		}

		if (externalApplicationLink.linkId != null) {
			builder.linkId(externalApplicationLink.linkId);
		}
		return builder.build();
	}

	protected RecurrenceIdInfo createRecurrenceIdInfo(String recurrenceId) {
		if (TextUtils.isEmpty(recurrenceId)) {
			return null;
		}

		RecurrenceIdInfo.Builder builder = InstanceRegistry.getInstance(RecurrenceIdInfo.class).newBuilder();
		builder.recurrenceId(recurrenceId);
		return builder.build();

	}

	protected RecurrenceOptions getRecurrenceOptions(UpdateRecurrenceOptions options) {
		if (options == null) {
			return null;
		}
		return new RecurrenceOptions.Builder()
				.isDateFiltered(options.isDateFiltered != 1 ? 0 : 1)
				.isRecurrenceExceptional(options.isRecurrenceExceptional)
				.recurrenceEndMillis(options.recurrenceEndMillis == null ? System.currentTimeMillis() : options.recurrenceEndMillis).build();
	}

	private static MonthlyPattern createMonthlyPattern(MonthlyPatternEntity monthlyPattern) {
		if (monthlyPattern == null) {
			return null;
		}
		MonthlyPattern.Builder builder = InstanceRegistry.getInstance(MonthlyPattern.class).newBuilder();
		List<Integer> monthDayList = monthlyPattern.dayOfMonthList;
		if (monthDayList != null) {
			if (builder.monthDayList.isEmpty()) {
				builder.monthDayList = new ArrayList<>();
			}
			builder.monthDayList.addAll(monthDayList);
		}

		if (monthlyPattern.weekDay != null) {
			builder.weekDay(monthlyPattern.weekDay);
		}

		if (monthlyPattern.weekOfMonth != null) {
			builder.weekDayNumber(monthlyPattern.weekOfMonth);
		}

		return builder.build();
	}

	private static PlaceCategory createPlaceCategory(CategoryInfoEntity categoryInfo) {
		if (categoryInfo == null) {
			return null;
		}

		PlaceCategory.Builder builder = InstanceRegistry.getInstance(PlaceCategory.class).newBuilder();
		if (!TextUtils.isEmpty(categoryInfo.categoryId)) {
			String s = categoryInfo.categoryId;
			builder.categoryId(s);
		}

		if (!TextUtils.isEmpty(categoryInfo.displayName)) {
			String s1 = categoryInfo.displayName;
			builder.displayName(s1);
		}

		return builder.build();
	}

	private static FeatureIdProto createFeatureIdProto(FeatureIdProtoEntity featureIdProtoEntity) {
		if (featureIdProtoEntity == null) {
			return null;
		}

		FeatureIdProto.Builder builder = InstanceRegistry.getInstance(FeatureIdProto.class).newBuilder();
		builder.cellId(featureIdProtoEntity.cellId);
		builder.fprint(featureIdProtoEntity.fprint);
		return builder.build();
	}

	private static DetailedAddress createDetailedAddress(AddressEntity addressEntity) {
		if (addressEntity == null) {
			return null;
		}

		DetailedAddress.Builder builder = InstanceRegistry.getInstance(DetailedAddress.class).newBuilder();
		if (!TextUtils.isEmpty(addressEntity.addressCountry)) {
			builder.addressCountry(addressEntity.addressCountry);
		}

		if (!TextUtils.isEmpty(addressEntity.addressLocality)) {
			builder.addressLocality(addressEntity.addressLocality);
		}

		if (!TextUtils.isEmpty(addressEntity.addressRegion)) {
			builder.addressRegion(addressEntity.addressRegion);
		}

		if (!TextUtils.isEmpty(addressEntity.addressStreetAddress)) {
			builder.addressStreetAddress(addressEntity.addressStreetAddress);
		}

		if (!TextUtils.isEmpty(addressEntity.addressStreetNumber)) {
			builder.addressStreetNumber(addressEntity.addressStreetNumber);
		}

		if (!TextUtils.isEmpty(addressEntity.addressStreetName)) {
			builder.addressStreetName(addressEntity.addressStreetName);
		}

		if (!TextUtils.isEmpty(addressEntity.addressPostalCode)) {
			builder.addressPostalCode(addressEntity.addressPostalCode);
		}

		if (!TextUtils.isEmpty(addressEntity.addressName)) {
			builder.addressName(addressEntity.addressName);
		}

		return builder.build();
	}

	private ProtoDateTimeEntity createProtoDateTime(DateTimeEntity dateTime) {
		if (dateTime == null) {
			return null;
		}

		ProtoDateTimeEntity.Builder builder = InstanceRegistry.getInstance(ProtoDateTimeEntity.class).newBuilder();
		if (dateTime.year != null) {
			builder.year(dateTime.year);
		}

		if (dateTime.month != null) {
			builder.month(dateTime.month);
		}

		if (dateTime.day != null) {
			builder.day(dateTime.day);
		}

		if (dateTime.period != null && dateTime.period != 0) {
			builder.period(dateTime.period);
		}

		if (dateTime.absoluteTimeMs != null) {
			builder.absoluteTimeMs(dateTime.absoluteTimeMs);
		}

		ProtoTimeEntity protoTimeEntity = createProtoTimeEntity(dateTime.timeEntity);
		if (protoTimeEntity != null) {
			builder.protoTimeEntity(protoTimeEntity);
		}

		if (dateTime.dateRange != null && (dateTime.dateRange == 1 ? 1 : 0) != 0) {
			builder.dateRange(1);
		}

		if (dateTime.unspecifiedFutureTime != null) {
			builder.unspecifiedFutureTime(dateTime.unspecifiedFutureTime);
		}

		if (dateTime.allDay != null) {
			builder.allDay(dateTime.allDay);
		}

		return builder.build();
	}

	private ProtoTimeEntity createProtoTimeEntity(TimeEntity timeEntity) {
		if (timeEntity == null) {
			return null;
		}

		ProtoTimeEntity.Builder builder = InstanceRegistry.getInstance(ProtoTimeEntity.class).newBuilder();
		if (timeEntity.hourOfDay != null) {
			builder.hour(timeEntity.hourOfDay);
		}

		if (timeEntity.minute != null) {
			builder.minute(timeEntity.minute);
		}

		if (timeEntity.second != null) {
			builder.second(timeEntity.second);
		}

		return builder.build();
	}

	public ArrayList<CustomizedSnoozePresetEntity> getCustomizedSnoozePresetInternal() {
		ArrayList<CustomizedSnoozePresetEntity> results = new ArrayList<>();
		CustomizedSnoozePresetEntity customizedSnoozePreset;
		Cursor res = context.getContentResolver().query(RemindersProvider.ACCOUNT_URI,
				new String[]{"morning_customized_time", "afternoon_customized_time", "evening_customized_time"}, "account_name=?", new String[]{account.name}, null);
		if (res != null) {
			res.moveToFirst();
			if (res.moveToNext()) {
				customizedSnoozePreset = new CustomizedSnoozePresetEntity(millis2Time(res.getLong(0)), millis2Time(res.getLong(1)), millis2Time(res.getLong(2)));
				results.add(customizedSnoozePreset);
			}
		}
		if (res != null) {
			res.close();
		}
		return results;
	}

	private TimeEntity millis2Time(long millis) {
		if (millis < 0 || millis >= MILLISECONDS_PER_DAY) {
			return null;
		}

		int hours = (int) (millis / MILLISECONDS_PER_HOUR);
		int remainingMillis = (int) (millis % MILLISECONDS_PER_HOUR);

		int minutes = remainingMillis / MILLISECONDS_PER_MINUTE;
		int seconds = (remainingMillis % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND;

		return new TimeEntity(hours, minutes, seconds);
	}

	private Long time2Mills(DateTimeEntity datetime) {
		if (datetime.absoluteTimeMs != null) {
			return datetime.absoluteTimeMs;
		}

		int year = datetime.year;
		int month = (datetime.month) - 1;
		int day = datetime.day;
		int second, hourOfDay, minute;
		if (datetime.timeEntity != null) {
			hourOfDay = datetime.timeEntity.hourOfDay;
			minute = datetime.timeEntity.minute;
			second = datetime.timeEntity.second;
		} else {
			minute = 0;
			second = 0;
			hourOfDay = 0;
		}

		Calendar calendar = CalendarUtils.getCalendarUTC();

		calendar.set(year, month, day, hourOfDay, minute, second);
		return calendar.getTimeInMillis();
	}

	public boolean snoozedCheck(TaskEntity task, boolean value) {
		if (task.snoozed != null) {
			return task.snoozed;
		}

		if (task.archived != null && task.archived) {
			return false;
		}

		if (task.deleted != null && task.deleted) {
			return false;
		}

		return value || task.dueDate != null && (checkDateSnooze(task.dueDate)) || (task.locationEntity != null || task.locationGroupEntity != null);
	}

	private boolean checkDateSnooze(DateTimeEntity dateTimeEntity) {
		Log.d(TAG, "checkDateSnooze: ");
		DateTimeEntity currentDateTime = millisGetDateTimeEntity(System.currentTimeMillis(),  TimeZone.getDefault());
		TimeEntity time = null;
		if (dateTimeEntity.timeEntity == null && dateTimeEntity.period != null) {
			CustomizedSnoozePresetEntity custom = getCustomizedSnoozePresetInternal().get((accountId.intValue()));
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_use_custom_snooze_preset", false) &&
					custom != null) {
				switch (dateTimeEntity.period) {
					case 1: {
						time = custom.morningCustomizedTime;
						break;
					}
					case 2: {
						time = custom.afternoonCustomizedTime;
						break;
					}
					case 3: {
						time = custom.eveningCustomizedTime;
						break;
					}
					case 4: {
						break;
					}
					default: {
						time = new TimeEntity(0, 0, 0);
						break;
					}
				}
				time = new TimeEntity(0, 0, 0);
			}
			dateTimeEntity.timeEntity = time;
		}

		return Boolean.TRUE.equals(dateTimeEntity.unspecifiedFutureTime) || time2Mills(dateTimeEntity) > time2Mills(currentDateTime);
	}

	private DateTimeEntity millisGetDateTimeEntity(long currentTimeMillis, TimeZone timeZone) {
		Calendar calendar = CalendarUtils.getCalendarUTC();
		calendar.setTimeInMillis(currentTimeMillis + timeZone.getOffset(currentTimeMillis));
		return new DateTimeEntity(
				calendar.get(Calendar.YEAR),
				(calendar.get(Calendar.MONTH) + 1),
				calendar.get(Calendar.DAY_OF_MONTH),
				new TimeEntity(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
				null,
				null,
				null,
				null,
				null,
				true);
	}


	public void handleTask(ContentValues content, TaskEntity task) {
		content.put("client_assigned_id", checkNullStr(task.taskId.clientAssignedId));
		content.put("client_assigned_thread_id", checkNullStr(task.taskId.clientAssignedThreadId));

		content.put("task_list", task.taskList == 0 ? 16 : task.taskList);

		content.put("title", checkNullStr(task.mTitle));
		content.put("created_time_millis", task.createdTimeMillis);
		content.put("archived_time_ms", task.archivedTimeMs);
		content.put("snoozed_time_millis", task.snoozedTimeMillis);
		content.put("location_snoozed_until_ms", task.locationSnoozedUntilMs);
		content.put("archived", task.archived);
		content.put("deleted", task.deleted);
		content.put("pinned", task.pinned == null || task.pinned);
		content.put("snoozed", task.snoozed);

		handleDateTime(content, task.dueDate, "due_date_");
		handleDateTime(content, task.eventDate, "event_date_");

		handleLocation(content, task.locationEntity);
		handleLocationGroup(content, task.locationGroupEntity);

		handleRecurrenceInfo(content, task.recurrenceInfoEntity);

		handleReminderType(content, task);

	}

	private void handleReminderType(ContentValues content, TaskEntity task) {
		if (task.dueDate != null) {
			content.put("reminder_type", 1);
		} else if (task.locationEntity != null && task.locationGroupEntity != null) {
			content.put("reminder_type", 0);
		} else {
			content.put("reminder_type", 2);
		}
	}

	private void handleRecurrenceInfo(ContentValues content, RecurrenceInfoEntity recurrenceInfo) {
		if (recurrenceInfo == null) {
			content.putNull("recurrence_id");

			content.putNull("recurrence_frequency");
			content.putNull("recurrence_every");
			handleDateTime(content, null, "recurrence_start_");

			handleRecurrenceEnd(content, null);

			handleDailyPattern(content, null);

			handleWeeklyPattern(content, null);

			handleMonthlyPattern(content, null);

			handleYearlyPattern(content, null);
			return;
		}

		handleRecurrence(content, recurrenceInfo.recurrenceEntity);
		content.put("recurrence_id", recurrenceInfo.recurrenceId);
		content.put("recurrence_master", recurrenceInfo.isRecurrenceMaster);
		content.put("recurrence_exceptional", recurrenceInfo.isRecurrenceExceptional);
	}

	private void handleRecurrence(ContentValues content, RecurrenceEntity recurrence) {
		if (recurrence == null) {
			content.putNull("recurrence_frequency");
			content.putNull("recurrence_every");
			handleDateTime(content, null, "recurrence_start_");

			handleRecurrenceEnd(content, null);

			handleDailyPattern(content, null);

			handleWeeklyPattern(content, null);

			handleMonthlyPattern(content, null);

			handleYearlyPattern(content, null);
			return;
		}

		content.put("recurrence_frequency", recurrence.frequency);
		content.put("recurrence_every", recurrence.recurrenceEvery);

		//recurrenceStart
		RecurrenceStartEntity recurrenceStart = recurrence.recurrenceStartEntity;
		if (recurrenceStart == null) {
			handleDateTime(content, null, "recurrence_start_");
		} else {
			handleDateTime(content, recurrenceStart.dateTimeEntity, "recurrence_start_");
		}

		//recurrenceEnd
		handleRecurrenceEnd(content, recurrence.recurrenceEndEntity);

		//DailyPattern
		handleDailyPattern(content, recurrence.dailyPatternEntity);

		//WeeklyPattern
		handleWeeklyPattern(content, recurrence.weeklyPatternEntity);

		//MonthlyPattern
		handleMonthlyPattern(content, recurrence.monthlyPatternEntity);

		//YearlyPattern
		handleYearlyPattern(content, recurrence.yearlyPatternEntity);
	}

	private void handleRecurrenceEnd(ContentValues content, RecurrenceEndEntity recurrenceEnd) {
		if (recurrenceEnd == null) {
			handleDateTime(content, null, "recurrence_end_");
			content.putNull("recurrence_end_num_occurrences");
			content.putNull("recurrence_end_auto_renew");
			handleDateTime(content, null, "recurrence_end_auto_renew_until_");
		} else {
			handleDateTime(content, recurrenceEnd.dateTimeEntity, "recurrence_end_");
			content.put("recurrence_end_num_occurrences", recurrenceEnd.num);
			content.put("recurrence_end_auto_renew", recurrenceEnd.isAutoRenew == null ? null : recurrenceEnd.isAutoRenew ? 1 : 0);
			handleDateTime(content, recurrenceEnd.untilDateTimeEntity, "recurrence_end_auto_renew_until_");
		}
	}

	private void handleWeeklyPattern(ContentValues content, WeeklyPatternEntity weeklyPattern) {
		if (weeklyPattern == null || weeklyPattern.weekday == null) {
			content.putNull("weekly_pattern_weekday");
		} else {
			content.put("weekly_pattern_weekday", TextUtils.join(",", weeklyPattern.weekday));
		}
	}

	private void handleYearlyPattern(ContentValues content, YearlyPatternEntity yearlyPattern) {
		if (yearlyPattern == null || (yearlyPattern.monthlyPatternEntity == null &&
				(yearlyPattern.yearMonth == null || yearlyPattern.yearMonth.isEmpty()))) {
			content.putNull("yearly_pattern_year_month");
			content.putNull("yearly_pattern_monthly_pattern_month_day");
			content.putNull("yearly_pattern_monthly_pattern_week_day");
			content.putNull("yearly_pattern_monthly_pattern_week_day_number");
			return;
		}
		content.put("yearly_pattern_year_month", TextUtils.join(",", yearlyPattern.yearMonth));
		MonthlyPatternEntity monthlyp = yearlyPattern.monthlyPatternEntity;
		content.put("yearly_pattern_monthly_pattern_month_day", TextUtils.join(",", monthlyp.dayOfMonthList));
		content.put("yearly_pattern_monthly_pattern_week_day", monthlyp.weekDay);
		content.put("yearly_pattern_monthly_pattern_week_day_number", monthlyp.weekOfMonth);
	}

	private void handleMonthlyPattern(ContentValues contentValues, MonthlyPatternEntity monthlyPattern) {
		if (monthlyPattern == null) {
			contentValues.putNull("monthly_pattern_month_day");
			contentValues.putNull("monthly_pattern_week_day");
			contentValues.putNull("monthly_pattern_week_day_number");
		} else {
			contentValues.put("monthly_pattern_month_day", TextUtils.join(",", monthlyPattern.dayOfMonthList));
			contentValues.put("monthly_pattern_week_day", monthlyPattern.weekDay == null ? 1 : monthlyPattern.weekDay);
			contentValues.put("monthly_pattern_week_day_number", monthlyPattern.weekOfMonth);
		}
	}

	private void handleDailyPattern(ContentValues content, DailyPatternEntity recurrenceDailyPattern) {
		if (recurrenceDailyPattern == null) {
			handleTime(content, null, "daily_pattern_");
			content.putNull("daily_pattern_period");
			content.putNull("daily_pattern_all_day");
			return;
		}
		handleTime(content, recurrenceDailyPattern.timeEntity, "daily_pattern_");
		content.put("daily_pattern_period", recurrenceDailyPattern.dailyPatternPeriod == 0 ? 1 : recurrenceDailyPattern.dailyPatternPeriod);
		content.put("daily_pattern_all_day", recurrenceDailyPattern.isAllDay ? 1 : 0);

	}

	private void handleLocationGroup(ContentValues content, LocationGroupEntity locationGroup) {
		if (locationGroup == null) {
			content.putNull("location_query");
			content.putNull("location_query_type");
			handleChainInfo(content, null);
			handleCategoryInfo(content, null);
			return;
		}
		content.put("location_query", locationGroup.locationQuery);
		content.put("location_query_type", locationGroup.locationQueryType);
		handleChainInfo(content, locationGroup.chainInfoEntity);
		handleCategoryInfo(content, locationGroup.categoryInfoEntity);
	}

	private void handleCategoryInfo(ContentValues content, CategoryInfoEntity category) {
		if (category == null) {
			content.putNull("category_id");
			content.putNull("display_name");
			return;
		}
		content.put("category_id", category.categoryId);
		content.put("display_name", category.displayName);

	}

	private void handleChainInfo(ContentValues content, ChainInfoEntity chain) {
		if (chain == null) {
			content.putNull("chain_name");
			handleFeatureIdProto(content, null, "chain_id_");
			return;
		}
		content.put("chain_name", chain.chainName);
		if (chain.featureIdProtoEntity == null) {
			handleFeatureIdProto(content, null, "chain_id_");
		} else {
			handleFeatureIdProto(content, chain.featureIdProtoEntity, "chain_id_");
		}
	}

	private void handleLocation(ContentValues content, LocationEntity location) {
		if (location == null) {
			content.putNull("lat");
			content.putNull("lng");
			content.putNull("name");
			content.putNull("radius_meters");
			content.putNull("location_type");
			content.putNull("display_address");
			handleAddress(content, null);
			handleFeatureIdProto(content, null, "location_");
			content.putNull("location_alias_id");
			return;
		}
		content.put("lat", location.latitude);
		content.put("lng", location.longitude);
		content.put("name", location.name);
		content.put("radius_meters", location.radiusMeters);
		content.put("location_type", location.locationType);
		content.put("display_address", location.displayAddress);
		handleAddress(content, location.addressEntity);
		handleFeatureIdProto(content, location.featureIdProtoEntity, "location_");
		if (location.locationAliasId == null) {
			content.putNull("location_alias_id");
		} else {
			content.put("location_alias_id", location.locationAliasId);
		}
	}

	private void handleFeatureIdProto(ContentValues content, FeatureIdProtoEntity featureId, String prefix) {
		if (featureId == null) {
			content.putNull(prefix.concat("cell_id"));
			content.putNull(prefix.concat("fprint"));
			return;
		}
		content.put(prefix.concat("cell_id"), featureId.cellId);
		content.put(prefix.concat("fprint"), featureId.fprint);
	}

	private void handleAddress(ContentValues content, AddressEntity address) {
		if (address == null) {
			content.putNull("address_country");
			content.putNull("address_locality");
			content.putNull("address_region");
			content.putNull("address_street_address");
			content.putNull("address_street_number");
			content.putNull("address_street_name");
			content.putNull("address_postal_code");
			content.putNull("address_name");
			return;
		}
		content.put("address_country", address.addressCountry);
		content.put("address_locality", address.addressLocality);
		content.put("address_region", address.addressRegion);
		content.put("address_street_address", address.addressStreetAddress);
		content.put("address_street_number", address.addressStreetNumber);
		content.put("address_street_name", address.addressStreetName);
		content.put("address_postal_code", address.addressPostalCode);
		content.put("address_name", address.addressName);
	}

	private void handleDateTime(ContentValues contentValues, DateTimeEntity dateTime, String prefix) {

		if (dateTime == null) {
			contentValues.putNull(prefix.concat("year"));
			contentValues.putNull(prefix.concat("month"));
			contentValues.putNull(prefix.concat("day"));
			contentValues.putNull(prefix.concat("period"));
			contentValues.putNull(prefix.concat("absolute_time_ms"));
			handleTime(contentValues, null, prefix);
			contentValues.putNull(prefix.concat("date_range"));
			contentValues.putNull(prefix.concat("unspecified_future_time"));
			contentValues.putNull(prefix.concat("all_day"));
			return;
		}
		contentValues.put(prefix.concat("year"), dateTime.year);
		contentValues.put(prefix.concat("month"), dateTime.month);
		contentValues.put(prefix.concat("day"), dateTime.day);

		if (dateTime.period == null) {
			contentValues.put(prefix.concat("period"), (Integer) null);
		} else {
			contentValues.put(prefix.concat("period"), dateTime.period == 0 ? 1 : dateTime.period);
		}

		contentValues.put(prefix.concat("absolute_time_ms"), dateTime.absoluteTimeMs);
		handleTime(contentValues, dateTime.timeEntity, prefix);
		contentValues.put(prefix.concat("date_range"), dateTime.dateRange == null ? null : 1);
		contentValues.put(prefix.concat("unspecified_future_time"), dateTime.unspecifiedFutureTime);
		contentValues.put(prefix.concat("all_day"), dateTime.allDay);


	}

	private void handleTime(ContentValues contentValues, TimeEntity value, String prefix) {
		if (value == null) {
			contentValues.putNull(prefix.concat("hour"));
			contentValues.putNull(prefix.concat("minute"));
			contentValues.putNull(prefix.concat("second"));
			return;
		}
		contentValues.put(prefix.concat("hour"), value.hourOfDay);
		contentValues.put(prefix.concat("minute"), value.minute);
		contentValues.put(prefix.concat("second"), value.second);
	}


	private String checkNullStr(String value) {
		return TextUtils.isEmpty(value) ? null : value;
	}

}
