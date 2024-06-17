package com.google.android.gms.games.player;

import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.IntDef;

import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.games.PlayersClient;

public interface Player extends Freezable<Player>, Parcelable {
    @IntDef({FriendsListVisibilityStatus.UNKNOWN, FriendsListVisibilityStatus.VISIBLE, FriendsListVisibilityStatus.REQUEST_REQUIRED, FriendsListVisibilityStatus.FEATURE_UNAVAILABLE})
    @interface FriendsListVisibilityStatus {
        /**
         * Constant indicating that currently it's unknown if the friends list is visible to the game, or whether the game can ask for
         * access from the user. Use {@link PlayersClient#getCurrentPlayer(boolean)} to force reload the latest status.
         */
        int UNKNOWN = 0;
        /**
         * Constant indicating that the friends list is currently visible to the game.
         */
        int VISIBLE = 1;
        /**
         * Constant indicating that the friends list is not visible to the game, but the game can ask for access.
         */
        int REQUEST_REQUIRED = 2;
        /**
         * Constant indicating that the friends list is currently unavailable for the game. You cannot request access at this time,
         * either because the user has permanently declined or the friends feature is not available to them. In this state, any
         * attempts to request access to the friends list will be unsuccessful.
         */
        int FEATURE_UNAVAILABLE = 3;
    }

    @IntDef({PlayerFriendStatus.UNKNOWN, PlayerFriendStatus.NO_RELATIONSHIP, PlayerFriendStatus.FRIEND})
    @interface PlayerFriendStatus {
        /**
         * Constant indicating that the currently signed-in player's friend status with this player is unknown. This may happen if the
         * user has not shared the friends list with the game.
         */
        int UNKNOWN = -1;
        /**
         * Constant indicating that the currently signed-in player is not a friend of this player, and there are no pending invitations
         * between them.
         */
        int NO_RELATIONSHIP = 0;
        /**
         * Constant indicating that the currently signed-in player and this player are friends.
         */
        int FRIEND = 4;
    }

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
