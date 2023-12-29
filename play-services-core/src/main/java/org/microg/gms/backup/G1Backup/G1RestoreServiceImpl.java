package org.microg.gms.backup.G1Backup;

import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.backup.internal.IG1RestoreService;
import com.google.android.gms.backup.internal.IRestoreDataCallback;
import com.google.android.gms.backup.internal.IRestoreNowCallbacks;

public class G1RestoreServiceImpl extends IG1RestoreService.Stub {
    private static final String TAG = "G1RestoreServiceImpl";
    @Override
    public void a(boolean arg1, boolean arg2) throws RemoteException {
        Log.w(TAG, "Method 'a' not yet implement.");
    }

    @Override
    public void e(boolean arg1, boolean arg2) throws RemoteException {
        Log.w(TAG, "Method 'e' not yet implement");
    }

    @Override
    public void f(String arg1) throws RemoteException {
        Log.w(TAG, "Method 'f' not yet implement.");
    }

    @Override
    public boolean g() throws RemoteException {
        Log.w(TAG, "Method 'g' not yet implement.");
        return false;
    }

    @Override
    public void h(boolean arg1) throws RemoteException {
        Log.w(TAG, "Method 'h' not yet implement.");
    }

    @Override
    public void i(boolean arg1) throws RemoteException {
        Log.w(TAG, "Method 'i' not yet implement.");

    }

    @Override
    public void j(IRestoreDataCallback callback, String arg2) throws RemoteException {
        Log.w(TAG, "Method 'j' not yet implement.");
    }

    @Override
    public void k(boolean arg1, IRestoreNowCallbacks callback) throws RemoteException {
        Log.w(TAG, "Method 'k' not yet implement");
    }
}
