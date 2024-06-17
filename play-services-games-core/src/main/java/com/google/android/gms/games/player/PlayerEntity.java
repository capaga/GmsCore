package com.google.android.gms.games.player;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableCreatorAndWriter;
import com.google.android.gms.games.CurrentPlayerInfo;
import com.google.android.gms.games.CurrentPlayerInfoEntity;
import com.google.android.gms.games.beans.FirstPartPlayer;
import com.google.android.gms.games.beans.GamesPlayer;

import org.json.JSONException;
import org.json.JSONObject;
import org.microg.gms.common.Hide;
import org.microg.safeparcel.AutoSafeParcelable;

@SuppressLint("ParcelCreator")
public class PlayerEntity extends AutoSafeParcelable implements Player {
    public static final SafeParcelableCreatorAndWriter<PlayerEntity> CREATOR = findCreator(PlayerEntity.class);
    private static final String TAG = PlayerEntity.class.getSimpleName();
    @Field(1)
    private String playerId;
    @Field(2)
    private String displayName;
    @Field(3)
    private Uri iconImageUri;
    @Field(4)
    private Uri hiResImageUri;
    @Field(5)
    private long retrievedTimestamp;
    @Field(6)
    private int mIsInCircles;
    @Field(7)
    private long playedWithTimestamp;
    @Field(8)
    private String iconImageUrl;
    @Field(9)
    private String hiResImageUrl;
    @Field(14)
    private String title;
    @Field(15)
    private MostRecentGameInfo mostRecentGameInfoEntity;
    @Field(16)
    private LevelInfo levelInfo;
    @Field(18)
    private boolean mIsProfileVisible;
    @Field(19)
    private boolean mHasDebugAccess;
    @Field(20)
    private String gamerTag;
    @Field(21)
    private String name;
    @Field(22)
    private Uri bannerImageLandscapeUri;
    @Field(23)
    private String bannerImageLandscapeUrl;
    @Field(24)
    private Uri bannerImagePortraitUri;
    @Field(25)
    private String bannerImagePortraitUrl;
    @Field(29)
    private long totalUnlockedAchievement = -1;
    @Field(33)
    private RelationshipInfo relationshipInfo;
    @Field(35)
    private CurrentPlayerInfoEntity currentPlayerInfo;
    @Field(36)
    private boolean mIsAlwaysAutoSignIn;
    @Field(37)
    private String gamePlayerId;

    public PlayerEntity(){}

    @Hide
    public PlayerEntity(String playerId, String displayName, Uri iconImageUri, Uri hiResImageUri, long retrievedTimestamp, int isInCircles, long lastPlayedWithTimestamp, String iconImageUrl, String hiResImageUrl, String title, MostRecentGameInfo mostRecentGameInfo, LevelInfo levelInfo, boolean profileVisible, boolean hasDebugAccess, String gamerTag, String name, Uri bannerImageLandscapeUri, String bannerImageLandscapeUrl, Uri bannerImagePortraitUri, String bannerImagePortraitUrl, long totalUnlockedAchievement, RelationshipInfo relationshipInfo, CurrentPlayerInfoEntity currentPlayerInfo, boolean alwaysAutoSignIn, String gamePlayerId) {
        this.playerId = playerId;
        this.displayName = displayName;
        this.iconImageUri = iconImageUri;
        this.hiResImageUri = hiResImageUri;
        this.retrievedTimestamp = retrievedTimestamp;
        this.mIsInCircles = isInCircles;
        this.playedWithTimestamp = lastPlayedWithTimestamp;
        this.iconImageUrl = iconImageUrl;
        this.hiResImageUrl = hiResImageUrl;
        this.title = title;
        this.mostRecentGameInfoEntity = mostRecentGameInfo;
        this.levelInfo = levelInfo;
        this.mIsProfileVisible = profileVisible;
        this.mHasDebugAccess = hasDebugAccess;
        this.gamerTag = gamerTag;
        this.name = name;
        this.bannerImageLandscapeUri = bannerImageLandscapeUri;
        this.bannerImageLandscapeUrl = bannerImageLandscapeUrl;
        this.bannerImagePortraitUri = bannerImagePortraitUri;
        this.bannerImagePortraitUrl = bannerImagePortraitUrl;
        this.totalUnlockedAchievement = totalUnlockedAchievement;
        this.relationshipInfo = relationshipInfo;
        this.currentPlayerInfo = currentPlayerInfo;
        this.mIsAlwaysAutoSignIn = alwaysAutoSignIn;
        this.gamePlayerId = gamePlayerId;
    }

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
        contentValues.put(PlayerColumns.externalPlayerId, getPlayerId());
        contentValues.put(PlayerColumns.profileName, getDisplayName());
        contentValues.put(PlayerColumns.gamerTag, getGamerTag());
        contentValues.put(PlayerColumns.realName, getName());
        putUri(contentValues, PlayerColumns.profileIconImageUri, getIconImageUri());
        contentValues.put(PlayerColumns.profileIconImageUrl, getIconImageUrl());
        putUri(contentValues, PlayerColumns.profileHiResImageUri, getHiResImageUri());
        contentValues.put(PlayerColumns.profileHiResImageUrl, getHiResImageUrl());
        putUri(contentValues, PlayerColumns.bannerImageLandscapeUri, getBannerImageLandscapeUri());
        contentValues.put(PlayerColumns.bannerImageLandscapeUrl, getBannerImageLandscapeUrl());
        putUri(contentValues, PlayerColumns.bannerImagePortraitUri, getBannerImagePortraitUri());
        contentValues.put(PlayerColumns.bannerImagePortraitUrl, getBannerImagePortraitUrl());
        contentValues.put(PlayerColumns.lastUpdated, getRetrievedTimestamp());
        contentValues.put(PlayerColumns.isInCircles, isInCircles());
        contentValues.put(PlayerColumns.playedWithTimestamp, getPlayedWithTimestamp());
        contentValues.put(PlayerColumns.playerTitle, getTitle());
        contentValues.put(PlayerColumns.isProfileVisible, isProfileVisible());
        contentValues.put(PlayerColumns.hasDebugAccess, hasDebugAccess());
        contentValues.put(PlayerColumns.gamerFriendStatus, 0);
        contentValues.put(PlayerColumns.gamerFriendUpdateTimestamp, 0L);
        contentValues.put(PlayerColumns.isMuted, Boolean.FALSE);
        contentValues.put(PlayerColumns.totalUnlockedAchievements, getTotalUnlockedAchievement());
        contentValues.put(PlayerColumns.totalUnlockedAchievements, getTotalUnlockedAchievement());
        contentValues.put(PlayerColumns.alwaysAutoSignIn, isAlwaysAutoSignIn());
        contentValues.put(PlayerColumns.hasAllPublicAcls, isProfileVisible());
        LevelInfo levelInfo = getLevelInfo();
        if (levelInfo == null) {
            contentValues.putNull(PlayerColumns.currentLevel);
            contentValues.putNull(PlayerColumns.currentLevelMinXp);
            contentValues.putNull(PlayerColumns.currentLevelMaxXp);
            contentValues.putNull(PlayerColumns.nextLevel);
            contentValues.putNull(PlayerColumns.nextLevelMaxXp);
            contentValues.put(PlayerColumns.lastLevelUpTimestamp, -1);
            contentValues.put(PlayerColumns.currentXpTotal, -1L);
        } else {
            contentValues.put(PlayerColumns.currentLevel, levelInfo.getCurrentLevel().getLevelNumber());
            contentValues.put(PlayerColumns.currentLevelMinXp, levelInfo.getCurrentLevel().getMinXp());
            contentValues.put(PlayerColumns.currentLevelMaxXp, levelInfo.getCurrentLevel().getMaxXp());
            contentValues.put(PlayerColumns.nextLevel, levelInfo.getNextLevel().getLevelNumber());
            contentValues.put(PlayerColumns.nextLevelMaxXp, levelInfo.getNextLevel().getMaxXp());
            contentValues.put(PlayerColumns.lastLevelUpTimestamp, levelInfo.getLastLevelUpTimestamp());
            contentValues.put(PlayerColumns.currentXpTotal, levelInfo.getCurrentXpTotal());
        }

