package com.google.android.gms.fitness.request;

import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.internal.IStatusCallback;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class DataDeleteRequest extends AutoSafeParcelable {
    public static final Creator<DataDeleteRequest> CREATOR = new AutoCreator<>(DataDeleteRequest.class);

    @Field(1)
    public long startTimeMillis;
    @Field(2)
    public long endTimeMillis;
    @Field(3)
    public List<DataSource> dataSources;
    @Field(4)
    public List<DataType> dataTypes;
    @Field(5)
    public List<Session> sessions;
    @Field(6)
    public boolean deleteAllData;
    @Field(7)
    public boolean deleteAllSessions;
    @Field(8)
    public IStatusCallback callback;

    @Field(9)
    public boolean deleteByTimeRange;
    @Field(10)
    public boolean enableLocationCleanup;

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public List<DataType> getDataTypes() {
        return dataTypes;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public boolean isDeleteAllData() {
        return deleteAllData;
    }

    public boolean isDeleteAllSessions() {
        return deleteAllSessions;
    }

    public IStatusCallback getCallback() {
        return callback;
    }

    public boolean isDeleteByTimeRange() {
        return deleteByTimeRange;
    }

    public boolean isEnableLocationCleanup() {
        return enableLocationCleanup;
    }

    @Override
    public String toString() {
        return "DataDeleteRequest{" +
                "startTimeMillis=" + startTimeMillis +
                ", endTimeMillis=" + endTimeMillis +
                ", dataSources=" + dataSources +
                ", dataTypes=" + dataTypes +
                ", sessions=" + sessions +
                ", deleteAllData=" + deleteAllData +
                ", deleteAllSessions=" + deleteAllSessions +
                ", callback=" + callback +
                ", deleteByTimeRange=" + deleteByTimeRange +
                ", enableLocationCleanup=" + enableLocationCleanup +
                '}';
    }
}
