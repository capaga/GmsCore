package org.microg.gms.backup;


import android.content.ContentResolver;
import android.os.Handler;
import android.provider.Settings;

/**
 * 从安卓 Frameworks复制的代码
 * /frameworks/base/packages/LocalTransport/src/com/android/localtransport/LocalTransportParameters.java
 */
public class TransportParameters extends KeyValueSettingObserver {
    private static final String TAG = "GmsTransportParams";
    private static final String SETTING = "backup_gms_transport_parameters";
    private static final String KEY_FAKE_ENCRYPTION_FLAG = "fake_encryption_flag";
    private static final String KEY_NON_INCREMENTAL_ONLY = "non_incremental_only";
    private static final String KEY_IS_DEVICE_TRANSFER = "is_device_transfer";
    private static final String KEY_IS_ENCRYPTED = "is_encrypted";


    private boolean mFakeEncryptionFlag;
    private boolean mIsNonIncrementalOnly;
    private boolean mIsDeviceTransfer;
    private boolean mIsEncrypted;

    public TransportParameters(Handler handler, ContentResolver resolver) {
        super(handler, resolver, Settings.Secure.getUriFor(SETTING));
    }

    boolean isFakeEncryptionFlag() {
        return mFakeEncryptionFlag;
    }

    boolean isNonIncrementalOnly() {
        return mIsNonIncrementalOnly;
    }

    boolean isDeviceTransfer() {
        return mIsDeviceTransfer;
    }

    boolean isEncrypted() {
        return mIsEncrypted;
    }

    public String getSettingValue(ContentResolver resolver) {
        return Settings.Secure.getString(resolver, SETTING);
    }

    public void update(KeyValueListParser parser) {
        mFakeEncryptionFlag = parser.getBoolean(KEY_FAKE_ENCRYPTION_FLAG, false);
        mIsNonIncrementalOnly = parser.getBoolean(KEY_NON_INCREMENTAL_ONLY, false);
        mIsDeviceTransfer = parser.getBoolean(KEY_IS_DEVICE_TRANSFER, false);
        mIsEncrypted = parser.getBoolean(KEY_IS_ENCRYPTED, false);
    }
}


