package org.microg.gms.backup.G1Backup;


import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.internal.ConnectionInfo;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;
import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;


public class G1BackupService extends BaseService {
    private static final String TAG = "G1BackupService";
    public G1BackupService() {
        super(TAG, GmsService.G1_BACKUP);
    }
    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.features = new Feature[]{
                new Feature("g1_backup", 1L),
                new Feature("g1_mms_backup_now", 1L),
                new Feature("g1_enable_android_backup", 1L)
        };
        callback.onPostInitCompleteWithConnectionInfo(ConnectionResult.SUCCESS,
                new G1BackupServiceImpl(this, request).asBinder(),
                connectionInfo);
    }
}
