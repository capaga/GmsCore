package com.google.android.gms.fitness.request;

import org.microg.safeparcel.AutoSafeParcelable;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.internal.IStatusCallback;
public class DataInsertRequest extends AutoSafeParcelable {
    public static final Creator<DataInsertRequest> CREATOR = new AutoCreator<>(DataInsertRequest.class);
    public DataSet dataSet;
    public IStatusCallback callback;
    public boolean c;

}
