package com.google.android.gms.games.beans;


import org.json.JSONException;
import org.json.JSONObject;

public class GamesPlayer {
    public String kind;
    public String playerId;
    public String displayName;
    public String avatarImageUrl;
    public String bannerUrlPortrait;
    public String bannerUrlLandscape;
    public ProfileSettings profileSettings;
    public ExperienceInfo experienceInfo;
    public String title;

    public static String toJson(GamesPlayer gamesPlayer) {
        JSONObject jsonObject = new JSONObject();
        if (gamesPlayer == null) return jsonObject.toString();
        try {
            jsonObject.put("kind", gamesPlayer.kind);
            jsonObject.put("playerId", gamesPlayer.playerId);
            jsonObject.put("displayName", gamesPlayer.displayName);
            jsonObject.put("avatarImageUrl", gamesPlayer.avatarImageUrl);
            jsonObject.put("bannerUrlPortrait", gamesPlayer.bannerUrlPortrait);
            jsonObject.put("bannerUrlLandscape", gamesPlayer.bannerUrlLandscape);
            jsonObject.put("profileSettings", ProfileSettings.toJson(gamesPlayer.profileSettings));
            jsonObject.put("experienceInfo", ExperienceInfo.toJson(gamesPlayer.experienceInfo));
            jsonObject.put("title", gamesPlayer.title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static GamesPlayer fromJson(String jsonString) {
        GamesPlayer gamesPlayer = new GamesPlayer();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            gamesPlayer.kind = jsonObject.getString("kind");
            gamesPlayer.playerId = jsonObject.getString("playerId");
            gamesPlayer.displayName = jsonObject.getString("displayName");
            gamesPlayer.avatarImageUrl = jsonObject.getString("avatarImageUrl");
            gamesPlayer.bannerUrlPortrait = jsonObject.getString("bannerUrlPortrait");
            gamesPlayer.bannerUrlLandscape = jsonObject.getString("bannerUrlLandscape");
            gamesPlayer.profileSettings = ProfileSettings.fromJson(jsonObject.getString("profileSettings"));
            gamesPlayer.experienceInfo = ExperienceInfo.fromJson(jsonObject.getString("experienceInfo"));
            gamesPlayer.title = jsonObject.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gamesPlayer;
    }
}
