package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class MonthlyPatternEntity extends AutoSafeParcelable {
	public static Creator<MonthlyPatternEntity> CREATOR = new AutoCreator<>(MonthlyPatternEntity.class);
	@Field(value = 2,useDirectList = true)
	public List<Integer> dayOfMonthList;
	@Field(4)
	public Integer weekDay;
	@Field(5)
	public Integer weekOfMonth;

	@Override
	public String toString() {
		return "MonthlyPatternEntity{" +
				"monthDayList=" + dayOfMonthList +
				", weekDay=" + weekDay +
				", weekDayNumber=" + weekOfMonth +
				'}';
	}
}
