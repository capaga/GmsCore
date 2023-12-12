package org.microg.gms.reminders;


import com.google.android.gms.reminders.model.DailyPatternEntity;
import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.MonthlyPatternEntity;
import com.google.android.gms.reminders.model.RecurrenceEndEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;
import com.google.android.gms.reminders.model.RecurrenceStartEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

abstract class RecurrenceIterator implements Iterator<DateTimeEntity> {
    final RecurrenceEntity recurrenceEntity;
    private Integer recurrenceNum;
    private DateTimeEntity endDateTime;
    private int nextNum;
    private DateTimeEntity startDateTime;

    public RecurrenceIterator(@NotNull RecurrenceEntity recurrenceEntity) {
        this.recurrenceEntity = recurrenceEntity;
        RecurrenceStartEntity recurrenceStartEntity = recurrenceEntity.recurrenceStartEntity;
        RecurrenceEndEntity recurrenceEndEntity = recurrenceEntity.recurrenceEndEntity;
        if (recurrenceEntity.recurrenceEvery != null && recurrenceEntity.recurrenceEvery <= 0) {
            return;
        }

        if (recurrenceStartEntity == null || recurrenceStartEntity.dateTimeEntity == null) {
            return;
        }

        DailyPatternEntity dailyPatternEntity = recurrenceEntity.dailyPatternEntity;
        if (dailyPatternEntity != null && dailyPatternEntity.timeEntity == null) {
            return;
        }

        if (recurrenceStartEntity.dateTimeEntity.unspecifiedFutureTime) {
            return;
        }

        if (recurrenceEndEntity == null) {
            return;
        }

        if (recurrenceEndEntity.dateTimeEntity != null) {
            Boolean unspecifiedFutureTime = recurrenceEndEntity.dateTimeEntity.unspecifiedFutureTime;
            if (unspecifiedFutureTime != null && unspecifiedFutureTime) {
                return;
            }
        }

        if (recurrenceEndEntity.dateTimeEntity == null && recurrenceEndEntity.num == null) {
            return;
        }

        if (recurrenceEndEntity.num != null && recurrenceEndEntity.num <= 0) {
            return;
        }

        this.recurrenceNum = recurrenceEndEntity.num;
        this.endDateTime = recurrenceEndEntity.dateTimeEntity;

        this.startDateTime = this.getStartDateTime(recurrenceStartEntity.dateTimeEntity);

        //throw new IllegalArgumentException("recurrence_start must have start_date_time");
    }

    public abstract DateTimeEntity getStartDateTime(DateTimeEntity originalDateTime);

    @Override
    public boolean hasNext() {
        boolean hasNext;
        if (this.startDateTime == null) {
            return false;
        }

        if (this.recurrenceNum != null) {
            hasNext = true;
        } else {
            hasNext = this.endDateTime != null;
        }

        if (!hasNext) {
            return false;
        }
        return (this.recurrenceNum == null || this.nextNum < this.recurrenceNum) && isDateTimeBeforeOrEqualToEnd(startDateTime);
    }

    @Override
    public DateTimeEntity next() {
        if (!this.hasNext()) {
            throw new IllegalStateException();
        }
        DateTimeEntity startDateTime = this.startDateTime;
        DailyPatternEntity dailyPatternEntity = recurrenceEntity.dailyPatternEntity;
        if (dailyPatternEntity != null) {
            DateTimeEntity tempStartDate = new DateTimeEntity(startDateTime);
            if (Boolean.TRUE.equals(dailyPatternEntity.isAllDay)) {
                tempStartDate.allDay = dailyPatternEntity.isAllDay;
            }

            if (dailyPatternEntity.timeEntity != null) {
                tempStartDate.timeEntity = dailyPatternEntity.timeEntity;
            }

            tempStartDate.period = dailyPatternEntity.dailyPatternPeriod;
            startDateTime = tempStartDate;
        }

        this.startDateTime = this.getNextReminderDateTime(this.startDateTime);
        ++this.nextNum;
        return startDateTime;
    }

    public abstract DateTimeEntity getNextReminderDateTime(DateTimeEntity e);

    final boolean isDateTimeBeforeOrEqualToEnd(DateTimeEntity dateTime) {
        return this.endDateTime == null || dateTime.compare(this.endDateTime) <= 0;
    }

