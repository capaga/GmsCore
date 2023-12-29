package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class Bucket extends AutoSafeParcelable {
    public static final Creator<Bucket> CREATOR = new AutoCreator<>(Bucket.class);
    public static final int TYPE_TIME = 1;
    public static final int TYPE_SESSION = 2;
    public static final int TYPE_ACTIVITY_TYPE = 3;
    public static final int TYPE_ACTIVITY_SEGMENT = 4;
    @NonNull
    @Field(1)
    private long startTimeMillis;
    @Field(2)
    private long endTimeMillis;
    @Field(3)
    @Nullable
    private Session session;
    @Field(4)
    private int activityType;
    @Field(5)
    private List<DataSet> dataSets;
    @Field(6)
    private int bucketType;

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    @Nullable
    public Session getSession() {
        return session;
    }

    public void setSession(@Nullable Session session) {
        this.session = session;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public List<DataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<DataSet> dataSets) {
        this.dataSets = dataSets;
    }

    public int getBucketType() {
        return bucketType;
    }

    public void setBucketType(int bucketType) {
        this.bucketType = bucketType;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "startTimeMillis=" + startTimeMillis +
                ", endTimeMillis=" + endTimeMillis +
                ", session=" + session +
                ", activityType=" + activityType +
                ", dataSets=" + dataSets +
                ", bucketType=" + bucketType +
                '}';
    }
}
