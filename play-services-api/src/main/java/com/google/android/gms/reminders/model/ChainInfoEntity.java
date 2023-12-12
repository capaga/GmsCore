package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

public class ChainInfoEntity extends AutoSafeParcelable {
	public static Creator<ChainInfoEntity> CREATOR = new AutoCreator<>(ChainInfoEntity.class);
	@Field(3)
	public String chainName;
	@Field(4)
	public FeatureIdProtoEntity featureIdProtoEntity;
}
