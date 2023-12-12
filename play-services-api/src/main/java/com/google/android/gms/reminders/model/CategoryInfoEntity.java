package com.google.android.gms.reminders.model;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class CategoryInfoEntity extends AutoSafeParcelable {
	public static Creator<CategoryInfoEntity> CREATOR = new AutoCreator<>(CategoryInfoEntity.class);
	@Field(2)
	public String categoryId;
	@Field(3)
	public String displayName;
	@Field(value = 4,useDirectList = true)
	public List list;

}
