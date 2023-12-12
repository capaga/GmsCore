package com.google.android.gms.games.player;

import android.net.Uri;

import com.google.android.gms.games.beans.FirstPartPlayer;

import org.json.JSONException;
import org.json.JSONObject;


public class MostRecentGameInfo {
    public String gameId;
    public String gameName;
    public long activityTimestampMillis;
    public Uri gameIconUri;
    public Uri gameHiResUri;
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

    
    
}
