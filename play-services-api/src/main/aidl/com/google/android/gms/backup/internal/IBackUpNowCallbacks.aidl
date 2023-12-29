package com.google.android.gms.backup.internal;

import com.google.android.gms.common.api.Status;

interface IBackUpNowCallbacks {
    void setStatus(in Status status);
}
