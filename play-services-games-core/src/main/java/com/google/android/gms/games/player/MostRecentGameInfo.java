package com.google.android.gms.games.player;

import android.net.Uri;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableCreatorAndWriter;
import com.google.android.gms.games.beans.FirstPartPlayer;
import com.google.android.gms.games.internal.player.MostRecentGameInfoEntity;

import org.json.JSONException;
import org.json.JSONObject;
import org.microg.safeparcel.AutoSafeParcelable;


public class MostRecentGameInfo extends AutoSafeParcelable {
    public static final SafeParcelableCreatorAndWriter<MostRecentGameInfo> CREATOR = findCreator(MostRecentGameInfo.class);
    @Field(1)
    public String gameId;
    @Field(2)
    public String gameName;
    @Field(3)
    public long activityTimestampMillis;
    @Field(4)
    public Uri gameIconUri;
    @Field(5)
    public Uri gameHiResUri;
    @Field(6)
    public Uri gameFeatureUri;

    public MostRecentGameInfo() {}
    public MostRecentGameInfo(FirstPartPlayer.DisplayPlayer.LastPlayedApp lastPlayedApp) {
        gameId = lastPlayedApp.applicationId;
        gameName = lastPlayedApp.applicationName;
        activityTimestampMillis = Long.parseLong(lastPlayedApp.timeMillis);
        gameIconUri = Uri.parse(lastPlayedApp.applicationIconUrl);
        gameHiResUri = Uri.parse(lastPlayedApp.applicationIconUrl);
        gameFeatureUri = Uri.parse(lastPlayedApp.featuredImageUrl);
    }

    public static String toJson(MostRecentGameInfo gameInfo) {
        JSONObject jsonObject = new JSONObject();
        if (gameInfo == null) return jsonObject.toString();
        try {
            jsonObject.put("gameId", gameInfo.gameId);
            jsonObject.put("gameName", gameInfo.gameName);
            jsonObject.put("activityTimestampMillis", gameInfo.activityTimestampMillis);
            jsonObject.put("gameIconUri", gameInfo.gameIconUri.toString());
            jsonObject.put("gameHiResUri", gameInfo.gameHiResUri.toString());
            jsonObject.put("gameFeatureUri", gameInfo.gameFeatureUri.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static MostRecentGameInfo fromJson(String json) {
        MostRecentGameInfo gameInfo = new MostRecentGameInfo();
        try {
            JSONObject jsonObject = new JSONObject(json);
            gameInfo.gameId = jsonObject.getString("gameId");
            gameInfo.gameName = jsonObject.getString("gameName");
            gameInfo.activityTimestampMillis = jsonObject.getLong("activityTimestampMillis");
            gameInfo.gameIconUri = Uri.parse(jsonObject.getString("gameIconUri"));
            gameInfo.gameHiResUri = Uri.parse(jsonObject.getString("gameHiResUri"));
            gameInfo.gameFeatureUri = Uri.parse(jsonObject.getString("gameFeatureUri"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameInfo;
    }

    public Uri getGameIconImageUri() {
        return gameIconUri;
    }

    public Uri getGameHiResImageUri() {
        return gameHiResUri;
    }

    public Uri getGameFeaturedImageUri() {
        return gameFeatureUri;
    }
}
