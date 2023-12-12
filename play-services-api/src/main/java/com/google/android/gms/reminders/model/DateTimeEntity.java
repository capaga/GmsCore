package com.google.android.gms.reminders.model;


import org.microg.gms.common.PublicApi;
import org.microg.safeparcel.AutoSafeParcelable;

@PublicApi
public class DateTimeEntity extends AutoSafeParcelable {
	public static Creator<DateTimeEntity> CREATOR = new AutoCreator<>(DateTimeEntity.class);
	@Field(2)
	public Integer year;
	@Field(3)
	public Integer month;
	@Field(4)
	public Integer day;
	@Field(5)
	public TimeEntity timeEntity;
	@Field(6)
	public Integer period;
	@Field(7)
	public Integer dateRange;
	@Field(8)
	public Long absoluteTimeMs;
	@Field(9)
	public Boolean unspecifiedFutureTime;
	@Field(10)
	public Boolean allDay;

	public DateTimeEntity(Integer year, Integer month, Integer day, TimeEntity timeEntity, Integer period
			, Integer dateRange, Long absoluteTimeMs, Boolean unspecifiedFutureTime, Boolean allDay, boolean isTimeEntity) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.period = period;
		this.dateRange = dateRange;
		this.absoluteTimeMs = absoluteTimeMs;
		this.unspecifiedFutureTime = unspecifiedFutureTime;
		this.allDay = allDay;
		if(isTimeEntity) {
			this.timeEntity = timeEntity;
			return;
		}
	}

	public DateTimeEntity(){
		this.year = null;
		this.month = null;
		this.day = null;
		this.period = null;
		this.dateRange = null;
		this.absoluteTimeMs = null;
		this.unspecifiedFutureTime = null;
		this.allDay = null;
		this.timeEntity = null;
	}

	public DateTimeEntity(DateTimeEntity dateTimeEntity) {
		this.year = dateTimeEntity.year;
		this.month = dateTimeEntity.month;
		this.day = dateTimeEntity.day;
		this.period = dateTimeEntity.period;
		this.dateRange = dateTimeEntity.dateRange;
		this.absoluteTimeMs = dateTimeEntity.absoluteTimeMs;
		this.unspecifiedFutureTime = dateTimeEntity.unspecifiedFutureTime;
		this.allDay = dateTimeEntity.allDay;
		this.timeEntity = dateTimeEntity.timeEntity;
	}

	public int compare(DateTimeEntity otherDateTime) {
		if (this.year != null && otherDateTime.year != null) {
			int yearComparison = Integer.compare(this.year, otherDateTime.year);
			if (yearComparison != 0) {
				return yearComparison;
			}
		}

		if (this.month != null && otherDateTime.month != null) {
			int monthComparison = Integer.compare(this.month, otherDateTime.month);
			if (monthComparison != 0) {
				return monthComparison;
			}
		}

		if (this.day != null && otherDateTime.day != null) {
			return Integer.compare(this.day, otherDateTime.day);
		}

		return 0;
	}

	@Override
	public String toString() {
		return "DateTimeEntity{" +
				"year=" + year +
				", month=" + month +
				", day=" + day +
				", timeEntity=" + timeEntity +
				", period=" + period +
				", dateRange=" + dateRange +
				", absoluteTimeMs=" + absoluteTimeMs +
				", unspecifiedFutureTime=" + unspecifiedFutureTime +
				", allDay=" + allDay +
				'}';
	}
}
