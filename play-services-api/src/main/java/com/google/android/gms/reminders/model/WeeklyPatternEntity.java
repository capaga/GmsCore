package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class WeeklyPatternEntity extends AutoSafeParcelable {
	public static Creator<WeeklyPatternEntity> CREATOR = new AutoCreator<>(WeeklyPatternEntity.class);
	@Field(value = 2,useDirectList = true)
	public List<Integer> weekday;
}
