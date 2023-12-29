package org.microg.gms.backup.G1Backup;

import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class BackUpNowService extends BaseService {
    private static final String TAG = "BackUpNowService";
    public BackUpNowService() {
        super(TAG, GmsService.BACKUP_NOW);
    }
    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        callback.onPostInitComplete(CommonStatusCodes.SUCCESS, new BackupNowServiceImpl(this).asBinder(), null);
    }
}
