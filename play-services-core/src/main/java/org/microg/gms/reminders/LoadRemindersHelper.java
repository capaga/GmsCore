package org.microg.gms.reminders;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.reminders.LoadRemindersOptions;

import org.microg.gms.reminders.provider.RemindersProvider;

import java.util.ArrayList;
import java.util.List;

public class LoadRemindersHelper {

    public static final String TAG = LoadRemindersHelper.class.getSimpleName();
    public static DataHolder createDataHolder(LoadRemindersOptions options, Context context, Long accountId) {
        DataHolder dataHolder;
        if (options.clientAssignedIds == null) {
            String[] selectionArgStr = new String[0];
            StringBuilder selectionStr = new StringBuilder("deleted=0 AND account_id=");
            selectionStr.append(accountId);

            appendTaskListSelection(selectionStr, options);
            if (options.recurrenceIds != null && !options.recurrenceIds.isEmpty()) {
                selectionArgStr = mergeSelectionArgs(selectionArgStr, (options.recurrenceIds.toArray(new String[0])));
                selectionStr = mergeSelection(selectionStr, createRecurrenceIdsSelection(options.recurrenceIds));
            }
            appendArchivedSelection(selectionStr, options);

            appendReminderTypeSelection(selectionStr, options);

            appendRecurrenceMasterSelection(selectionStr, options);

            if (options.isExceptional) {
                selectionStr = selectionStr.append(" AND (recurrence_exceptional IS NULL OR recurrence_exceptional!=1)");
            }

            if (options.recurrenceIdNotNull) {
                selectionStr = selectionStr.append(" AND recurrence_id IS NOT NULL");
            }

            String selection = options.firedTimeMillisBegin != null && options.firedTimeMillisEnd != null
                    ? selectionStr + " AND (fired_time_millis BETWEEN IFNULL(" + options.firedTimeMillisBegin + ", 0) AND IFNULL(" + options.firedTimeMillisEnd + ", 9223372036854775807) )" : selectionStr.toString();
            dataHolder = getDataHolder(context, RemindersProvider.REMINDERS_URI, null,
                    selection, selectionArgStr, options.sortKey == 1 ? "due_date_millis ASC" : "created_time_millis DESC");

        } else {
            String[] selectionArgs = {String.valueOf(accountId)};
            StringBuilder selectionBuilder = new StringBuilder();
            if (!options.clientAssignedIds.isEmpty()) {
                for (int v = 0; v < options.clientAssignedIds.size(); v++) {
                    if (selectionBuilder.length() != 0) {
                        selectionBuilder.append(" OR ");
                    }

                    selectionBuilder.append("client_assigned_id=? ");
                }

                selectionArgs = mergeSelectionArgs(selectionArgs, (options.clientAssignedIds.toArray(new String[0])));
            }

            dataHolder = getDataHolder(context, RemindersProvider.REMINDERS_URI, null, "(account_id=?) AND (" + selectionBuilder + ")", selectionArgs, (options.sortKey == 1 ? "due_date_millis ASC" : "created_time_millis DESC"));
        }
        return dataHolder;
    }

    private static void appendTaskListSelection(StringBuilder selectionStr, LoadRemindersOptions options) {
        if (options.taskList != null) {
            selectionStr.append(" AND task_list IN (");
            selectionStr.append(TextUtils.join(",", options.taskList));
            selectionStr.append(")");
        }
    }

    private static void appendArchivedSelection(StringBuilder selectionStr, LoadRemindersOptions options) {
        if (!options.archived) {
            selectionStr.append(" AND archived=0");
        }
    }

    private static void appendReminderTypeSelection(StringBuilder selectionStr, LoadRemindersOptions options) {
        if (options.reminderType != -1) {
            ArrayList<String> selectionReminderType = selectionListByReminderType(options);
            selectionStr.append(" AND (");
            selectionStr.append(TextUtils.join(" OR ", selectionReminderType));
            selectionStr.append(")");
        }
    }

