package com.google.android.gms.games.internal.connect;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.internal.connect.GamesSignInResult;

interface IGamesConnectCallbacks {
    void onSiginInComplete(in Status status, in GamesSignInResult gamesSignInResult) = 1;
}