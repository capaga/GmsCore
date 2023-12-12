package com.google.android.gms.common;

import org.microg.safeparcel.AutoSafeParcelable;

public class GoogleCertificatesLookupQuery extends AutoSafeParcelable {

    @Field(1)
    public  String a;
    @Field(2)
    public  boolean b;
    @Field(3)
    public  boolean c;
    @Field(5)
    public  boolean e;


    public static final Creator<GoogleCertificatesLookupQuery> CREATOR = new AutoCreator<GoogleCertificatesLookupQuery>(GoogleCertificatesLookupQuery.class);
}
