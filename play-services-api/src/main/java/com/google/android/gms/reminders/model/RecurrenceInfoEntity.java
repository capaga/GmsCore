package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class RecurrenceInfoEntity extends AutoSafeParcelable {
	public static Creator<RecurrenceInfoEntity> CREATOR = new AutoCreator<>(RecurrenceInfoEntity.class);
	@Field(2)
	public RecurrenceEntity recurrenceEntity;
	@Field(3)
	public String recurrenceId;
	@Field(4)
	public Boolean isRecurrenceMaster;
	@Field(5)
	public Boolean isRecurrenceExceptional;
}
