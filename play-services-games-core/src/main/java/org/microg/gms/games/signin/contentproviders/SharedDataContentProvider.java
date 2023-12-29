package org.microg.gms.games.signin.contentproviders;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.microg.gms.auth.AuthConstants;
import org.microg.gms.auth.AuthResponse;
import org.microg.gms.auth.AuthServiceManager;
import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.Constants;
import org.microg.gms.games.signin.utils.PeopleUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SharedDataContentProvider extends ContentProvider {

    private static final String TAG = SharedDataContentProvider.class.getSimpleName();

    private final Executor executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean onCreate() {

        List<String> services = new ArrayList<>(5);
        services.add("https://www.googleapis.com/auth/peopleapi.legacy.readwrite");
        services.add("https://www.googleapis.com/auth/peopleapi.readonly");
        services.add("https://www.googleapis.com/auth/peopleapi.readwrite");
        services.add("https://www.googleapis.com/auth/plus.peopleapi.readwrite");
        services.add("https://www.googleapis.com/auth/plus.me");

        File filesDir = getContext().getFilesDir();
        File publicFile = new File(filesDir, "/mdisync/shared/datadownload/public");

        if (!publicFile.exists()) {
            publicFile.mkdirs();
        }

        String service = "oauth2:";
        StringBuilder serviceSb = new StringBuilder("oauth2:");
        for (String s : services) {
            serviceSb.append(s);
            serviceSb.append(" ");
        }
        Log.d(TAG, "onCreate service:" + service);
        Account[] accountsByType = AccountManagerUtils.getInstance(getContext()).getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accountsByType.length > 0) {

            for (int i = 0; i < accountsByType.length; i++) {
                File save = new File(filesDir, "/managed/mdisync/" + i + "/profilesync/public");
                if (!save.exists()) {
                    save.mkdirs();
                }
                File profileInfoFile = new File(save, "profile_info.pb");
                File profileInfoLockFile = new File(save, "profile_info.pb.lock");
                if (!profileInfoLockFile.exists()) {
                    try {
                        profileInfoLockFile.createNewFile();
                    } catch (IOException e) {
                        Log.d(TAG, "onCreate: " + e);
                    }
                }
                if (!profileInfoFile.exists()) {
                    RequestRunnable requestRunnable = new RequestRunnable(getContext(), service, accountsByType[i]);
                    executor.execute(requestRunnable);
                }

            }
        }


        return true;
    }

    private static class RequestRunnable implements Runnable {

        private final Context context;
        private final String service;
        private final Account account;

        public RequestRunnable(Context context, String service, Account account) {
            this.context = context;
            this.service = service;
            this.account = account;
        }

        @Override
        public void run() {
            AuthResponse authResponse = AuthServiceManager.Companion.getInstance().requestSharedDataAuth(context, context.getPackageName(), account, service);
            PeopleUtils.startPeopleConfigGrpc(context, "Bearer " + authResponse.auth);
        }
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
