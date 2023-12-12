package com.google.android.gms.common;

import org.microg.safeparcel.AutoSafeParcelable;

public class GoogleCertificatesLookupResponse extends AutoSafeParcelable {

    @Field(1)
    public  boolean flag;
    @Field(2)
    public  String name;
    @Field(3)
    public  int code;


    public static final Creator<GoogleCertificatesLookupResponse> CREATOR = new AutoCreator<GoogleCertificatesLookupResponse>(GoogleCertificatesLookupResponse.class);

}
