package org.microg.gms.reminders;


import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;
import com.google.android.gms.reminders.model.YearlyPatternEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class YearlyRecurrenceIterator extends RecurrenceIterator {
    private final List<Integer> yearMonth;

    public YearlyRecurrenceIterator(RecurrenceEntity recurrenceEntity) {
        super(recurrenceEntity);
        this.yearMonth = new ArrayList<>(sortYearMonth(recurrenceEntity.yearlyPatternEntity));
    }

    @Override
    public DateTimeEntity getStartDateTime(DateTimeEntity originalDateTime) {
        SortedSet<Integer> yearMonthSet = sortYearMonth(recurrenceEntity.yearlyPatternEntity);
        while ((!yearMonthSet.contains(originalDateTime.month)
                || !matchesMonthlyPattern(recurrenceEntity.yearlyPatternEntity.monthlyPatternEntity, originalDateTime))
                && isDateTimeBeforeOrEqualToEnd(originalDateTime)) {

            originalDateTime = CalendarUtils.calculateDateWithOffset(originalDateTime, 1);
        }

        return originalDateTime;
    }

    @Override
    public DateTimeEntity getNextReminderDateTime(DateTimeEntity dateTime) {
        DateTimeEntity dateTime2;
        DateTimeEntity tempDateTime = new DateTimeEntity(dateTime);
        tempDateTime.day = 1;
        int day = dateTime.day;
        boolean flag = false;
        while (true) {
            DateTimeEntity nextReminderDate = getNextReminderDateByTargetDay(recurrenceEntity.yearlyPatternEntity.monthlyPatternEntity, tempDateTime, day);
            if (nextReminderDate != null) {
                return nextReminderDate;
            }

            for (Integer month : this.yearMonth) {
                if (month <= tempDateTime.month) {
                    continue;
                }
                tempDateTime.month = month;

                day = 0;
                flag = true;
                break;
            }
            if (flag) {
                flag = false;
                continue;
            }

            tempDateTime.month = this.yearMonth.get(0);
            Calendar calendar = CalendarUtils.dateTimeToCalendar(tempDateTime);
            calendar.add(Calendar.YEAR, getRecurrenceEvery());
            tempDateTime = CalendarUtils.extractDateFromCalendar(calendar);
            day = 0;
        }
    }

    private SortedSet<Integer> sortYearMonth(YearlyPatternEntity yearlyPattern) {
        return new TreeSet<>(yearlyPattern.yearMonth);
    }
}
