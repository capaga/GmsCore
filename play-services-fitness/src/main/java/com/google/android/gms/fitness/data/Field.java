package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import org.microg.safeparcel.AutoSafeParcelable;

public class Field extends AutoSafeParcelable {
    public static final Parcelable.Creator<com.google.android.gms.fitness.data.Field> CREATOR = new AutoSafeParcelable.AutoCreator<>(com.google.android.gms.fitness.data.Field.class);

    public String ad;
    public int ae;
    public Boolean af;
    public static final com.google.android.gms.fitness.data.Field a = d("activity");
    public static final com.google.android.gms.fitness.data.Field b = d("sleep_segment_type");
    public static final com.google.android.gms.fitness.data.Field c = b("confidence");
    public static final com.google.android.gms.fitness.data.Field d = d("steps");
    @Deprecated
    public static final com.google.android.gms.fitness.data.Field e = b("step_length");
    public static final com.google.android.gms.fitness.data.Field f = d("duration");
    public static final com.google.android.gms.fitness.data.Field g = f("duration");
    public static final com.google.android.gms.fitness.data.Field h = c("activity_duration.ascending");
    public static final com.google.android.gms.fitness.data.Field i = c("activity_duration.descending");
    public static final com.google.android.gms.fitness.data.Field j = b("bpm");
    public static final com.google.android.gms.fitness.data.Field k = b("respiratory_rate");
    public static final com.google.android.gms.fitness.data.Field l = b("latitude");
    public static final com.google.android.gms.fitness.data.Field m = b("longitude");
    public static final com.google.android.gms.fitness.data.Field n = b("accuracy");
    public static final com.google.android.gms.fitness.data.Field o = e("altitude");
    public static final com.google.android.gms.fitness.data.Field p = b("distance");
    public static final com.google.android.gms.fitness.data.Field q = b("height");
    public static final com.google.android.gms.fitness.data.Field r = b("weight");
    public static final com.google.android.gms.fitness.data.Field s = b("percentage");
    public static final com.google.android.gms.fitness.data.Field t = b("speed");
    public static final com.google.android.gms.fitness.data.Field u = b("rpm");
    public static final com.google.android.gms.fitness.data.Field v = a("google.android.fitness.GoalV2");
    public static final com.google.android.gms.fitness.data.Field w = a("google.android.fitness.Device");
    public static final com.google.android.gms.fitness.data.Field x = d("revolutions");
    public static final com.google.android.gms.fitness.data.Field y = b("calories");
    public static final com.google.android.gms.fitness.data.Field z = b("watts");
    public static final com.google.android.gms.fitness.data.Field A = b("volume");
    public static final com.google.android.gms.fitness.data.Field B = f("meal_type");
    public static final com.google.android.gms.fitness.data.Field C = new com.google.android.gms.fitness.data.Field("food_item", 3, true);
    public static final com.google.android.gms.fitness.data.Field D = c("nutrients");
    public static final com.google.android.gms.fitness.data.Field E = g("exercise");
    public static final com.google.android.gms.fitness.data.Field F = f("repetitions");
    public static final com.google.android.gms.fitness.data.Field G = e("resistance");
    public static final com.google.android.gms.fitness.data.Field H = f("resistance_type");
    public static final com.google.android.gms.fitness.data.Field I = d("num_segments");
    public static final com.google.android.gms.fitness.data.Field J = b("average");
    public static final com.google.android.gms.fitness.data.Field K = b("max");
    public static final com.google.android.gms.fitness.data.Field L = b("min");
    public static final com.google.android.gms.fitness.data.Field M = b("low_latitude");
    public static final com.google.android.gms.fitness.data.Field N = b("low_longitude");
    public static final com.google.android.gms.fitness.data.Field O = b("high_latitude");
    public static final com.google.android.gms.fitness.data.Field P = b("high_longitude");
    public static final com.google.android.gms.fitness.data.Field Q = d("occurrences");
    public static final com.google.android.gms.fitness.data.Field R = d("sensor_type");
    public static final com.google.android.gms.fitness.data.Field S = new com.google.android.gms.fitness.data.Field("timestamps", 5);
    public static final com.google.android.gms.fitness.data.Field T = new com.google.android.gms.fitness.data.Field("sensor_values", 6);
    public static final com.google.android.gms.fitness.data.Field U = b("intensity");
    public static final com.google.android.gms.fitness.data.Field V = c("activity_confidence");
    public static final com.google.android.gms.fitness.data.Field W = b("probability");
    public static final com.google.android.gms.fitness.data.Field X = a("google.android.fitness.SleepAttributes");
    public static final com.google.android.gms.fitness.data.Field Y = a("google.android.fitness.SleepSchedule");
    @Deprecated
    public static final com.google.android.gms.fitness.data.Field Z = b("circumference");
    public static final com.google.android.gms.fitness.data.Field aa = a("google.android.fitness.PacedWalkingAttributes");
    public static final com.google.android.gms.fitness.data.Field ab = g("zone_id");
    public static final com.google.android.gms.fitness.data.Field ac = b("met");

    public Field(String ad, int ae, Boolean af) {
        this.ad = ad;
        this.ae = ae;
        this.af = af;
    }

    public Field(String str, int i2) {
        this(str, i2, null);
    }

    public static com.google.android.gms.fitness.data.Field a(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 7);
    }

    public static com.google.android.gms.fitness.data.Field b(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 2);
    }

    public static com.google.android.gms.fitness.data.Field c(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 4);
    }

    public static com.google.android.gms.fitness.data.Field d(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 1);
    }

    public static com.google.android.gms.fitness.data.Field e(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 2, true);
    }

    public static com.google.android.gms.fitness.data.Field f(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 1, true);
    }

    public static com.google.android.gms.fitness.data.Field g(String str) {
        return new com.google.android.gms.fitness.data.Field(str, 3);
    }

}
