package com.google.android.gms.fitness.data;

import android.os.Parcelable;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataType extends AutoSafeParcelable {
    public static final Parcelable.Creator<DataType> CREATOR = new AutoSafeParcelable.AutoCreator<>(DataType.class);
    public String Z;
    public List aa;
    public String ab;
    public String ac;
    public static final DataType a = new DataType("com.google.step_count.delta", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.d);
    public static final DataType b = new DataType("com.google.step_count.cumulative", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.d);
    public static final DataType c = new DataType("com.google.step_count.cadence", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.u);
    public static final DataType d = new DataType("com.google.internal.goal", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.v);
    public static final DataType e = new DataType("com.google.activity.segment", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.a);
    public static final DataType f = new DataType("com.google.sleep.segment", "https://www.googleapis.com/auth/fitness.sleep.read", "https://www.googleapis.com/auth/fitness.sleep.write", com.google.android.gms.fitness.data.Field.b);
    public static final DataType g = new DataType("com.google.calories.expended", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.y);
    public static final DataType h = new DataType("com.google.calories.bmr", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.y);
    public static final DataType i = new DataType("com.google.power.sample", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.z);
    public static final DataType j = new DataType("com.google.sensor.events", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.R, com.google.android.gms.fitness.data.Field.S, com.google.android.gms.fitness.data.Field.T);
    public static final DataType k = new DataType("com.google.heart_rate.bpm", "https://www.googleapis.com/auth/fitness.heart_rate.read", "https://www.googleapis.com/auth/fitness.heart_rate.write", com.google.android.gms.fitness.data.Field.j);
    public static final DataType l = new DataType("com.google.respiratory_rate", "https://www.googleapis.com/auth/fitness.respiratory_rate.read", "https://www.googleapis.com/auth/fitness.respiratory_rate.write", com.google.android.gms.fitness.data.Field.k);
    public static final DataType m = new DataType("com.google.location.sample", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.l, com.google.android.gms.fitness.data.Field.m, com.google.android.gms.fitness.data.Field.n, com.google.android.gms.fitness.data.Field.o);
    @Deprecated
    public static final DataType n = new DataType("com.google.location.track", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.l, com.google.android.gms.fitness.data.Field.m, com.google.android.gms.fitness.data.Field.n, com.google.android.gms.fitness.data.Field.o);
    public static final DataType o = new DataType("com.google.distance.delta", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.p);
    public static final DataType p = new DataType("com.google.speed", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.t);
    public static final DataType q = new DataType("com.google.cycling.wheel_revolution.cumulative", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.x);
    public static final DataType r = new DataType("com.google.cycling.wheel_revolution.rpm", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.u);
    public static final DataType s = new DataType("com.google.cycling.pedaling.cumulative", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.x);
    public static final DataType t = new DataType("com.google.cycling.pedaling.cadence", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.u);
    public static final DataType u = new DataType("com.google.height", "https://www.googleapis.com/auth/fitness.body.read", "https://www.googleapis.com/auth/fitness.body.write", com.google.android.gms.fitness.data.Field.q);
    public static final DataType v = new DataType("com.google.weight", "https://www.googleapis.com/auth/fitness.body.read", "https://www.googleapis.com/auth/fitness.body.write", com.google.android.gms.fitness.data.Field.r);
    public static final DataType w = new DataType("com.google.body.fat.percentage", "https://www.googleapis.com/auth/fitness.body.read", "https://www.googleapis.com/auth/fitness.body.write", com.google.android.gms.fitness.data.Field.s);
    public static final DataType x = new DataType("com.google.nutrition", "https://www.googleapis.com/auth/fitness.nutrition.read", "https://www.googleapis.com/auth/fitness.nutrition.write", com.google.android.gms.fitness.data.Field.D, com.google.android.gms.fitness.data.Field.B, com.google.android.gms.fitness.data.Field.C);
    public static final DataType y = new DataType("com.google.hydration", "https://www.googleapis.com/auth/fitness.nutrition.read", "https://www.googleapis.com/auth/fitness.nutrition.write", com.google.android.gms.fitness.data.Field.A);
    public static final DataType z = new DataType("com.google.activity.exercise", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.E, com.google.android.gms.fitness.data.Field.F, com.google.android.gms.fitness.data.Field.g, com.google.android.gms.fitness.data.Field.H, com.google.android.gms.fitness.data.Field.G);
    public static final DataType A = new DataType("com.google.active_minutes", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.f);
    public static final DataType B = A;
    public static final DataType C = new DataType("com.google.device_on_body", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.W);
    public static final DataType D = new DataType("com.google.activity.summary", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.a, com.google.android.gms.fitness.data.Field.f, com.google.android.gms.fitness.data.Field.I);
    public static final DataType E = new DataType("com.google.calories.bmr.summary", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType F = a;
    public static final DataType G = o;
    public static final DataType H = g;
    public static final DataType I = new DataType("com.google.heart_minutes", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.U);
    public static final DataType J = new DataType("com.google.heart_minutes.summary", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.U, com.google.android.gms.fitness.data.Field.f);
    public static final DataType K = new DataType("com.google.heart_rate.summary", "https://www.googleapis.com/auth/fitness.heart_rate.read", "https://www.googleapis.com/auth/fitness.heart_rate.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType L = new DataType("com.google.location.bounding_box", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.M, com.google.android.gms.fitness.data.Field.N, com.google.android.gms.fitness.data.Field.O, com.google.android.gms.fitness.data.Field.P);
    public static final DataType M = new DataType("com.google.power.summary", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType N = new DataType("com.google.speed.summary", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType O = new DataType("com.google.body.fat.percentage.summary", "https://www.googleapis.com/auth/fitness.body.read", "https://www.googleapis.com/auth/fitness.body.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType P = new DataType("com.google.weight.summary", "https://www.googleapis.com/auth/fitness.body.read", "https://www.googleapis.com/auth/fitness.body.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType Q = new DataType("com.google.height.summary", "https://www.googleapis.com/auth/fitness.body.read", "https://www.googleapis.com/auth/fitness.body.write", com.google.android.gms.fitness.data.Field.J, com.google.android.gms.fitness.data.Field.K, com.google.android.gms.fitness.data.Field.L);
    public static final DataType R = new DataType("com.google.nutrition.summary", "https://www.googleapis.com/auth/fitness.nutrition.read", "https://www.googleapis.com/auth/fitness.nutrition.write", com.google.android.gms.fitness.data.Field.D, com.google.android.gms.fitness.data.Field.B);
    public static final DataType S = y;
    public static final DataType T = new DataType("com.google.activity.samples", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.V);
    public static final DataType U = new DataType("com.google.internal.sleep_attributes", "https://www.googleapis.com/auth/fitness.sleep.read", "https://www.googleapis.com/auth/fitness.sleep.write", com.google.android.gms.fitness.data.Field.X);
    public static final DataType V = new DataType("com.google.internal.sleep_schedule", "https://www.googleapis.com/auth/fitness.sleep.read", "https://www.googleapis.com/auth/fitness.sleep.write", com.google.android.gms.fitness.data.Field.Y);
    public static final DataType W = new DataType("com.google.internal.paced_walking_attributes", "https://www.googleapis.com/auth/fitness.activity.read", "https://www.googleapis.com/auth/fitness.activity.write", com.google.android.gms.fitness.data.Field.aa);
    public static final DataType X = new DataType("com.google.time_zone_change", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.ab);
    public static final DataType Y = new DataType("com.google.internal.met", "https://www.googleapis.com/auth/fitness.location.read", "https://www.googleapis.com/auth/fitness.location.write", com.google.android.gms.fitness.data.Field.ac);

    public DataType(String str, String str2, String str3, com.google.android.gms.fitness.data.Field... fieldArr) {
        this.Z = str;
        this.aa = Collections.unmodifiableList(Arrays.asList(fieldArr));
        this.ab = str2;
        this.ac = str3;
    }

}
