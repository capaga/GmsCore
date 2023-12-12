package com.google.android.gms.potokens.internal;

import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.potokens.internal.ITokenCallbacks;

interface IPoTokensService{

    void reponseStatus(IStatusCallback call,in int code)=1;

    void reponseStatustoken(ITokenCallbacks call, in int i, in byte[] bArr)=2;
}