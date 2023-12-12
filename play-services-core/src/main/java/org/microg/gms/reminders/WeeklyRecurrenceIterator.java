package org.microg.gms.reminders;


import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;
import com.google.android.gms.reminders.model.WeeklyPatternEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class WeeklyRecurrenceIterator extends RecurrenceIterator {
    private final List<Integer> weekdayList;

    public WeeklyRecurrenceIterator(RecurrenceEntity recurrenceEntity) {
        super(recurrenceEntity);
        this.weekdayList = new ArrayList<>(sortWeekday((recurrenceEntity.weeklyPatternEntity)));
    }

    @Override
    public DateTimeEntity getStartDateTime(DateTimeEntity originalDateTime) {
        SortedSet<Integer> weekday = sortWeekday(recurrenceEntity.weeklyPatternEntity);
        int dayOfWeek = CalendarUtils.dateTimeToCalendar(originalDateTime).get(Calendar.DAY_OF_WEEK);
        while (!weekday.contains(dayOfWeek == 1 ? 7 : dayOfWeek - 1) && isDateTimeBeforeOrEqualToEnd(originalDateTime)) {
            originalDateTime = CalendarUtils.calculateDateWithOffset(originalDateTime, 1);
        }

        return originalDateTime;
    }

    private SortedSet<Integer> sortWeekday(WeeklyPatternEntity weeklyPatternEntity) {
        return new TreeSet<>(weeklyPatternEntity.weekday);
    }

    @Override
    public DateTimeEntity getNextReminderDateTime(DateTimeEntity dateTime) {
        if (this.weekdayList.size() == 1) {
            return CalendarUtils.calculateDateWithOffset(dateTime, getRecurrenceEvery() * 7);
        }

        int dayOfWeek = CalendarUtils.dateTimeToCalendar(dateTime).get(Calendar.DAY_OF_WEEK);
        int weekDay = dayOfWeek == 1 ? 7 : dayOfWeek - 1;
        int indexOf = this.weekdayList.indexOf(weekDay);
        if (indexOf < 0) {
            throw new IllegalStateException();
        }
        return indexOf == this.weekdayList.size() - 1 ?
                CalendarUtils.calculateDateWithOffset(CalendarUtils.calculateDateWithOffset(dateTime, getRecurrenceEvery() * 7), -(weekDay - this.weekdayList.get(0)))
                : CalendarUtils.calculateDateWithOffset(dateTime, this.weekdayList.get(indexOf + 1) - weekDay);

    }
}
