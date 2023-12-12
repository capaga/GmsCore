package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class TimeEntity extends AutoSafeParcelable {
	public static Creator<TimeEntity> CREATOR = new AutoCreator<>(TimeEntity.class);
	@Field(2)
	public Integer hourOfDay;
	@Field(3)
	public Integer minute;
	@Field(4)
	public Integer second;

	public TimeEntity(){
		this(null, null, null);
	}

	public TimeEntity(Integer hourOfDay, Integer minute, Integer second) {
		this.hourOfDay = hourOfDay;
		this.minute = minute;
		this.second = second;
	}

	@Override
	public String toString() {
		return "TimeEntity{" +
				"hourOfDay=" + hourOfDay +
				", minute=" + minute +
				", second=" + second +
				'}';
	}
}
