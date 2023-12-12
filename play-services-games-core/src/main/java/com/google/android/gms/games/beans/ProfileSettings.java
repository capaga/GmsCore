package com.google.android.gms.games.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileSettings {
    public String kind;
    public Boolean profileVisible;
    public String friendsListVisibility;
    public Boolean profileVisibilityWasChosenByPlayer;
    public String gamerTag;
    public Boolean gamerTagIsDefault;
    public Boolean gamerTagIsExplicitlySet;
    public String stockGamerAvatarUrl;
    public Boolean gamesLitePlayerStatsEnabled;
    public Boolean allowFriendInvites;
    public String profileVisibilityV2;
    public Boolean alwaysAutoSignIn;

    public static String toJson(ProfileSettings profileSettings) {
        JSONObject jsonObject = new JSONObject();
        if (profileSettings == null) return jsonObject.toString();
        try {
            jsonObject.put("kind", profileSettings.kind);
            jsonObject.put("profileVisible", profileSettings.profileVisible);
            jsonObject.put("friendsListVisibility", profileSettings.friendsListVisibility);
            jsonObject.put("profileVisibilityWasChosenByPlayer", profileSettings.profileVisibilityWasChosenByPlayer);
            jsonObject.put("gamerTag", profileSettings.gamerTag);
            jsonObject.put("gamerTagIsDefault", profileSettings.gamerTagIsDefault);
            jsonObject.put("gamerTagIsExplicitlySet", profileSettings.gamerTagIsExplicitlySet);
            jsonObject.put("stockGamerAvatarUrl", profileSettings.stockGamerAvatarUrl);
            jsonObject.put("gamesLitePlayerStatsEnabled", profileSettings.gamesLitePlayerStatsEnabled);
            jsonObject.put("allowFriendInvites", profileSettings.allowFriendInvites);
            jsonObject.put("profileVisibilityV2", profileSettings.profileVisibilityV2);
            jsonObject.put("alwaysAutoSignIn", profileSettings.alwaysAutoSignIn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static ProfileSettings fromJson(String jsonString) {
        ProfileSettings profileSettings = new ProfileSettings();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            profileSettings.kind = jsonObject.getString("kind");
            profileSettings.profileVisible = jsonObject.getBoolean("profileVisible");
            profileSettings.friendsListVisibility = jsonObject.getString("friendsListVisibility");
            profileSettings.profileVisibilityWasChosenByPlayer = jsonObject.getBoolean("profileVisibilityWasChosenByPlayer");
            profileSettings.gamerTag = jsonObject.getString("gamerTag");
            profileSettings.gamerTagIsDefault = jsonObject.getBoolean("gamerTagIsDefault");
            profileSettings.gamerTagIsExplicitlySet = jsonObject.getBoolean("gamerTagIsExplicitlySet");
            profileSettings.stockGamerAvatarUrl = jsonObject.getString("stockGamerAvatarUrl");
            profileSettings.gamesLitePlayerStatsEnabled = jsonObject.getBoolean("gamesLitePlayerStatsEnabled");
            profileSettings.allowFriendInvites = jsonObject.getBoolean("allowFriendInvites");
            profileSettings.profileVisibilityV2 = jsonObject.getString("profileVisibilityV2");
            profileSettings.alwaysAutoSignIn = jsonObject.getBoolean("alwaysAutoSignIn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profileSettings;
    }

}
