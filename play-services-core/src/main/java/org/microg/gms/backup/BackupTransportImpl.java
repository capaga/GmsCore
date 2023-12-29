package org.microg.gms.backup;

import static android.content.Context.MODE_PRIVATE;

import android.accounts.Account;
import android.app.backup.RestoreDescription;
import android.app.backup.RestoreSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStat;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.internal.backup.IBackupTransport;
import com.google.android.gms.backup.BackupRequest;
import com.google.android.gms.backup.BackupResponse;
import com.google.android.gms.backup.KVBackupData;
import com.google.protobuf.ByteString;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class BackupTransportImpl extends IBackupTransport.Stub {
    private static final String TAG = "BackupTransportImpl";

    private static final String TRANSPORT_DIR_NAME
            = ".backup.BackupTransportService";

    public static final String TRANSPORT_COMPONENT_NAME = ".backup.BackupTransportService";

    private static final String TRANSPORT_DESTINATION_STRING
            = "No account is available for backup";

    private static final String TRANSPORT_DATA_MANAGEMENT_LABEL
            = "";

    private static final String INCREMENTAL_DIR = "_delta";
    private static final String FULL_DATA_DIR = "_full";
    private static final String DEVICE_NAME_FOR_D2D_RESTORE_SET = "D2D";
    private static final String DEFAULT_DEVICE_NAME_FOR_RESTORE_SET = "flash";

    // The currently-active restore set always has the same (nonzero!) token
    private static final long CURRENT_SET_TOKEN = 1;

    // Size quotas at reasonable values, similar to the current cloud-storage limits
    protected static final long FULL_BACKUP_SIZE_QUOTA = 25 * 1024 * 1024;
    protected static final long KEY_VALUE_BACKUP_SIZE_QUOTA = 5 * 1024 * 1024;

    private Context mContext;
    private File mDataDir;
    private File mCurrentSetDir;
    protected File mCurrentSetIncrementalDir;
    private File mCurrentSetFullDir;

    protected PackageInfo[] mRestorePackages = null;
    protected int mRestorePackage = -1;  // Index into mRestorePackages
    protected int mRestoreType;
    private File mRestoreSetDir;
    protected File mRestoreSetIncrementalDir;
    private File mRestoreSetFullDir;

    // Additional bookkeeping for full backup
    private String mFullTargetPackage;
    private ParcelFileDescriptor mSocket;
    private FileInputStream mSocketInputStream;
    private BufferedOutputStream mFullBackupOutputStream;
    private byte[] mFullBackupBuffer;
    private long mFullBackupSize;
    private FileInputStream mCurFullRestoreStream;
    private byte[] mFullRestoreBuffer;
    private final TransportParameters mParameters;
    public static final int TRANSPORT_ERROR = -1000;
    public static final int TRANSPORT_NOT_INITIALIZED = -1001;
    public static final int TRANSPORT_PACKAGE_REJECTED = -1002;
    public static final int AGENT_ERROR = -1003;
    public static final int AGENT_UNKNOWN = -1004;
    public static final int TRANSPORT_QUOTA_EXCEEDED = -1005;
    public static final int TRANSPORT_OK = 0;
    public static final int NO_MORE_DATA = -1;
    public static final int TRANSPORT_NON_INCREMENTAL_BACKUP_REQUIRED = -1006;
    public static final int FLAG_USER_INITIATED = 1;
    public static final int FLAG_INCREMENTAL = 1 << 1;
    public static final int FLAG_NON_INCREMENTAL = 1 << 2;
    public static final int FLAG_DATA_NOT_CHANGED = 1 << 3;
    public static final String EXTRA_TRANSPORT_REGISTRATION = "android.app.backup.extra.TRANSPORT_REGISTRATION";
    static final long[] POSSIBLE_SETS = {2, 3, 4, 5, 6, 7, 8, 9};
    public static final int FLAG_FAKE_CLIENT_SIDE_ENCRYPTION_ENABLED = 1 << 31;
    public static final int FLAG_DEVICE_TO_DEVICE_TRANSFER = 2;
    public static final int FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED = 1;
    private BackupAccountsManager mBackupAccountsManager;
    private BackupHttpRequestUtil mBackupHttpRequestUtil;
    private ByteArrayOutputStream mByteArrayOutputStream;
    private Account account;

    public BackupTransportImpl(Context context, TransportParameters parameters) {
        mContext = context;
        mParameters = parameters;
        mBackupAccountsManager = BackupAccountsManager.getInstance(context);
        getHttpRequestUtil();
        makeDataDirs();
    }


    private boolean getHttpRequestUtil() {
        if (this.mBackupHttpRequestUtil == null) {
            this.account = mBackupAccountsManager.getCurrentBackupAccount();
            if (this.account != null) {
                long androidId = mBackupAccountsManager.getAndroidId();
                String token = mBackupAccountsManager.getAuthToken();
                if (androidId != 0 && token != null) {
                    this.mBackupHttpRequestUtil = new BackupHttpRequestUtil(androidId, token);
                    return true;
                }
            }
        } else {
            Account currentBackupAccount = mBackupAccountsManager.getCurrentBackupAccount();
            if (currentBackupAccount != null && this.account != null){
                if (currentBackupAccount.equals(this.account)) {
                    return true;
                } else {
                    long androidId = mBackupAccountsManager.getAndroidId();
                    String token = mBackupAccountsManager.getAuthToken();
                    if (androidId != 0 && token != null) {
                        this.account = currentBackupAccount;
                        this.mBackupHttpRequestUtil.setAndroidIdAndAuthToken(androidId, token);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void makeDataDirs() {
        mDataDir = mContext.getFilesDir();
        mCurrentSetDir = new File(mDataDir, Long.toString(CURRENT_SET_TOKEN));
        mCurrentSetIncrementalDir = new File(mCurrentSetDir, INCREMENTAL_DIR);
        mCurrentSetFullDir = new File(mCurrentSetDir, FULL_DATA_DIR);
        mCurrentSetDir.mkdirs();
        mCurrentSetFullDir.mkdir();
        mCurrentSetIncrementalDir.mkdir();
    }


    public IBinder getBinder() {
        return this.asBinder();
    }

    @Override
    public String name() throws RemoteException {
        return TRANSPORT_COMPONENT_NAME;
    }


    @Override
    public Intent configurationIntent() throws RemoteException {
        Log.d(TAG, "configurationIntent: enter");
        return null;
    }

    @Override
    public String currentDestinationString() throws RemoteException {
        Account account = mBackupAccountsManager.getCurrentBackupAccount();
        if (account != null) {
            return "Backing up to " + account.name;
        }
        return TRANSPORT_DESTINATION_STRING;
    }

    @Override
    public Intent dataManagementIntent() throws RemoteException {
        Log.d(TAG, "dataManagementIntent: enter");
        return null;
    }

    @Override
    public CharSequence dataManagementIntentLabel() throws RemoteException {
        Log.d(TAG, "dataManagementIntentLabel: enter");
        return TRANSPORT_DATA_MANAGEMENT_LABEL;
    }

    @Override
    public String transportDirName() throws RemoteException {
        Log.d(TAG, "transportDirName: enter");
        return TRANSPORT_DIR_NAME;
    }

    @Override
    public long requestBackupTime() throws RemoteException {
        Log.d(TAG, "requestBackupTime: enter");
        return 0;
    }

    @Override
    public int initializeDevice() throws RemoteException {
        Log.d(TAG, "initializeDevice: enter");
        Log.v(TAG, "wiping all data");
        deleteContents(mCurrentSetDir);
        makeDataDirs();
        return TRANSPORT_OK;
    }

    private void deleteContents(File dirname) {
        Log.d(TAG, "deleteContents: enter");
        File[] contents = dirname.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (f.isDirectory()) {
                    // delete the directory's contents then fall through
                    // and delete the directory itself.
                    deleteContents(f);
                }
                f.delete();
            }
        }
    }

    @Override
    public int performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd, int flags) throws RemoteException {
        Log.d(TAG, "performBackup: enter");
        if (!checkNetworkConfig() || !getHttpRequestUtil()) {
            Log.e(TAG, "Network error or network not connected or can not using mobile data to back up");
            return TRANSPORT_ERROR;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return performBackupInternal(packageInfo, inFd, flags);
            }
        } finally {
            closeQuietly(inFd);
        }
        return TRANSPORT_ERROR;
    }

    private class KVOperation {
        final String key;     // Element filename, not the raw key, for efficiency
        final byte[] value;   // null when this is a deletion operation

        KVOperation(String k, byte[] v) {
            key = k;
            value = v;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int performBackupInternal(
            PackageInfo packageInfo, ParcelFileDescriptor data, int flags) throws RemoteException {
        Log.d(TAG, "performBackupInternal: enter packageInfo=>" + packageInfo + " data=>" + data + " flags=>" + flags);
        if ((flags & FLAG_DATA_NOT_CHANGED) != 0) {
            // For unchanged data notifications we do nothing and tell the
            // caller everything was OK
            return TRANSPORT_OK;
        }

        boolean isIncremental = (flags & FLAG_INCREMENTAL) != 0;
        boolean isNonIncremental = (flags & FLAG_NON_INCREMENTAL) != 0;

        if (isIncremental) {
            Log.i(TAG, "Performing incremental backup for " + packageInfo.packageName);
        } else if (isNonIncremental) {
            Log.i(TAG, "Performing non-incremental backup for " + packageInfo.packageName);
        } else {
            Log.i(TAG, "Performing backup for " + packageInfo.packageName);
        }


        try {
            StructStat ss = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ss = Os.fstat(data.getFileDescriptor());
            }
            Log.v(TAG, "performBackup() pkg=" + packageInfo.packageName
                    + " size=" + ss.st_size + " flags=" + flags);
        } catch (ErrnoException e) {
            Log.w(TAG, "Unable to stat input file in performBackup() on "
                    + packageInfo.packageName);
        }


        File packageDir = new File(mCurrentSetIncrementalDir, packageInfo.packageName);
        boolean hasDataForPackage = !packageDir.mkdirs();

        if (isIncremental) {
            if (mParameters.isNonIncrementalOnly() || !hasDataForPackage) {
                if (mParameters.isNonIncrementalOnly()) {
                    Log.w(TAG, "Transport is in non-incremental only mode.");

                } else {
                    Log.w(TAG,
                            "Requested incremental, but transport currently stores no data for the "
                                    + "package, requesting non-incremental retry.");
                }
                return TRANSPORT_NON_INCREMENTAL_BACKUP_REQUIRED;
            }
        }
        if (isNonIncremental && hasDataForPackage) {
            Log.w(TAG, "Requested non-incremental, deleting existing data.");
            clearBackupData(packageInfo);
            packageDir.mkdirs();
        }

        // Each 'record' in the restore set is kept in its own file, named by
        // the record key.  Wind through the data file, extracting individual
        // record operations and building a list of all the updates to apply
        // in this update.
        final ArrayList<KVOperation> changeOps;
        try {
            changeOps = parseBackupStream(data);
        } catch (IOException e) {
            // oops, something went wrong.  abort the operation and return error.
            Log.v(TAG, "Exception reading backup input", e);
            return TRANSPORT_ERROR;
        }

        // Okay, now we've parsed out the delta's individual operations.  We need to measure
        // the effect against what we already have in the datastore to detect quota overrun.
        // So, we first need to tally up the current in-datastore size per key.
        final ArrayMap<String, Integer> datastore = new ArrayMap<>();
        int totalSize = parseKeySizes(packageDir, datastore);

        // ... and now figure out the datastore size that will result from applying the
        // sequence of delta operations

        if (changeOps.size() > 0) {
            Log.v(TAG, "Calculating delta size impact");
        } else {
            Log.v(TAG, "No operations in backup stream, so no size change");
        }

        int updatedSize = totalSize;
        for (KVOperation op : changeOps) {
            // Deduct the size of the key we're about to replace, if any
            final Integer curSize = datastore.get(op.key);
            if (curSize != null) {
                updatedSize -= curSize.intValue();
                if (op.value == null) {
                    Log.v(TAG, "  delete " + op.key + ", updated total " + updatedSize);
                }
            }

            // And add back the size of the value we're about to store, if any
            if (op.value != null) {
                updatedSize += op.value.length;

                Log.v(TAG, ((curSize == null) ? "  new " : "  replace ")
                        + op.key + ", updated total " + updatedSize);
            }
        }

        // If our final size is over quota, report the failure
        if (updatedSize > KEY_VALUE_BACKUP_SIZE_QUOTA) {
            Log.i(TAG, "New datastore size " + updatedSize
                    + " exceeds quota " + KEY_VALUE_BACKUP_SIZE_QUOTA);
            return TRANSPORT_QUOTA_EXCEEDED;
        }

        // No problem with storage size, so go ahead and apply the delta operations
        // (in the order that the app provided them)
        BackupRequest.BackupData.Builder backupDataBuilder = BackupRequest.BackupData.newBuilder();
        backupDataBuilder.setPackageName(packageInfo.packageName);
        for (KVOperation op : changeOps) {
            File element = new File(packageDir, op.key);

            // this is either a deletion or a rewrite-from-zero, so we can just remove
            // the existing file and proceed in either case.
            element.delete();

            // if this wasn't a deletion, put the new data in place
            if (op.value != null) {
                try (FileOutputStream out = new FileOutputStream(element)) {
                    KVBackupData kvBackupData = KVBackupData.newBuilder()
                            .setKey(new String(Base64.decode(op.key.getBytes(), Base64.NO_WRAP)))
                            .setData(ByteString.copyFrom(op.value)).build();
                    backupDataBuilder.addKvBackupData(kvBackupData);
                    out.write(op.value, 0, op.value.length);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to update key file " + element, e);
                    return TRANSPORT_ERROR;
                }
            }
        }
        BackupRequest.BackupData backupData = backupDataBuilder.build();
        if (mBackupHttpRequestUtil != null) {
            BackupResponse response = mBackupHttpRequestUtil.postKVBackupData(backupData);
            if (response != null && response.getCode() == 0) {
                return TRANSPORT_OK;
            }
        } else {
            Log.d(TAG, "performBackupInternal: backupHttpRequestUtil is null");
        }
        return TRANSPORT_ERROR;
    }

    private int parseKeySizes(File packageDir, ArrayMap<String, Integer> datastore) {
        int totalSize = 0;
        final String[] elements = packageDir.list();
        if (elements != null) {

            Log.v(TAG, "Existing datastore contents:");

            for (String file : elements) {
                File element = new File(packageDir, file);
                String key = file;  // filename
                int size = (int) element.length();
                totalSize += size;

                Log.v(TAG, "  key " + key + "   size " + size);

                datastore.put(key, size);
            }

            Log.v(TAG, "  TOTAL: " + totalSize);

        } else {

            Log.v(TAG, "No existing data for this package");

        }
        return totalSize;
    }

    private ArrayList<KVOperation> parseBackupStream(ParcelFileDescriptor data)
            throws IOException {
        ArrayList<KVOperation> changeOps = new ArrayList<>();
        BackupDataInputWrapper changeSet = new BackupDataInputWrapper(data.getFileDescriptor());
        while (changeSet.readNextHeader()) {
            String key = changeSet.getKey();
            String base64Key = new String(Base64.encode(key.getBytes(), Base64.NO_WRAP));
            int dataSize = changeSet.getDataSize();
            Log.v(TAG, "  Delta operation key " + key + "   size " + dataSize
                    + "   key64 " + base64Key);
            byte[] buf = (dataSize >= 0) ? new byte[dataSize] : null;
            if (dataSize >= 0) {
                changeSet.readEntityData(buf, 0, dataSize);
            }
            changeOps.add(new KVOperation(base64Key, buf));
        }
        return changeOps;
    }


    @Override
    public int clearBackupData(PackageInfo packageInfo) throws RemoteException {
        Log.d(TAG, "clearBackupData: enter pkg=" + packageInfo.packageName);
        File packageDir = new File(mCurrentSetIncrementalDir, packageInfo.packageName);
        final File[] fileset = packageDir.listFiles();
        if (fileset != null) {
            for (File f : fileset) {
                f.delete();
            }
            packageDir.delete();
        }

        packageDir = new File(mCurrentSetFullDir, packageInfo.packageName);
        final File[] tarballs = packageDir.listFiles();
        if (tarballs != null) {
            for (File f : tarballs) {
                f.delete();
            }
            packageDir.delete();
        }
        return TRANSPORT_OK;
    }

    @Override
    public int finishBackup() throws RemoteException {
        Log.d(TAG, "finishBackup: pkg=" + mFullTargetPackage);
        return tearDownFullBackup();
    }

    private int tearDownFullBackup() {
        if (mSocket != null) {
            try {
                if (mFullBackupOutputStream != null && mByteArrayOutputStream != null) {
                    mFullBackupOutputStream.flush();
                    mFullBackupOutputStream.close();
                    byte[] sendBytes = mByteArrayOutputStream.toByteArray();
                    Log.d(TAG, "tearDownFullBackup: sendBytes=>" + BackupHttpRequestUtil.bytesToHex(sendBytes));
                    mByteArrayOutputStream.flush();
                    mByteArrayOutputStream.close();
                    new SendFullBackupDataThread(mFullTargetPackage, sendBytes).run();
                }
                mSocketInputStream = null;
                mFullTargetPackage = null;
                mByteArrayOutputStream = null;
                mSocket.close();
            } catch (IOException e) {
                Log.w(TAG, "Exception caught in tearDownFullBackup()", e);
                return TRANSPORT_ERROR;
            } finally {
                mSocket = null;
                mFullBackupOutputStream = null;
                mByteArrayOutputStream = null;
            }
        } else {
            Log.d(TAG, "tearDownFullBackup: FullBackupOutputStream is null");
        }
        return TRANSPORT_OK;
    }

    private class SendFullBackupDataThread extends Thread {
        private final byte[] sendBytes;
        private final String packageName;

        public SendFullBackupDataThread(String packageName, byte[] sendBytes) {
            this.sendBytes = sendBytes;
            this.packageName = packageName;
        }

        @Override
        public void run() {
            if (getHttpRequestUtil()) {
                mBackupHttpRequestUtil.putFullBackupData(packageName, sendBytes);
            }
        }
    }

    @Override
    public RestoreSet[] getAvailableRestoreSets() throws RemoteException {
        Log.d(TAG, "getAvailableRestoreSets: enter");
        long[] existing = new long[POSSIBLE_SETS.length + 1];
        int num = 0;

        // see which possible non-current sets exist...
        for (long token : POSSIBLE_SETS) {
            if ((new File(mDataDir, Long.toString(token))).exists()) {
                existing[num++] = token;
            }
        }
        // ...and always the currently-active set last
        existing[num++] = CURRENT_SET_TOKEN;

        RestoreSet[] available = new RestoreSet[num];
        String deviceName = mParameters.isDeviceTransfer() ? DEVICE_NAME_FOR_D2D_RESTORE_SET
                : DEFAULT_DEVICE_NAME_FOR_RESTORE_SET;
        for (int i = 0; i < available.length; i++) {
            available[i] = new RestoreSet("Local disk image", deviceName, existing[i]);
        }
        return available;
    }

    @Override
    public long getCurrentRestoreSet() throws RemoteException {
        Log.d(TAG, "getCurrentRestoreSet: enter");
        return CURRENT_SET_TOKEN;
    }

    @Override
    public int startRestore(long token, PackageInfo[] packages) throws RemoteException {
        Log.d(TAG, "startRestore: token=" + token + " packages=" + packages);
        mRestorePackages = packages;
        mRestorePackage = -1;
        mRestoreSetDir = new File(mDataDir, Long.toString(token));
        mRestoreSetIncrementalDir = new File(mRestoreSetDir, INCREMENTAL_DIR);
        mRestoreSetFullDir = new File(mRestoreSetDir, FULL_DATA_DIR);
        return TRANSPORT_OK;
    }

    @Override
    public RestoreDescription nextRestorePackage() throws RemoteException {
        Log.d(TAG, "nextRestorePackage() : mRestorePackage=" + mRestorePackage
                + " length=" + mRestorePackages.length);
        if (mRestorePackages == null) throw new IllegalStateException("startRestore not called");

        boolean found;
        while (++mRestorePackage < mRestorePackages.length) {
            String name = mRestorePackages[mRestorePackage].packageName;

            // If we have key/value data for this package, deliver that
            // skip packages where we have a data dir but no actual contents
            found = hasRestoreDataForPackage(name);
            if (found) {
                mRestoreType = RestoreDescription.TYPE_KEY_VALUE;
            }

            if (!found) {
                // No key/value data; check for [non-empty] full data
                File maybeFullData = new File(mRestoreSetFullDir, name);
                if (maybeFullData.length() > 0) {

                    Log.v(TAG, "  nextRestorePackage(TYPE_FULL_STREAM) @ "
                            + mRestorePackage + " = " + name);

                    mRestoreType = RestoreDescription.TYPE_FULL_STREAM;
                    mCurFullRestoreStream = null;   // ensure starting from the ground state
                    found = true;
                }
            }

            if (found) {
                return new RestoreDescription(name, mRestoreType);
            }


            Log.v(TAG, "  ... package @ " + mRestorePackage + " = " + name
                    + " has no data; skipping");

        }

        Log.v(TAG, "  no more packages to restore");
        return RestoreDescription.NO_MORE_PACKAGES;
    }

    protected boolean hasRestoreDataForPackage(String packageName) {
        String[] contents = (new File(mRestoreSetIncrementalDir, packageName)).list();
        if (contents != null && contents.length > 0) {

            Log.v(TAG, "  nextRestorePackage(TYPE_KEY_VALUE) @ "
                    + mRestorePackage + " = " + packageName);

            return true;
        }
        return false;
    }

    @Override
    public int getRestoreData(ParcelFileDescriptor outFd) throws RemoteException {
        Log.d(TAG, "getRestoreData: enter outFd=>" + outFd);
        if (mRestorePackages == null) throw new IllegalStateException("startRestore not called");
        if (mRestorePackage < 0) throw new IllegalStateException("nextRestorePackage not called");
        if (mRestoreType != RestoreDescription.TYPE_KEY_VALUE) {
            throw new IllegalStateException("getRestoreData(fd) for non-key/value dataset");
        }
        File packageDir = new File(mRestoreSetIncrementalDir,
                mRestorePackages[mRestorePackage].packageName);

        // The restore set is the concatenation of the individual record blobs,
        // each of which is a file in the package's directory.  We return the
        // data in lexical order sorted by key, so that apps which use synthetic
        // keys like BLOB_1, BLOB_2, etc will see the date in the most obvious
        // order.
        ArrayList<DecodedFilename> blobs = contentsByKey(packageDir);
        if (blobs == null) {  // nextRestorePackage() ensures the dir exists, so this is an error
            Log.e(TAG, "No keys for package: " + packageDir);
            return TRANSPORT_ERROR;
        }

        // We expect at least some data if the directory exists in the first place
        Log.v(TAG, "  getRestoreData() found " + blobs.size() + " key files");
        BackupDataOutputWrapper out = new BackupDataOutputWrapper(outFd.getFileDescriptor());
        try {
            for (DecodedFilename keyEntry : blobs) {
                File f = keyEntry.file;
                FileInputStream in = new FileInputStream(f);
                try {
                    int size = (int) f.length();
                    byte[] buf = new byte[size];
                    in.read(buf);
                    Log.v(TAG, "    ... key=" + keyEntry.key + " size=" + size);
                    out.writeEntityHeader(keyEntry.key, size);
                    out.writeEntityData(buf, size);
                } finally {
                    in.close();
                }
            }
            return TRANSPORT_OK;
        } catch (IOException e) {
            Log.e(TAG, "Unable to read backup records", e);
            return TRANSPORT_ERROR;
        }
    }

    static class DecodedFilename implements Comparable<DecodedFilename> {
        public File file;
        public String key;

        public DecodedFilename(File f) {
            file = f;
            key = new String(Base64.decode(f.getName(), Base64.DEFAULT));
        }

        @Override
        public int compareTo(DecodedFilename other) {
            // sorts into ascending lexical order by decoded key
            return key.compareTo(other.key);
        }
    }

    private ArrayList<DecodedFilename> contentsByKey(File dir) {
        File[] allFiles = dir.listFiles();
        if (allFiles == null || allFiles.length == 0) {
            return null;
        }

        // Decode the filenames into keys then sort lexically by key
        ArrayList<DecodedFilename> contents = new ArrayList<DecodedFilename>();
        for (File f : allFiles) {
            contents.add(new DecodedFilename(f));
        }
        Collections.sort(contents);
        return contents;
    }

    @Override
    public void finishRestore() throws RemoteException {
        Log.d(TAG, "finishRestore: enter");
        if (mRestoreType == RestoreDescription.TYPE_FULL_STREAM) {
            resetFullRestoreState();
        }
        mRestoreType = 0;
    }

    private void resetFullRestoreState() {
        closeQuietly(mCurFullRestoreStream);
        mCurFullRestoreStream = null;
        mFullRestoreBuffer = null;
    }

    @Override
    public long requestFullBackupTime() throws RemoteException {
        Log.d(TAG, "requestFullBackupTime: enter");
        return 0;
    }

    @Override
    public int performFullBackup(PackageInfo targetPackage, ParcelFileDescriptor socket, int flags) throws RemoteException {
        Log.d(TAG, "performFullBackup: enter targetPackage=>" + targetPackage + " socket=>" + socket + " flags=>" + flags);
        if (!checkNetworkConfig()) {
            Log.e(TAG, "network error or network not connected or can not using mobile data to back up");
            return TRANSPORT_ERROR;
        }
        if (mSocket != null) {
            Log.e(TAG, "Attempt to initiate full backup while one is in progress");
            return TRANSPORT_ERROR;
        }
        Log.i(TAG, "performFullBackup : " + targetPackage);
        // We know a priori that we run in the system process, so we need to make
        // sure to dup() our own copy of the socket fd.  Transports which run in
        // their own processes must not do this.
        try {
            mFullBackupSize = 0;
            mSocket = ParcelFileDescriptor.dup(socket.getFileDescriptor());
            mSocketInputStream = new FileInputStream(mSocket.getFileDescriptor());
        } catch (IOException e) {
            Log.e(TAG, "Unable to process socket for full backup");
            return TRANSPORT_ERROR;
        }

        mFullTargetPackage = targetPackage.packageName;
        mFullBackupBuffer = new byte[4096];

        return TRANSPORT_OK;
    }

    @Override
    public int checkFullBackupSize(long size) throws RemoteException {
        Log.d(TAG, "checkFullBackupSize: enter size=>" + size);
        int result = TRANSPORT_OK;
        // Decline zero-size "backups"
        if (size <= 0) {
            result = TRANSPORT_PACKAGE_REJECTED;
        } else if (size > FULL_BACKUP_SIZE_QUOTA) {
            result = TRANSPORT_QUOTA_EXCEEDED;
        }
        if (result != TRANSPORT_OK) {
            Log.v(TAG, "Declining backup of size " + size);
        }
        return result;
    }

    @Override
    public int sendBackupData(int numBytes){
        Log.d(TAG, "sendBackupData: enter int=>" + numBytes);
        if (mSocket == null) {
            Log.w(TAG, "Attempted sendBackupData before performFullBackup");
            return TRANSPORT_ERROR;
        }

        mFullBackupSize += numBytes;
        if (mFullBackupSize > FULL_BACKUP_SIZE_QUOTA) {
            return TRANSPORT_QUOTA_EXCEEDED;
        }

        if (numBytes > mFullBackupBuffer.length) {
            mFullBackupBuffer = new byte[numBytes];
        }

        if (mFullBackupOutputStream == null && mByteArrayOutputStream == null) {
            FileOutputStream tarstream;
            try {
                File tarball = tarballFile(mFullTargetPackage);
                tarstream = new FileOutputStream(tarball);
            } catch (FileNotFoundException e) {
                return TRANSPORT_ERROR;
            }
            mFullBackupOutputStream = new BufferedOutputStream(tarstream);
            mByteArrayOutputStream = new ByteArrayOutputStream();
            Log.d(TAG, "sendBackupData: new a Instances");
        }

        int bytesLeft = numBytes;
        while (bytesLeft > 0) {
            try {
                int nRead = mSocketInputStream.read(mFullBackupBuffer, 0, bytesLeft);
                if (nRead < 0) {
                    // Something went wrong if we expect data but saw EOD
                    Log.w(TAG, "Unexpected EOD; failing backup");
                    return TRANSPORT_ERROR;
                }
                mFullBackupOutputStream.write(mFullBackupBuffer, 0, nRead);
                mByteArrayOutputStream.write(mFullBackupBuffer);
                bytesLeft -= nRead;
            } catch (IOException e) {
                Log.e(TAG, "Error handling backup data for " + mFullTargetPackage);
                return TRANSPORT_ERROR;
            }
        }

        Log.v(TAG, "   stored " + numBytes + " of data");

        return TRANSPORT_OK;
    }

    private File tarballFile(String pkgName) {
        Log.d(TAG, "tarballFile: enter pkgName=>" + pkgName);
        return new File(mCurrentSetFullDir, pkgName);
    }

    @Override
    public void cancelFullBackup() throws RemoteException {
        Log.d(TAG, "Canceling full backup of " + mFullTargetPackage);
        File archive = tarballFile(mFullTargetPackage);
        tearDownFullBackup();
        if (archive.exists()) {
            archive.delete();
        }
    }

    @Override
    public boolean isAppEligibleForBackup(PackageInfo targetPackage, boolean isFullBackup) throws RemoteException {
        Log.d(TAG, "isAppEligibleForBackup: packageName=" + targetPackage + " isFullBackup=" + isFullBackup);
        return false;
    }

    @Override
    public long getBackupQuota(String packageName, boolean isFullBackup) throws RemoteException {
        Log.d(TAG, "getBackupQuota: enter packageName=>" + packageName + " isFullBackup=>" + isFullBackup);
        return isFullBackup ? FULL_BACKUP_SIZE_QUOTA : KEY_VALUE_BACKUP_SIZE_QUOTA;
    }

    @Override
    public int getNextFullRestoreDataChunk(ParcelFileDescriptor socket) throws RemoteException {
        Log.d(TAG, "getNextFullRestoreDataChunk: enter: " + socket);
        if (mRestoreType != RestoreDescription.TYPE_FULL_STREAM) {
            throw new IllegalStateException("Asked for full restore data for non-stream package");
        }

        // first chunk?
        if (mCurFullRestoreStream == null) {
            final String name = mRestorePackages[mRestorePackage].packageName;
            Log.i(TAG, "Starting full restore of " + name);
            File dataset = new File(mRestoreSetFullDir, name);
            try {
                mCurFullRestoreStream = new FileInputStream(dataset);
            } catch (IOException e) {
                // If we can't open the target package's tarball, we return the single-package
                // error code and let the caller go on to the next package.
                Log.e(TAG, "Unable to read archive for " + name);
                return TRANSPORT_PACKAGE_REJECTED;
            }
            mFullRestoreBuffer = new byte[2 * 1024];
        }

        FileOutputStream stream = new FileOutputStream(socket.getFileDescriptor());

        int nRead;
        try {
            nRead = mCurFullRestoreStream.read(mFullRestoreBuffer);
            if (nRead < 0) {
                // EOF: tell the caller we're done
                nRead = NO_MORE_DATA;
            } else if (nRead == 0) {
                // This shouldn't happen when reading a FileInputStream; we should always
                // get either a positive nonzero byte count or -1.  Log the situation and
                // treat it as EOF.
                Log.w(TAG, "read() of archive file returned 0; treating as EOF");
                nRead = NO_MORE_DATA;
            } else {

                Log.i(TAG, "   delivering restore chunk: " + nRead);

                stream.write(mFullRestoreBuffer, 0, nRead);
            }
        } catch (IOException e) {
            return TRANSPORT_ERROR;  // Hard error accessing the file; shouldn't happen
        } finally {
            closeQuietly(socket);
        }

        return nRead;
    }

    private void closeQuietly(@Nullable AutoCloseable closeable) {
        Log.d(TAG, "closeQuietly: enter: AutoCloseable=>" + closeable);
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public int abortFullRestore() throws RemoteException {
        Log.d(TAG, "abortFullRestore: enter");
        if (mRestoreType != RestoreDescription.TYPE_FULL_STREAM) {
            throw new IllegalStateException("abortFullRestore() but not currently restoring");
        }
        resetFullRestoreState();
        mRestoreType = 0;
        return TRANSPORT_OK;
    }

    @Override
    public int getTransportFlags() throws RemoteException {
        Log.d(TAG, "getTransportFlags: enter");
        int flags = 0;
        // Testing for a fake flag and having it set as a boolean in settings prevents anyone from
        // using this it to pull data from the agent
        if (mParameters.isFakeEncryptionFlag()) {
            flags |= FLAG_FAKE_CLIENT_SIDE_ENCRYPTION_ENABLED;
        }
        if (mParameters.isDeviceTransfer()) {
            flags |= FLAG_DEVICE_TO_DEVICE_TRANSFER;
        }
        if (mParameters.isEncrypted()) {
            flags |= FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED;
        }
        return flags;
    }

    public TransportParameters getParameters() {
        Log.d(TAG, "getParameters: enter");
        return mParameters;
    }

    private boolean canUsingMobileData() {
        SharedPreferences backupSettings = this.mContext.getSharedPreferences("backup_settings", MODE_PRIVATE);
        boolean canUsingMobileData = backupSettings.getBoolean("use_mobile_data", false);
        Log.d(TAG, "canUseMobileData: " + canUsingMobileData);
        return canUsingMobileData;
    }

    private int getNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            String typeName = connectivityManager.getActiveNetworkInfo().getTypeName();
            if (typeName == null) {
                Log.d(TAG, "getNetworkInfo: network error");
                return 0;
            }
            Log.d(TAG, "getNetworkInfo: network type name=>" + typeName);
            if (typeName.equalsIgnoreCase("WIFI")) {
                return 1;
            } else if (typeName.equalsIgnoreCase("MOBILE")) {
                return 2;
            }
        } catch (Exception e) {
            Log.e(TAG, "getNetworkInfo: error", e);
        }
        return 0;
    }

    private boolean checkNetworkConfig() {
        int type = getNetworkInfo();
        switch (type) {
            case 1:
                return true;
            case 2:
                if (canUsingMobileData()) {
                    return true;
                }
            default:
                return false;
        }
    }
}



