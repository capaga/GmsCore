package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class RecurrenceEndEntity extends AutoSafeParcelable {
	public static Creator<RecurrenceEndEntity> CREATOR = new AutoCreator<>(RecurrenceEndEntity.class);
	@Field(2)
	public DateTimeEntity dateTimeEntity;
	@Field(4)
	public Integer num;
	@Field(5)
	public Boolean isAutoRenew;
	@Field(6)
	public DateTimeEntity untilDateTimeEntity;

	public RecurrenceEndEntity(){}
	public RecurrenceEndEntity(DateTimeEntity dateTimeEntity,Integer num, Boolean isAutoRenew, DateTimeEntity untilDateTimeEntity){
		this.num = num;
		this.isAutoRenew = isAutoRenew;
		this.dateTimeEntity = dateTimeEntity;
		this.untilDateTimeEntity = untilDateTimeEntity;
	}
}
