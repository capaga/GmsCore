package com.google.android.gms.games.player;

import android.net.Uri;

public interface Player {
    @Deprecated
    int isInCircles();

    @Deprecated
    long getPlayedWithTimestamp();

    long getRetrievedTimestamp();

    long getTotalUnlockedAchievement();

    Uri getBannerImageLandscapeUri();

    Uri getBannerImagePortraitUri();

    @Deprecated
    String getBannerImageLandscapeUrl();

    @Deprecated
    String getBannerImagePortraitUrl();

    @Deprecated
    String getHiResImageUrl();

    @Deprecated
    String getIconImageUrl();

    Uri getHiResImageUri();

    Uri getIconImageUri();

    LevelInfo getLevelInfo();

    RelationshipInfo getRelationshipInfo();

    MostRecentGameInfo getMostRecentGameInfo();

    String getDisplayName();

    String getGamerTag();

    String getName();

    String getPlayerId();

    String getTitle();

    boolean hasDebugAccess();

    boolean isAlwaysAutoSignIn();

    boolean isProfileVisible();
}
