package com.google.android.gms.auth.api.identity;

import org.microg.safeparcel.AutoSafeParcelable;

public class GetPhoneNumberHintIntentRequest extends AutoSafeParcelable {
    @Field(1)
    public int a;

    public static final Creator<GetPhoneNumberHintIntentRequest> CREATOR = new AutoCreator<>(GetPhoneNumberHintIntentRequest.class);
}