    private static void appendRecurrenceMasterSelection(StringBuilder selectionStr, LoadRemindersOptions options) {
        if (options.h == 0) {
            selectionStr.append(" AND (recurrence_master IS NULL OR recurrence_master!=1)");
        } else {
            selectionStr.append(" AND (recurrence_master IS NULL OR recurrence_master!=0");
            if (options.h == 3) {
                selectionStr.append(" OR recurrence_exceptional=1");
            }
            selectionStr.append(")");
        }
    }

    private static StringBuilder createRecurrenceIdsSelection(List<String> recurrenceIds) {
        StringBuilder selectionIdBuilder = new StringBuilder();
        for (String ignored : recurrenceIds) {
            if (selectionIdBuilder.length() != 0) {
                selectionIdBuilder.append(" OR ");
            }

            selectionIdBuilder.append("recurrence_id=? ");
        }
        return selectionIdBuilder;
    }


    private static ArrayList<String> selectionListByReminderType(LoadRemindersOptions options) {
        ArrayList<String> selectionReminderType = new ArrayList<>();
        if (checkBit(options.reminderType, 0)) {
            long currentTimeMillis = System.currentTimeMillis();
            boolean isTimeBetweenTimestamps = (options.startTimeTodayMidnight != null && currentTimeMillis > options.startTimeTodayMidnight)
                    && (options.endTimeLaterMidnight != null && currentTimeMillis < options.endTimeLaterMidnight);

            selectionReminderType.add(isTimeBetweenTimestamps ? "(reminder_type=0)" : "0");
        }

        if (checkBit(options.reminderType, 1)) {
            String selection = "(reminder_type=1";
            if (options.startTimeTodayMidnight != null) {
                selection = selection + " AND due_date_millis IS NOT NULL  AND due_date_millis>=" + options.startTimeTodayMidnight;
            }

            if (options.endTimeLaterMidnight != null) {
                selection = selection + " AND due_date_millis IS NOT NULL  AND due_date_millis<" + options.endTimeLaterMidnight;
            }

            if (options.e != null) {
                selection = selection + " AND (due_date_millis IS NULL OR due_date_millis < " + options.e + ")";
            }

            if (options.f != null) {
                selection = selection + " AND (due_date_millis IS NULL OR due_date_millis >= " + options.f + ")";
            }

            selectionReminderType.add(selection.concat(")"));
        }

        if (checkBit(options.reminderType, 2)) {
            selectionReminderType.add("(reminder_type=2)");
        }
        return selectionReminderType;
    }

    private static boolean checkBit(int value, int position) {
        return (value & (1 << position)) != 0;
    }

    private static StringBuilder mergeSelection(StringBuilder selection, StringBuilder otherSelection) {
        if (TextUtils.isEmpty(selection)) {
            return otherSelection;
        }

        if (TextUtils.isEmpty(otherSelection)) {
            return selection;
        } else {
            StringBuilder stringBuilder = new StringBuilder("(");
            stringBuilder.append(otherSelection);
            stringBuilder.append(") AND (");
            stringBuilder.append(selection);
            stringBuilder.append(")");
            return stringBuilder;
        }
    }

    private static String[] mergeSelectionArgs(String[] selectionArg, String[] otherSelectionArg) {
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

    private static DataHolder getDataHolder(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = queryProvider(context, uri, projection, selection, selectionArgs, sortOrder);
        return cursor == null ? null : new DataHolder(cursor, 0, null);
    }

    private static Cursor queryProvider(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        ContentResolver contentResolver = context.getContentResolver();
        String authority = uri.getAuthority();
        if (authority == null) {
            return null;
        }
        ContentProviderClient contentProviderClient = contentResolver.acquireContentProviderClient(authority);
        if (contentProviderClient == null) {
            return null;
        }

        try {
            cursor = contentProviderClient.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (RemoteException remoteException) {
            try {
                throw new RuntimeException(remoteException);
            } catch (Throwable throwable) {
                contentProviderClient.release();
                throw throwable;
            }
        } catch (Throwable throwable) {
            contentProviderClient.release();
            throw throwable;
        } finally {
            contentProviderClient.release();
        }
        return cursor;
    }
}
