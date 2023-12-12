package org.microg.gms.reminders;

import com.google.android.gms.reminders.model.DateTimeEntity;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtils {
    public static Calendar getCalendarUTC() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setLenient(false);
        calendar.clear();
        return calendar;
    }

    public static DateTimeEntity extractDateFromCalendar(Calendar calendar) {
        DateTimeEntity tmpDateTime = new DateTimeEntity();
        tmpDateTime.year = calendar.get(Calendar.YEAR);
        tmpDateTime.month = calendar.get(Calendar.MONTH) + 1;
        tmpDateTime.day = calendar.get(Calendar.DAY_OF_MONTH);
        return tmpDateTime;
    }

    public static DateTimeEntity calculateDateWithOffset(DateTimeEntity dateTime, int offsetDays) {
        Calendar calendar = CalendarUtils.dateTimeToCalendar(dateTime);
        calendar.add(Calendar.DAY_OF_MONTH, offsetDays);
        return CalendarUtils.extractDateFromCalendar(calendar);
    }

    public static Calendar dateTimeToCalendar(DateTimeEntity dateTime) {
        Calendar calendar = getCalendarUTC();

        calendar.set(dateTime.year, dateTime.month - 1, dateTime.day);
        if (dateTime.timeEntity != null) {
            calendar.set(Calendar.HOUR_OF_DAY, dateTime.timeEntity.hourOfDay);
            calendar.set(Calendar.MINUTE, dateTime.timeEntity.minute);
            calendar.set(Calendar.SECOND, dateTime.timeEntity.second);
        }
        return calendar;
    }
}