        MostRecentGameInfo mostRecentGameInfo = getMostRecentGameInfo();
        if (mostRecentGameInfo == null) {
            contentValues.putNull(PlayerColumns.mostRecentExternalGameId);
            contentValues.putNull(PlayerColumns.mostRecentGameName);
            contentValues.putNull(PlayerColumns.mostRecentActivityTimestamp);
            contentValues.putNull(PlayerColumns.mostRecentGameIconUri);
            contentValues.putNull(PlayerColumns.mostRecentGameHiResUri);
            contentValues.putNull(PlayerColumns.mostRecentGameFeaturedUri);
        } else {
            contentValues.put(PlayerColumns.mostRecentExternalGameId, mostRecentGameInfo.gameId);
            contentValues.put(PlayerColumns.mostRecentGameName, mostRecentGameInfo.gameName);
            contentValues.put(PlayerColumns.mostRecentActivityTimestamp, mostRecentGameInfo.activityTimestampMillis);
            putUri(contentValues, PlayerColumns.mostRecentGameIconUri, mostRecentGameInfo.gameIconUri);
            putUri(contentValues, PlayerColumns.mostRecentGameHiResUri, mostRecentGameInfo.gameHiResUri);
            putUri(contentValues, PlayerColumns.mostRecentGameFeaturedUri, mostRecentGameInfo.gameFeatureUri);
        }

        RelationshipInfo relationshipInfo = getRelationshipInfo();
        if (relationshipInfo == null) {
            contentValues.putNull(PlayerColumns.playTogetherFriendStatus);
            contentValues.putNull(PlayerColumns.playTogetherNickname);
            contentValues.putNull(PlayerColumns.playTogetherInvitationNickname);
            contentValues.putNull(PlayerColumns.nicknameAbuseReportToken);
        } else {
            contentValues.put(PlayerColumns.playTogetherFriendStatus, relationshipInfo.getFriendStatus());
            contentValues.put(PlayerColumns.playTogetherNickname, relationshipInfo.getNickName());
            contentValues.put(PlayerColumns.playTogetherInvitationNickname, relationshipInfo.getInvitationNickname());
            contentValues.put(PlayerColumns.nicknameAbuseReportToken, relationshipInfo.getNickName());
        }

        contentValues.putNull(PlayerColumns.friendsListVisibility);

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

    public long getLastPlayedWithTimestamp() {
        return playedWithTimestamp;
    }

    @Hide
    public boolean getHasDebugAccess() {
        return mHasDebugAccess;
    }

    public CurrentPlayerInfo getCurrentPlayerInfo() {
        return currentPlayerInfo;
    }

    @Override
    public Player freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
