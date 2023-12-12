package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class DailyPatternEntity extends AutoSafeParcelable {
	public static Creator<DailyPatternEntity> CREATOR = new AutoCreator<>(DailyPatternEntity.class);
	@Field(2)
	public TimeEntity timeEntity;
	@Field(3)
	public int dailyPatternPeriod;
	@Field(4)
	public Boolean isAllDay;

	@Override
	public String toString() {
		return "DailyPatternEntity{" +
				"timeEntity=" + timeEntity +
				", dailyPatternPeriod=" + dailyPatternPeriod +
				", isAllDay=" + isAllDay +
				'}';
	}
}
