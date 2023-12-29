package com.google.android.gms.fitness.data;

import com.google.android.gms.fitness.request.DataDeleteRequest;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class DataSet extends AutoSafeParcelable {
    public static final Creator<DataSet> CREATOR = new AutoCreator<>(DataSet.class);
    public int a;
    public DataSource b;
    public List c;
    public List d;

}
