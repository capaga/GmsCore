/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package com.google.android.gms.auth.api.signin.internal;

import android.os.Bundle;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelableCreatorAndWriter;
import org.microg.gms.common.Hide;
import org.microg.safeparcel.AutoSafeParcelable;

public class GoogleSignInOptionsExtensionParcelable extends AutoSafeParcelable {
    public static final Creator<GoogleSignInOptionsExtensionParcelable> CREATOR = findCreator(GoogleSignInOptionsExtensionParcelable.class);
    @Field(1)
    public final int versionCode;
    @Field(2)
    public final int type;
    @Field(3)
    public final Bundle bundle;

    public GoogleSignInOptionsExtensionParcelable(GoogleSignInOptionsExtension extension) {
        this(extension.getExtensionType(), extension.toBundle());
    }

    public GoogleSignInOptionsExtensionParcelable(int type, Bundle bundle) {
        this(1, type, bundle);
    }
    public GoogleSignInOptionsExtensionParcelable() {
        this(1, 0, new Bundle());
    }

    @Constructor
    public GoogleSignInOptionsExtensionParcelable(@Param(1) int versionCode, @Param(2) int type, @Param(3) Bundle bundle) {
        this.versionCode = versionCode;
        this.type = type;
        this.bundle = bundle;
    }
}
