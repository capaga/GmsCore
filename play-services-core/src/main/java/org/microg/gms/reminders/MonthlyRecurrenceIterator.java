package org.microg.gms.reminders;


import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;

import java.util.Calendar;

public class MonthlyRecurrenceIterator extends RecurrenceIterator {
    private static final String TAG = MonthlyRecurrenceIterator.class.getSimpleName();

    public MonthlyRecurrenceIterator(RecurrenceEntity recurrenceEntity) {
        super(recurrenceEntity);
    }

    @Override
    public DateTimeEntity getStartDateTime(DateTimeEntity originalDateTime) {
        while (!matchesMonthlyPattern(recurrenceEntity.monthlyPatternEntity, originalDateTime) && isDateTimeBeforeOrEqualToEnd(originalDateTime)) {
            originalDateTime = CalendarUtils.calculateDateWithOffset(originalDateTime, 1);
        }

        return originalDateTime;
    }

    @Override
    public DateTimeEntity getNextReminderDateTime(DateTimeEntity dateTime) {
        //Find the next suitable reminder time.
        DateTimeEntity tempDateTime = new DateTimeEntity(dateTime);
        tempDateTime.day = 1;
        for (int day = dateTime.day; true; day = 0) {
            DateTimeEntity nextReminderDate = getNextReminderDateByTargetDay(recurrenceEntity.monthlyPatternEntity, tempDateTime, day);
            if (nextReminderDate != null) {
                return nextReminderDate;
            }

            int amount = this.getRecurrenceEvery();
            Calendar calendar = CalendarUtils.dateTimeToCalendar(tempDateTime);
            calendar.add(Calendar.MONTH, amount);
            tempDateTime = CalendarUtils.extractDateFromCalendar(calendar);
        }
    }
}
