package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;


public class LocationEntity extends AutoSafeParcelable {
	public static Creator<LocationEntity> CREATOR = new AutoCreator<>(LocationEntity.class);
	@Field(2)
	public Double latitude;
	@Field(3)
	public Double longitude;
	@Field(4)
	public String name;
	@Field(5)
	public Integer radiusMeters;
	@Field(6)
	public Integer locationType;
	@Field(7)
	public FeatureIdProtoEntity featureIdProtoEntity;
	@Field(8)
	public String displayAddress;
	@Field(9)
	public String locationAliasId;
	@Field(10)
	public AddressEntity addressEntity;
}
