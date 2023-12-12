/*
 * Copyright (C) 2013-2017 microG Project Team
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

package org.microg.gms.reminders;

import android.accounts.AccountManager;
import android.os.Build;
import android.os.RemoteException;

import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;

public class RemindersService extends BaseService {
    private final AccountListener accountListener;
    private static boolean listenerSet = false;

    public RemindersService() {
        super("GmsRemindSvc", GmsService.REMINDERS);
        accountListener = new AccountListener(this);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        RemindersServiceImpl binder = new RemindersServiceImpl(this, request.account);
        callback.onPostInitComplete(0, binder, null);
        if (!listenerSet) {
            listenerSet = true;
            AccountManager manager = AccountManager.get(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.addOnAccountsUpdatedListener(accountListener, null, true, new String[]{"com.google"});
            } else {
                manager.addOnAccountsUpdatedListener(accountListener, null, true);
            }
        }
    }
}
