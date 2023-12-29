package com.google.android.libraries.photos.backup.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class AutoBackupState implements Parcelable {
    public static final Creator<AutoBackupState> CREATOR = new Creator<AutoBackupState>() {
        @Override  // android.os.Parcelable$Creator
        public final AutoBackupState createFromParcel(Parcel parcel0) {
            return new AutoBackupState(parcel0);
        }
        @Override  // android.os.Parcelable$Creator
        public final AutoBackupState[] newArray(int v) {
            return new AutoBackupState[v];
        }
    };

    public final String account_name;
    private final boolean original_size;
    private final boolean use_data;

    public AutoBackupState(Parcel parcel0) {
        Bundle bundle = parcel0.readBundle();
        this.account_name = bundle.getString("account_name");
        this.original_size = bundle.getBoolean("original_size");
        this.use_data = bundle.getBoolean("use_data");
    }

    @Override  // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override  // android.os.Parcelable
    public void writeToParcel(Parcel parcel0, int v) {
        Bundle bundle0 = new Bundle();
        bundle0.putString("account_name", this.account_name);
        bundle0.putBoolean("original_size", this.original_size);
        bundle0.putBoolean("use_data", this.use_data);
        parcel0.writeBundle(bundle0);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AutoBackupState{account_name=");
        stringBuilder.append(this.account_name);
        stringBuilder.append(", original_size=");
        stringBuilder.append(this.original_size);
        stringBuilder.append(", use_data=");
        stringBuilder.append(this.use_data);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}

