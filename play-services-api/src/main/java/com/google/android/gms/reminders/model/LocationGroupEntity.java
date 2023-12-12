package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class LocationGroupEntity extends AutoSafeParcelable {
	public static Creator<LocationGroupEntity> CREATOR = new AutoCreator<>(LocationGroupEntity.class);
	@Field(2)
	public String locationQuery;
	@Field(3)
	public Integer locationQueryType;
	@Field(5)
	public ChainInfoEntity chainInfoEntity;
	@Field(6)
	public CategoryInfoEntity categoryInfoEntity;
}
