package org.microg.gms.reminders;


import com.google.android.gms.reminders.model.DateTimeEntity;
import com.google.android.gms.reminders.model.RecurrenceEntity;

public class DailyRecurrenceIterator extends RecurrenceIterator {
    public DailyRecurrenceIterator(RecurrenceEntity recurrenceEntity) {
        super(recurrenceEntity);
    }

    @Override
    public DateTimeEntity getStartDateTime(DateTimeEntity a) {
        return a;
    }

    @Override
    public DateTimeEntity getNextReminderDateTime(DateTimeEntity e) {
        return CalendarUtils.calculateDateWithOffset(e, recurrenceEntity.recurrenceEvery == null ? 1 : recurrenceEntity.recurrenceEvery);
    }
}
