package com.google.android.gms.backup.internal;

import com.google.android.gms.backup.internal.IRestoreDataCallback;
import com.google.android.gms.backup.internal.IRestoreNowCallbacks;

interface IG1RestoreService {
    void h(boolean arg1) = 0;
    void i(boolean arg1) = 1;
    boolean g() = 3;
    void f(String arg1) = 4;
    void a(boolean arg1, boolean arg2) = 5;
    void e(boolean arg1, boolean arg2) = 6;
    void j(IRestoreDataCallback callback, String arg2) = 7;
    void k(boolean arg1, IRestoreNowCallbacks callback) = 8;
}