package com.google.android.gms.potokens;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.List;

public class PoToken extends AutoSafeParcelable {

    @Field(1)
    public  byte[] a;

    public PoToken(byte[] a) {
        this.a = a;
    }

    public static Creator<PoToken> CREATOR = new AutoCreator<>(PoToken.class);
}
