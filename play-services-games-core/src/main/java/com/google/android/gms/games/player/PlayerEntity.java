package com.google.android.gms.games.player;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.games.beans.FirstPartPlayer;
import com.google.android.gms.games.beans.GamesPlayer;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerEntity implements Player {
    private static final String TAG = PlayerEntity.class.getSimpleName();
    private String playerId;
    private String displayName;
    private Uri iconImageUri;
    private Uri hiResImageUri;
    private long retrievedTimestamp;
    private int mIsInCircles;
    private long playedWithTimestamp;
    private String title;
    private MostRecentGameInfo mostRecentGameInfoEntity;
    private LevelInfo levelInfo;
    private boolean mIsProfileVisible;
    private boolean mHasDebugAccess;
    private String gamerTag;
    private String name;
    private Uri bannerImageLandscapeUri;
    private Uri bannerImagePortraitUri;
    private long totalUnlockedAchievement;
    private RelationshipInfo relationshipInfo;
    private boolean mIsAlwaysAutoSignIn;
    private String iconImageUrl;
    private String hiResImageUrl;
    private String bannerImageLandscapeUrl;
    private String bannerImagePortraitUrl;

    public PlayerEntity(){}

    public PlayerEntity(FirstPartPlayer firstPartPlayer) {
        playerId = firstPartPlayer.displayPlayer.playerId;
        displayName = firstPartPlayer.displayPlayer.displayName;
        iconImageUri = Uri.parse(firstPartPlayer.displayPlayer.avatarImageUrl);
        iconImageUrl = firstPartPlayer.displayPlayer.avatarImageUrl;
        hiResImageUrl = firstPartPlayer.displayPlayer.avatarImageUrl;
        hiResImageUri = Uri.parse(firstPartPlayer.displayPlayer.avatarImageUrl);
        retrievedTimestamp = System.currentTimeMillis();
        title = firstPartPlayer.displayPlayer.title;
        gamerTag = firstPartPlayer.displayPlayer.gamerTag;
        bannerImageLandscapeUri = Uri.parse(firstPartPlayer.displayPlayer.bannerUrlLandscape);
        bannerImageLandscapeUrl = firstPartPlayer.displayPlayer.bannerUrlLandscape;
        bannerImagePortraitUri = Uri.parse(firstPartPlayer.displayPlayer.bannerUrlPortrait);
        bannerImagePortraitUrl = firstPartPlayer.displayPlayer.bannerUrlPortrait;
        try {
            mIsProfileVisible = firstPartPlayer.displayPlayer.profileSettings.profileVisible;
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            mIsProfileVisible = false;
        }
        try {
            mostRecentGameInfoEntity = new MostRecentGameInfo(firstPartPlayer.displayPlayer.lastPlayedApp);
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            mostRecentGameInfoEntity = null;
        }
        try {
            levelInfo = new LevelInfo(firstPartPlayer.displayPlayer.experienceInfo);
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            levelInfo = null;
        }
        try {
            name = firstPartPlayer.displayPlayer.name.fullName;
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            name = "";
        }
        try {
            totalUnlockedAchievement = Long.parseLong(firstPartPlayer.displayPlayer.experienceInfo.totalUnlockedAchievements);
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            totalUnlockedAchievement = 0;
        }
        try {
            mIsAlwaysAutoSignIn = firstPartPlayer.displayPlayer.profileSettings.alwaysAutoSignIn;
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            mIsAlwaysAutoSignIn = false;
        }
        relationshipInfo = null;
        mIsInCircles = 0;
        playedWithTimestamp = 0;
        mHasDebugAccess = false;
    }

    public void update(GamesPlayer gamesPlayer) {
        playerId = gamesPlayer.playerId;
        displayName = gamesPlayer.displayName;
        iconImageUri = Uri.parse(gamesPlayer.avatarImageUrl);
        iconImageUrl = gamesPlayer.avatarImageUrl;
        hiResImageUrl = gamesPlayer.avatarImageUrl;
        hiResImageUri = Uri.parse(gamesPlayer.avatarImageUrl);
        retrievedTimestamp = System.currentTimeMillis();
        title = gamesPlayer.title;
        bannerImageLandscapeUri = Uri.parse(gamesPlayer.bannerUrlLandscape);
        bannerImageLandscapeUrl = gamesPlayer.bannerUrlLandscape;
        bannerImagePortraitUri = Uri.parse(gamesPlayer.bannerUrlPortrait);
        bannerImagePortraitUrl = gamesPlayer.bannerUrlPortrait;
        try {
            mIsProfileVisible = gamesPlayer.profileSettings.profileVisible;
            levelInfo = new LevelInfo(gamesPlayer.experienceInfo);
        } catch (Exception e) {
            Log.w(TAG, "failed to get value", e);
            mIsProfileVisible = false;
            levelInfo = null;
        }
    }

    private void putUri(ContentValues contentValues, String key, Uri uri) {
        contentValues.put(key, (uri == null ? null : uri.toString()));
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        PlayerColumns playerColumns = PlayerColumns.DEFAULT;
        contentValues.put(playerColumns.externalPlayerId, getPlayerId());
        contentValues.put(playerColumns.profileName, getDisplayName());
        contentValues.put(playerColumns.gamerTag, getGamerTag());
        contentValues.put(playerColumns.realName, getName());
        putUri(contentValues, playerColumns.profileIconImageUri, getIconImageUri());
        contentValues.put(playerColumns.profileIconImageUrl, getIconImageUrl());
        putUri(contentValues, playerColumns.profileHiResImageUri, getHiResImageUri());
        contentValues.put(playerColumns.profileHiResImageUrl, getHiResImageUrl());
        putUri(contentValues, playerColumns.bannerImageLandscapeUri, getBannerImageLandscapeUri());
        contentValues.put(playerColumns.bannerImageLandscapeUrl, getBannerImageLandscapeUrl());
        putUri(contentValues, playerColumns.bannerImagePortraitUri, getBannerImagePortraitUri());
        contentValues.put(playerColumns.bannerImagePortraitUrl, getBannerImagePortraitUrl());
        contentValues.put(playerColumns.lastUpdated, getRetrievedTimestamp());
        contentValues.put(playerColumns.isInCircles, isInCircles());
        contentValues.put(playerColumns.playedWithTimestamp, getPlayedWithTimestamp());
        contentValues.put(playerColumns.playerTitle, getTitle());
        contentValues.put(playerColumns.isProfileVisible, isProfileVisible());
        contentValues.put(playerColumns.hasDebugAccess, hasDebugAccess());
        contentValues.put(playerColumns.gamerFriendStatus, 0);
        contentValues.put(playerColumns.gamerFriendUpdateTimestamp, 0L);
        contentValues.put(playerColumns.isMuted, Boolean.FALSE);
        contentValues.put(playerColumns.totalUnlockedAchievements, getTotalUnlockedAchievement());
        contentValues.put(playerColumns.totalUnlockedAchievements, getTotalUnlockedAchievement());
        contentValues.put(playerColumns.alwaysAutoSignIn, isAlwaysAutoSignIn());
        contentValues.put(playerColumns.hasAllPublicAcls, isProfileVisible());
        LevelInfo levelInfo = getLevelInfo();
        if (levelInfo == null) {
            contentValues.putNull(playerColumns.currentLevel);
            contentValues.putNull(playerColumns.currentLevelMinXp);
            contentValues.putNull(playerColumns.currentLevelMaxXp);
            contentValues.putNull(playerColumns.nextLevel);
            contentValues.putNull(playerColumns.nextLevelMaxXp);
            contentValues.put(playerColumns.lastLevelUpTimestamp, -1);
            contentValues.put(playerColumns.currentXpTotal, -1L);
        } else {
            contentValues.put(playerColumns.currentLevel, levelInfo.getCurrentLevel().getLevelNumber());
            contentValues.put(playerColumns.currentLevelMinXp, levelInfo.getCurrentLevel().getMinXp());
            contentValues.put(playerColumns.currentLevelMaxXp, levelInfo.getCurrentLevel().getMaxXp());
            contentValues.put(playerColumns.nextLevel, levelInfo.getNextLevel().getLevelNumber());
            contentValues.put(playerColumns.nextLevelMaxXp, levelInfo.getNextLevel().getMaxXp());
            contentValues.put(playerColumns.lastLevelUpTimestamp, levelInfo.getLastLevelUpTimestamp());
            contentValues.put(playerColumns.currentXpTotal, levelInfo.getCurrentXpTotal());
        }

        MostRecentGameInfo mostRecentGameInfo = getMostRecentGameInfo();
        if (mostRecentGameInfo == null) {
            contentValues.putNull(playerColumns.mostRecentExternalGameId);
            contentValues.putNull(playerColumns.mostRecentGameName);
            contentValues.putNull(playerColumns.mostRecentActivityTimestamp);
            contentValues.putNull(playerColumns.mostRecentGameIconUri);
            contentValues.putNull(playerColumns.mostRecentGameHiResUri);
            contentValues.putNull(playerColumns.mostRecentGameFeaturedUri);
        } else {
            contentValues.put(playerColumns.mostRecentExternalGameId, mostRecentGameInfo.gameId);
            contentValues.put(playerColumns.mostRecentGameName, mostRecentGameInfo.gameName);
            contentValues.put(playerColumns.mostRecentActivityTimestamp, mostRecentGameInfo.activityTimestampMillis);
            putUri(contentValues, playerColumns.mostRecentGameIconUri, mostRecentGameInfo.gameIconUri);
            putUri(contentValues, playerColumns.mostRecentGameHiResUri, mostRecentGameInfo.gameHiResUri);
            putUri(contentValues, playerColumns.mostRecentGameFeaturedUri, mostRecentGameInfo.gameFeatureUri);
        }

        RelationshipInfo relationshipInfo = getRelationshipInfo();
        if (relationshipInfo == null) {
            contentValues.putNull(playerColumns.playTogetherFriendStatus);
            contentValues.putNull(playerColumns.playTogetherNickname);
            contentValues.putNull(playerColumns.playTogetherInvitationNickname);
            contentValues.putNull(playerColumns.nicknameAbuseReportToken);
        } else {
            contentValues.put(playerColumns.playTogetherFriendStatus, relationshipInfo.getFriendStatus());
            contentValues.put(playerColumns.playTogetherNickname, relationshipInfo.getNickName());
            contentValues.put(playerColumns.playTogetherInvitationNickname, relationshipInfo.getInvitationNickname());
            contentValues.put(playerColumns.nicknameAbuseReportToken, relationshipInfo.getNickName());
        }

        contentValues.putNull(playerColumns.friendsListVisibility);

        Log.d(TAG, "contentValues: " + contentValues);
        return contentValues;
    }

    public static String toJson(PlayerEntity playerEntity) {
        JSONObject jsonObject = new JSONObject();
        if (playerEntity == null) return jsonObject.toString();
        try {
            jsonObject.put("playerId", playerEntity.getPlayerId());
            jsonObject.put("displayName", playerEntity.getDisplayName());
            jsonObject.put("iconImageUri", playerEntity.getIconImageUri() != null ? playerEntity.getIconImageUri().toString() : "");
            jsonObject.put("hiResImageUri", playerEntity.getHiResImageUri() != null ? playerEntity.getHiResImageUri().toString() : "");
            jsonObject.put("retrievedTimestamp", playerEntity.getRetrievedTimestamp());
            jsonObject.put("mIsInCircles", playerEntity.isInCircles());
            jsonObject.put("playedWithTimestamp", playerEntity.getPlayedWithTimestamp());
            jsonObject.put("title", playerEntity.getTitle());
            jsonObject.put("mostRecentGameInfoEntity", MostRecentGameInfo.toJson(playerEntity.getMostRecentGameInfoEntity()));
            jsonObject.put("levelInfo", LevelInfo.toJson(playerEntity.getLevelInfo()));
            jsonObject.put("mIsProfileVisible", playerEntity.isProfileVisible());
            jsonObject.put("mHasDebugAccess", playerEntity.isHasDebugAccess());
            jsonObject.put("gamerTag", playerEntity.getGamerTag());
            jsonObject.put("name", playerEntity.getName());
            jsonObject.put("bannerImageLandscapeUri", playerEntity.getBannerImageLandscapeUri() != null
                    ? playerEntity.getBannerImageLandscapeUri().toString() : "");
            jsonObject.put("bannerImagePortraitUri", playerEntity.getBannerImagePortraitUri() != null
                    ? playerEntity.getBannerImagePortraitUri().toString() : "");
            jsonObject.put("totalUnlockedAchievement", playerEntity.getTotalUnlockedAchievement());
            jsonObject.put("relationshipInfo", RelationshipInfo.toJson(playerEntity.getRelationshipInfo()));
            jsonObject.put("mIsAlwaysAutoSignIn", playerEntity.isAlwaysAutoSignIn());
            jsonObject.put("iconImageUrl", playerEntity.getIconImageUrl());
            jsonObject.put("hiResImageUrl", playerEntity.getHiResImageUrl());
            jsonObject.put("bannerImageLandscapeUrl", playerEntity.getBannerImageLandscapeUrl());
            jsonObject.put("bannerImagePortraitUrl", playerEntity.getBannerImagePortraitUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static PlayerEntity fromJson(String json) {
        PlayerEntity playerEntity = new PlayerEntity();
        try {
            JSONObject jsonObject = new JSONObject(json);
            playerEntity.setPlayerId(jsonObject.getString("playerId"));
            playerEntity.setDisplayName(jsonObject.getString("displayName"));
            playerEntity.setIconImageUri(Uri.parse(jsonObject.getString("iconImageUri")));
            playerEntity.setHiResImageUri(Uri.parse(jsonObject.getString("hiResImageUri")));
            playerEntity.setRetrievedTimestamp(jsonObject.getLong("retrievedTimestamp"));
            playerEntity.setIsInCircles(jsonObject.getInt("mIsInCircles"));
            playerEntity.setPlayedWithTimestamp(jsonObject.getLong("playedWithTimestamp"));
            playerEntity.setTitle(jsonObject.getString("title"));
            playerEntity.setMostRecentGameInfoEntity(MostRecentGameInfo.fromJson(jsonObject.getJSONObject("mostRecentGameInfoEntity").toString()));
            playerEntity.setLevelInfo(LevelInfo.fromJson(jsonObject.getJSONObject("levelInfo").toString()));
            playerEntity.setIsProfileVisible(jsonObject.getBoolean("mIsProfileVisible"));
            playerEntity.setHasDebugAccess(jsonObject.getBoolean("mHasDebugAccess"));
            playerEntity.setGamerTag(jsonObject.getString("gamerTag"));
            playerEntity.setName(jsonObject.getString("name"));
            playerEntity.setBannerImageLandscapeUri(Uri.parse(jsonObject.getString("bannerImageLandscapeUri")));
            playerEntity.setBannerImagePortraitUri(Uri.parse(jsonObject.getString("bannerImagePortraitUri")));
            playerEntity.setTotalUnlockedAchievement(jsonObject.getLong("totalUnlockedAchievement"));
            playerEntity.setRelationshipInfo(RelationshipInfo.fromJson(jsonObject.getJSONObject("relationshipInfo").toString()));
            playerEntity.setIsAlwaysAutoSignIn(jsonObject.getBoolean("mIsAlwaysAutoSignIn"));
            playerEntity.setIconImageUrl(jsonObject.getString("iconImageUrl"));
            playerEntity.setHiResImageUrl(jsonObject.getString("hiResImageUrl"));
            playerEntity.setBannerImageLandscapeUrl(jsonObject.getString("bannerImageLandscapeUrl"));
            playerEntity.setBannerImagePortraitUrl(jsonObject.getString("bannerImagePortraitUrl"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playerEntity;
    }

    public void setRelationshipInfo(RelationshipInfo relationshipInfo) {
        this.relationshipInfo = relationshipInfo;
    }

    public void setGamerTag(String gamerTag) {
        this.gamerTag = gamerTag;
    }

    public void setHasDebugAccess(boolean hasDebugAccess) {
        this.mHasDebugAccess = hasDebugAccess;
    }

    public void setIsInCircles(int isInCircles) {
        this.mIsInCircles = isInCircles;
    }

    public void setPlayedWithTimestamp(long playedWithTimestamp) {
        this.playedWithTimestamp = playedWithTimestamp;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setIconImageUri(Uri iconImageUri) {
        this.iconImageUri = iconImageUri;
    }

    public void setHiResImageUri(Uri hiResImageUri) {
        this.hiResImageUri = hiResImageUri;
    }

    public void setRetrievedTimestamp(long retrievedTimestamp) {
        this.retrievedTimestamp = retrievedTimestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MostRecentGameInfo getMostRecentGameInfoEntity() {
        return mostRecentGameInfoEntity;
    }

    public void setMostRecentGameInfoEntity(MostRecentGameInfo mostRecentGameInfoEntity) {
        this.mostRecentGameInfoEntity = mostRecentGameInfoEntity;
    }

    public void setLevelInfo(LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
    }

    public void setIsProfileVisible(boolean mIsProfileVisible) {
        this.mIsProfileVisible = mIsProfileVisible;
    }

    public boolean isHasDebugAccess() {
        return mHasDebugAccess;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBannerImageLandscapeUri(Uri bannerImageLandscapeUri) {
        this.bannerImageLandscapeUri = bannerImageLandscapeUri;
    }

    public void setBannerImagePortraitUri(Uri bannerImagePortraitUri) {
        this.bannerImagePortraitUri = bannerImagePortraitUri;
    }

    public void setTotalUnlockedAchievement(long totalUnlockedAchievement) {
        this.totalUnlockedAchievement = totalUnlockedAchievement;
    }

    public void setIsAlwaysAutoSignIn(boolean mIsAlwaysAutoSignIn) {
        this.mIsAlwaysAutoSignIn = mIsAlwaysAutoSignIn;
    }

    public void setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
    }

    public void setHiResImageUrl(String hiResImageUrl) {
        this.hiResImageUrl = hiResImageUrl;
    }

    public void setBannerImageLandscapeUrl(String bannerImageLandscapeUrl) {
        this.bannerImageLandscapeUrl = bannerImageLandscapeUrl;
    }

    public void setBannerImagePortraitUrl(String bannerImagePortraitUrl) {
        this.bannerImagePortraitUrl = bannerImagePortraitUrl;
    }

    @Override
    public int isInCircles() {
        return mIsInCircles;
    }

    @Override
    public long getPlayedWithTimestamp() {
        return playedWithTimestamp;
    }

    @Override
    public long getRetrievedTimestamp() {
        return retrievedTimestamp;
    }

    @Override
    public long getTotalUnlockedAchievement() {
        return totalUnlockedAchievement;
    }

    @Override
    public Uri getBannerImageLandscapeUri() {
        return bannerImageLandscapeUri;
    }

    @Override
    public Uri getBannerImagePortraitUri() {
        return bannerImagePortraitUri;
    }

    @Override
    public String getBannerImageLandscapeUrl() {
        return bannerImageLandscapeUrl;
    }

    @Override
    public String getBannerImagePortraitUrl() {
        return bannerImagePortraitUrl;
    }

    @Override
    public String getHiResImageUrl() {
        return hiResImageUrl;
    }

    @Override
    public String getIconImageUrl() {
        return iconImageUrl;
    }

    @Override
    public Uri getHiResImageUri() {
        return hiResImageUri;
    }

    @Override
    public Uri getIconImageUri() {
        return iconImageUri;
    }

    @Override
    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    @Override
    public RelationshipInfo getRelationshipInfo() {
        return relationshipInfo;
    }

    @Override
    public MostRecentGameInfo getMostRecentGameInfo() {
        return mostRecentGameInfoEntity;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getGamerTag() {
        return gamerTag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean hasDebugAccess() {
        return mHasDebugAccess;
    }

    @Override
    public boolean isAlwaysAutoSignIn() {
        return mIsAlwaysAutoSignIn;
    }

    @Override
    public boolean isProfileVisible() {
        return mIsProfileVisible;
    }
}
