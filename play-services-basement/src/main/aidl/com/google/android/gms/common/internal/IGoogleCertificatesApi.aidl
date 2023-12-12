package com.google.android.gms.common.internal;

import com.google.android.gms.common.internal.GoogleCertificatesQuery;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.common.GoogleCertificatesLookupQuery;
import com.google.android.gms.common.GoogleCertificatesLookupResponse;

interface IGoogleCertificatesApi {
    IObjectWrapper getGoogleCertificates()=0;
    IObjectWrapper getGoogleReleaseCertificates()=1;
    boolean isGoogleReleaseSigned(String packageName, IObjectWrapper certData)=2;
    boolean isGoogleSigned(String packageName, IObjectWrapper certData)=3;
    boolean isGoogleOrPlatformSigned(in GoogleCertificatesQuery query, IObjectWrapper packageManager)=4;
    GoogleCertificatesLookupResponse isPackageGoogleOrPlatformSigned(in GoogleCertificatesLookupQuery googleCertificatesLookupQuery)=5;
    boolean isPackageGoogleOrPlatformSignedAvailable() = 6;
}