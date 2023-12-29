/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.app.backup;

import androidx.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Descriptive information about a set of backed-up app data available for restore.
 * Used by IRestoreSession clients.
 *
 * @hide
 */


/**
 * 从安卓 Frameworks复制的代码
 * /frameworks/base/core/java/android/app/backup/RestoreSet.java
 */

public class RestoreSet implements Parcelable {
    @Nullable
    public String name;


    @Nullable
    public String device;


    public long token;


    @BackupTransportFlags
    public final int backupTransportFlags;


    public RestoreSet() {
        // Leave everything zero / null
        backupTransportFlags = 0;
    }


    public RestoreSet(@Nullable String name, @Nullable String device, long token) {
        this(name, device, token, /* backupTransportFlags */ 0);
    }

    public RestoreSet(@Nullable String name, @Nullable String device, long token,
            @BackupTransportFlags int backupTransportFlags) {
        this.name = name;
        this.device = device;
        this.token = token;
        this.backupTransportFlags = backupTransportFlags;
    }

    // Parcelable implementation
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(device);
        out.writeLong(token);
        out.writeInt(backupTransportFlags);
    }

    public static final @androidx.annotation.NonNull Parcelable.Creator<RestoreSet> CREATOR
            = new Parcelable.Creator<RestoreSet>() {
        public RestoreSet createFromParcel(Parcel in) {
            return new RestoreSet(in);
        }

        public RestoreSet[] newArray(int size) {
            return new RestoreSet[size];
        }
    };

    private RestoreSet(Parcel in) {
        name = in.readString();
        device = in.readString();
        token = in.readLong();
        backupTransportFlags = in.readInt();
    }
}
