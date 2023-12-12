/*
 * Copyright (C) 2017 microG Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.gms.people;

import static org.microg.gms.ui.AskPermissionActivityKt.EXTRA_PERMISSIONS;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import org.microg.gms.ui.AskPermissionActivity;


public class ContactSyncService extends Service {
    private static final String TAG = ContactSyncService.class.getSimpleName();
    private static ContactSyncAdapter instance;
    private final Object obj = new Object();

    @Override
    public void onCreate() {
        synchronized(this.obj) {
            if(instance == null){
                instance = new ContactSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"enter onbind");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onPerformSync: ");
            Intent permissionIntent = new Intent(this, AskPermissionActivity.class);
            permissionIntent.putExtra(EXTRA_PERMISSIONS, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS});
            permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissionIntent);
        }
        return instance.getSyncAdapterBinder();
    }
}
