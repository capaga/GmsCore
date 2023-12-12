package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class YearlyPatternEntity extends AutoSafeParcelable {
	public static Creator<YearlyPatternEntity> CREATOR = new AutoCreator<>(YearlyPatternEntity.class);
	@Field(2)
	public MonthlyPatternEntity monthlyPatternEntity;
	@Field(value = 3,useDirectList = true)
	public List<Integer> yearMonth;
}
