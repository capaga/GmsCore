package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class AddressEntity extends AutoSafeParcelable {
	public static Creator<AddressEntity> CREATOR = new AutoCreator<>(AddressEntity.class);
	@Field(2)
	public String addressCountry;
	@Field(3)
	public String addressLocality;
	@Field(4)
	public String addressRegion;
	@Field(5)
	public String addressStreetAddress;
	@Field(6)
	public String addressPostalCode;
	@Field(7)
	public String addressStreetNumber;
	@Field(8)
	public String addressStreetName;
	@Field(9)
	public String addressName;
}
