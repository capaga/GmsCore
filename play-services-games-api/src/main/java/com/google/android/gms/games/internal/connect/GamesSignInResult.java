package com.google.android.gms.games.internal.connect;

import org.microg.safeparcel.AutoSafeParcelable;

public class GamesSignInResult extends AutoSafeParcelable {
    @Field(1)
    private String gameRunToken;

    public GamesSignInResult() {

    }

    public GamesSignInResult(String gameRunToken) {
        this.gameRunToken = gameRunToken;
    }

    public String getGameRunToken() {
        return gameRunToken;
    }

    public void setGameRunToken(String gameRunToken) {
        this.gameRunToken = gameRunToken;
    }


    public static final Creator<GamesSignInResult> CREATOR = new AutoCreator<>(GamesSignInResult.class);
}
