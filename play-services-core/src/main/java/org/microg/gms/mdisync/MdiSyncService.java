package org.microg.gms.mdisync;

import android.os.RemoteException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class MdiSyncService extends BaseService {
    public MdiSyncService() {
        super("GmsMdiSyncSvc", GmsService.MDI_SYNC);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        callback.onPostInitComplete(ConnectionResult.SUCCESS, new MdiSyncServiceImpl().asBinder(), null);
    }
}
