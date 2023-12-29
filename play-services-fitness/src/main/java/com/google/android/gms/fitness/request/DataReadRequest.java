package com.google.android.gms.fitness.request;

import android.os.Parcelable;

import com.google.android.gms.fitness.data.DataSource;

import org.microg.safeparcel.AutoSafeParcelable;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.internal.IDataReadCallback;
import java.util.List;

public class DataReadRequest extends AutoSafeParcelable {
    public static final Parcelable.Creator<DataReadRequest> CREATOR = new AutoSafeParcelable.AutoCreator<>(DataReadRequest.class);

    public List<DataType> dataTypes;
    public List<DataSource> dataSources;
    public long startTimeMillis;
    public long endTimeMillis;
    public List<DataType> aggregatedDataTypes;
    public List<DataSource> aggregatedDataSources;
    public int bucketType;
    public long bucketDurationMillis;
    public DataSource activityDataSource;
    public int limit;
    public boolean flushBufferBeforeRead;
    public boolean areServerQueriesEnabled;
    public List intervalStartTimesNanos;
    public List intervalEndTimesNanos;
    public IDataReadCallback callback;

    public List<DataType> getDataTypes() {
        return dataTypes;
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public List<DataType> getAggregatedDataTypes() {
        return aggregatedDataTypes;
    }

    public List<DataSource> getAggregatedDataSources() {
        return aggregatedDataSources;
    }

    public int getBucketType() {
        return bucketType;
    }

    public long getBucketDurationMillis() {
        return bucketDurationMillis;
    }

    public DataSource getActivityDataSource() {
        return activityDataSource;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isFlushBufferBeforeRead() {
        return flushBufferBeforeRead;
    }

    public boolean isAreServerQueriesEnabled() {
        return areServerQueriesEnabled;
    }

    public List getIntervalStartTimesNanos() {
        return intervalStartTimesNanos;
    }

    public List getIntervalEndTimesNanos() {
        return intervalEndTimesNanos;
    }

    public IDataReadCallback getCallback() {
        return callback;
    }

    @Override
    public String toString() {
        return "DataReadRequest{" +
                "dataTypes=" + dataTypes +
                ", dataSources=" + dataSources +
                ", startTimeMillis=" + startTimeMillis +
                ", endTimeMillis=" + endTimeMillis +
                ", aggregatedDataTypes=" + aggregatedDataTypes +
                ", aggregatedDataSources=" + aggregatedDataSources +
                ", bucketType=" + bucketType +
                ", bucketDurationMillis=" + bucketDurationMillis +
                ", activityDataSource=" + activityDataSource +
                ", limit=" + limit +
                ", flushBufferBeforeRead=" + flushBufferBeforeRead +
                ", areServerQueriesEnabled=" + areServerQueriesEnabled +
                ", intervalStartTimesNanos=" + intervalStartTimesNanos +
                ", intervalEndTimesNanos=" + intervalEndTimesNanos +
                ", callback=" + callback +
                '}';
    }
}