    protected final int getRecurrenceEvery() {
        Integer recurrenceEvery = recurrenceEntity.recurrenceEvery;
        return recurrenceEvery == null ? 1 : recurrenceEvery;
    }

    protected boolean matchesMonthlyPattern(MonthlyPatternEntity monthlyPattern, DateTimeEntity dateTime) {
        if (processMonthlyPattern(monthlyPattern, dateTime).contains(dateTime.day)) {
            return true;
        }

        if (monthlyPattern.weekDay != null) {
            int weekOfMonth = monthlyPattern.weekOfMonth;
            DateTimeEntity dateOfMonth = findDateTimeByWeekOfMonth(dateTime, monthlyPattern.weekDay, weekOfMonth);
            return dateOfMonth != null && dateOfMonth.compare(dateTime) == 0;
        }
        return false;
    }

    private Collection<Integer> processMonthlyPattern(MonthlyPatternEntity monthlyPattern, DateTimeEntity dateTime) {
        int maxDaysInMonth = CalendarUtils.dateTimeToCalendar(dateTime).getActualMaximum(Calendar.DAY_OF_MONTH);
        SortedSet<Integer> sortedSet = new TreeSet<>();
        if (monthlyPattern.dayOfMonthList == null) {
            return sortedSet;
        }

        for (Integer dayOfMonth : monthlyPattern.dayOfMonthList) {
            if (dayOfMonth <= 0) {
                dayOfMonth = dayOfMonth + maxDaysInMonth + 1;
            }

            if (dayOfMonth <= 0 || dayOfMonth > maxDaysInMonth) {
                continue;
            }
            sortedSet.add(dayOfMonth);
        }
        return sortedSet;
    }

    private DateTimeEntity findDateTimeByWeekOfMonth(DateTimeEntity dateTime, int weekDay, int weekOfMonth) {
        DateTimeEntity currentDateTime = new DateTimeEntity(dateTime);
        if (weekOfMonth <= 0) {
            currentDateTime.day = CalendarUtils.dateTimeToCalendar(dateTime).getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            currentDateTime.day = 1;
        }

        DateTimeEntity nextDateTime = getNextDateWithWeekday(currentDateTime, weekDay);
        final int offsetDays;
        if (weekOfMonth <= 0) {
            offsetDays = Objects.equals(currentDateTime.month, nextDateTime.month) ? (weekOfMonth + 1) * 7 : weekOfMonth * 7;
        } else {
            offsetDays = Objects.equals(currentDateTime.month, nextDateTime.month) ? (weekOfMonth - 1) * 7 : weekOfMonth * 7;
        }
        DateTimeEntity offsetDateTime = CalendarUtils.calculateDateWithOffset(nextDateTime, offsetDays);
        return Objects.equals(offsetDateTime.month, dateTime.month) ? offsetDateTime : null;
    }

    /**
     * Based on the given date and the specified day of the week, calculate the nearest date that satisfies the condition
     *
     * @param dateTime
     * @param weekDay  day of the week
     * @return
     */
    private DateTimeEntity getNextDateWithWeekday(DateTimeEntity dateTime, int weekDay) {
        Calendar calendar = CalendarUtils.dateTimeToCalendar(dateTime);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH, weekDay - (dayOfWeek == 1 ? 7 : dayOfWeek - 1));
        return CalendarUtils.extractDateFromCalendar(calendar);
    }


    public DateTimeEntity getNextReminderDateByTargetDay(MonthlyPatternEntity monthlyPattern, DateTimeEntity dateTime, int targetDay) {
        for (Integer dayOfMonth : processMonthlyPattern(monthlyPattern, dateTime)) {
            if (dayOfMonth <= targetDay) {
                continue;
            }

            DateTimeEntity nextReminderDate = new DateTimeEntity(dateTime);
            nextReminderDate.day = dayOfMonth;
            return nextReminderDate;
        }

        if (monthlyPattern.weekDay != null) {
            int weekOfMonth = monthlyPattern.weekOfMonth;
            DateTimeEntity dateOfMonth = findDateTimeByWeekOfMonth(dateTime, monthlyPattern.weekDay, weekOfMonth);
            return dateOfMonth == null || dateOfMonth.day <= targetDay ? null : dateOfMonth;
        }
        return null;
    }
}
