package com.google.android.gms.games.internal.connect;


import org.microg.safeparcel.AutoSafeParcelable;

public class GamesSignInRequest extends AutoSafeParcelable {
    @Field(1)
    private int signInType;
    @Field(2)
    private PreviousStepResolutionResult previousStepResolutionResult;

    public static final Creator<GamesSignInRequest> CREATOR = new AutoCreator<>(GamesSignInRequest.class);

    public int getSignInType() {
        return signInType;
    }

    public void setSignInType(int signInType) {
        this.signInType = signInType;
    }

    public PreviousStepResolutionResult getPreviousStepResolutionResult() {
        return previousStepResolutionResult;
    }

    public void setPreviousStepResolutionResult(PreviousStepResolutionResult previousStepResolutionResult) {
        this.previousStepResolutionResult = previousStepResolutionResult;
    }
}
