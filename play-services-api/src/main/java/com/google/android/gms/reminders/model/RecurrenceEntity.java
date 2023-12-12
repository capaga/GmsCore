package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class RecurrenceEntity extends AutoSafeParcelable {
	public static Creator<RecurrenceEntity> CREATOR = new AutoCreator<>(RecurrenceEntity.class);
	@Field(2)
	public Integer frequency;
	@Field(3)
	public Integer recurrenceEvery;
	@Field(4)
	public RecurrenceStartEntity recurrenceStartEntity;
	@Field(5)
	public RecurrenceEndEntity recurrenceEndEntity;
	@Field(6)
	public DailyPatternEntity dailyPatternEntity;
	@Field(7)
	public WeeklyPatternEntity weeklyPatternEntity;
	@Field(8)
	public MonthlyPatternEntity monthlyPatternEntity;
	@Field(9)
	public YearlyPatternEntity yearlyPatternEntity;
}
