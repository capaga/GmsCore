package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class ExternalApplicationLinkEntity extends AutoSafeParcelable {
	public static Creator<ExternalApplicationLinkEntity> CREATOR = new AutoCreator<>(ExternalApplicationLinkEntity.class);
	@Field(2)
	public Integer applicationLink;
	@Field(3)
	public String linkId;
}
