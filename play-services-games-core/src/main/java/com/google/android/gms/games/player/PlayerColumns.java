package com.google.android.gms.games.player;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

public final class PlayerColumns {
    private static final String TAG = PlayerColumns.class.getSimpleName();
    public final String gamerTag;
    public final String realName;
    public final String bannerImageLandscapeUri;
    public final String bannerImageLandscapeUrl;
    public final String bannerImagePortraitUri;
    public final String bannerImagePortraitUrl;
    public final String gamerFriendStatus;
    public final String gamerFriendUpdateTimestamp;
    public final String isMuted;
    public final String totalUnlockedAchievements;
    public final String playTogetherFriendStatus;
    public final String playTogetherNickname;
    public final String playTogetherInvitationNickname;
    public final String nicknameAbuseReportToken;
    public final String friendsListVisibility;
    public final String alwaysAutoSignIn;
    public final String externalPlayerId;
    public final String profileName;
    public final String profileIconImageUri;
    public final String profileIconImageUrl;
    public final String profileHiResImageUri;
    public final String profileHiResImageUrl;
    public final String lastUpdated;
    public final String isInCircles;
    public final String playedWithTimestamp;
    public final String currentXpTotal;
    public final String currentLevel;
    public final String currentLevelMinXp;
    public final String currentLevelMaxXp;
    public final String nextLevel;
    public final String nextLevelMaxXp;
    public final String lastLevelUpTimestamp;
    public final String playerTitle;
    public final String hasAllPublicAcls;
    public final String isProfileVisible;
    public final String mostRecentExternalGameId;
    public final String mostRecentGameName;
    public final String mostRecentActivityTimestamp;
    public final String mostRecentGameIconUri;
    public final String mostRecentGameHiResUri;
    public final String mostRecentGameFeaturedUri;
    public final String hasDebugAccess;

    public final static PlayerColumns DEFAULT = new PlayerColumns();

    private PlayerColumns() {
        this.externalPlayerId = "external_player_id";
        this.profileName = "profile_name";
        this.profileIconImageUri = "profile_icon_image_uri";
        this.profileIconImageUrl = "profile_icon_image_url";
        this.profileHiResImageUri = "profile_hi_res_image_uri";
        this.profileHiResImageUrl = "profile_hi_res_image_url";
        this.lastUpdated = "last_updated";
        this.isInCircles = "is_in_circles";
        this.playedWithTimestamp = "played_with_timestamp";
        this.currentXpTotal = "current_xp_total";
        this.currentLevel = "current_level";
        this.currentLevelMinXp = "current_level_min_xp";
        this.currentLevelMaxXp = "current_level_max_xp";
        this.nextLevel = "next_level";
        this.nextLevelMaxXp = "next_level_max_xp";
        this.lastLevelUpTimestamp = "last_level_up_timestamp";
        this.playerTitle = "player_title";
        this.hasAllPublicAcls = "has_all_public_acls";
        this.isProfileVisible = "is_profile_visible";
        this.mostRecentExternalGameId = "most_recent_external_game_id";
        this.mostRecentGameName = "most_recent_game_name";
        this.mostRecentActivityTimestamp = "most_recent_activity_timestamp";
        this.mostRecentGameIconUri = "most_recent_game_icon_uri";
        this.mostRecentGameHiResUri = "most_recent_game_hi_res_uri";
        this.mostRecentGameFeaturedUri = "most_recent_game_featured_uri";
        this.hasDebugAccess = "has_debug_access";
        this.gamerTag = "gamer_tag";
        this.realName = "real_name";
        this.bannerImageLandscapeUri = "banner_image_landscape_uri";
        this.bannerImageLandscapeUrl = "banner_image_landscape_url";
        this.bannerImagePortraitUri = "banner_image_portrait_uri";
        this.bannerImagePortraitUrl = "banner_image_portrait_url";
        this.gamerFriendStatus = "gamer_friend_status";
        this.gamerFriendUpdateTimestamp = "gamer_friend_update_timestamp";
        this.isMuted = "is_muted";
        this.totalUnlockedAchievements = "total_unlocked_achievements";
        this.playTogetherFriendStatus = "play_together_friend_status";
        this.playTogetherNickname = "play_together_nickname";
        this.playTogetherInvitationNickname = "play_together_invitation_nickname";
        this.nicknameAbuseReportToken = "nickname_abuse_report_token";
        this.friendsListVisibility = "friends_list_visibility";
        this.alwaysAutoSignIn = "always_auto_sign_in";
    }

    public String[] getColumns() {
        try {
            ArrayList<String> columns = new ArrayList<>();
            Field[] declaredFields = this.getClass().getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                declaredFields[i].setAccessible(true);
                if (declaredFields[i].getType().equals(String.class)) {
                    columns.add((String) declaredFields[i].get(this));
                }
            }
            return columns.toArray(new String[0]);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getColumns", e);
        }
        return null;
    }
}