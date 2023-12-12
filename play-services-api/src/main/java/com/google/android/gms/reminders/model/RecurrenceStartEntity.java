package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class RecurrenceStartEntity extends AutoSafeParcelable {
	public static Creator<RecurrenceStartEntity> CREATOR = new AutoCreator<>(RecurrenceStartEntity.class);
	@Field(2)
	public DateTimeEntity dateTimeEntity;
}
