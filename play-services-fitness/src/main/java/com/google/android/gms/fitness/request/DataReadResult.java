package com.google.android.gms.fitness.request;

import android.os.Parcelable;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class DataReadResult extends AutoSafeParcelable {
    public static final Parcelable.Creator<DataReadResult> CREATOR = new AutoSafeParcelable.AutoCreator<>(DataReadResult.class);

    public List<DataSet> rawDataSets;
    public Status status;
    public List<Bucket> rawBuckets;
    public int batchCount;
    public List<DataSet> uniqueDataSources;

}
