package org.microg.gms.panorama;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.panorama.internal.IPanoramaCallbacks;
import com.google.android.gms.panorama.internal.IPanoramaService;

public class PanoramaServiceImpl extends IPanoramaService.Stub{

    public PanoramaServiceImpl(Context context, String packageName, Bundle extras) {
    }

    @Override
    public void unknowMethod(IPanoramaCallbacks callback, Uri uri, Bundle bundle, boolean needGrantReadUriPermissions) throws RemoteException {
        Log.w("GmsPanoramaService", "ERROR:PanoramaService not implement! Print by GMS...");
    }
}
