package org.microg.gms.backup.G1Backup;

import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class G1RestoreService extends BaseService {
    private static final String TAG = "G1RestoreService";

    public G1RestoreService() {
        super(TAG, GmsService.G1_RESTORE);
    }
    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        Log.d(TAG, "handleServiceRequest: enter");
        callback.onPostInitComplete(CommonStatusCodes.SUCCESS, new G1RestoreServiceImpl().asBinder(), null);
    }
}
