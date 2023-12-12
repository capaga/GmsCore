package com.google.android.gms.potokens.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.potokens.PoToken;
import com.google.android.gms.potokens.utils.PoTokenHelper;

import java.util.ArrayList;
import java.util.List;

public  class PoTokensApiChimeraServiceImpl extends IPoTokensService.Stub{

    private Context context;
    private String packageName;

    public PoTokensApiChimeraServiceImpl(Context context,String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    @Override
    public void reponseStatus(IStatusCallback call, int code) throws RemoteException {
        Log.e(PoTokensApiChimeraService.tag,"reponseStatus this is success");
        call.onResult(Status.SUCCESS);
    }

    @Override
    public void reponseStatustoken(ITokenCallbacks call, int i, byte[] bArr) throws RemoteException {
        Log.e(PoTokensApiChimeraService.tag,"reponseStatustoken this is cancel,"+new String(bArr));
        Log.e(PoTokensApiChimeraService.tag,"reponseStatustoken this is cancel,"+i);
        Log.e(PoTokensApiChimeraService.tag,"reponseStatustoken this is packageName,"+packageName);
        PoTokenHelper poTokenHelper=new PoTokenHelper();
        long l = System.currentTimeMillis();
        byte[] bytes = poTokenHelper.calc_po_token(context, packageName,bArr);

        if(bytes!=null){
            Log.e(PoTokensApiChimeraService.tag,"reponseStatustoken this is result,"+bytes.length);
        }
        long l1 = System.currentTimeMillis();
        if((l1-l)>2000){
            throw new NullPointerException("time out");
        }
        call.responseToken(Status.SUCCESS,new PoToken(bytes));
        Log.e(PoTokensApiChimeraService.tag,"reponseStatustoken this is result end");

    }
}