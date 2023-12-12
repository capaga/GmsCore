package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class FeatureIdProtoEntity extends AutoSafeParcelable {
	public static Creator<FeatureIdProtoEntity> CREATOR = new AutoCreator<>(FeatureIdProtoEntity.class);
	@Field(2)
	public Long cellId;
	@Field(3)
	public Long fprint;
}
