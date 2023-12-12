package com.google.android.gms.games.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstPartPlayer {
    public String kind;
    public DisplayPlayer displayPlayer;
    public String lastPlayedApplicationId;
    public String lastPlayedApplicationIconUrl;
    public String lastPlayedTimeMillis;
    public LastPlayed lastPlayed;
    public String profileCreationTimestampMillis;
    public String playerIdConsistencyToken;

    public static String toJson(FirstPartPlayer firstPartPlayer) {
        JSONObject jsonObject = new JSONObject();
        if (firstPartPlayer == null) return jsonObject.toString();
        try {
            jsonObject.put("kind", firstPartPlayer.kind);
            jsonObject.put("displayPlayer", DisplayPlayer.toJson(firstPartPlayer.displayPlayer));
            jsonObject.put("lastPlayedApplicationId", firstPartPlayer.lastPlayedApplicationId);
            jsonObject.put("lastPlayedApplicationIconUrl", firstPartPlayer.lastPlayedApplicationIconUrl);
            jsonObject.put("lastPlayedTimeMillis", firstPartPlayer.lastPlayedTimeMillis);
            jsonObject.put("lastPlayed", LastPlayed.toJson(firstPartPlayer.lastPlayed));
            jsonObject.put("profileCreationTimestampMillis", firstPartPlayer.profileCreationTimestampMillis);
            jsonObject.put("playerIdConsistencyToken", firstPartPlayer.playerIdConsistencyToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static FirstPartPlayer fromJson(String json) {
        FirstPartPlayer firstPartPlayer = new FirstPartPlayer();
        try {
            JSONObject jsonObject = new JSONObject(json);
            firstPartPlayer.kind = jsonObject.optString("kind");
            firstPartPlayer.displayPlayer = DisplayPlayer.fromJson(jsonObject.optString("displayPlayer"));
            firstPartPlayer.lastPlayedApplicationId = jsonObject.optString("lastPlayedApplicationId");
            firstPartPlayer.lastPlayedApplicationIconUrl = jsonObject.optString("lastPlayedApplicationIconUrl");
            firstPartPlayer.lastPlayedTimeMillis = jsonObject.optString("lastPlayedTimeMillis");
            firstPartPlayer.lastPlayed = LastPlayed.fromJson(jsonObject.optString("lastPlayed"));
            firstPartPlayer.profileCreationTimestampMillis = jsonObject.optString("profileCreationTimestampMillis");
            firstPartPlayer.playerIdConsistencyToken = jsonObject.optString("playerIdConsistencyToken");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstPartPlayer;
    }

    public static class DisplayPlayer {
        public String kind;
        public String playerId;
        public String displayName;
        public String avatarImageUrl;
        public String bannerUrlPortrait;
        public String bannerUrlLandscape;
        public String gamerTag;
        public LastPlayedApp lastPlayedApp;
        public ProfileSettings profileSettings;
        public Name name;
        public ExperienceInfo experienceInfo;
        public String title;

        public static String toJson(DisplayPlayer displayPlayer) {
            JSONObject jsonObject = new JSONObject();
            if (displayPlayer == null) return jsonObject.toString();
            try {
                jsonObject.put("kind", displayPlayer.kind);
                jsonObject.put("playerId", displayPlayer.playerId);
                jsonObject.put("displayName", displayPlayer.displayName);
                jsonObject.put("avatarImageUrl", displayPlayer.avatarImageUrl);
                jsonObject.put("bannerUrlPortrait", displayPlayer.bannerUrlPortrait);
                jsonObject.put("bannerUrlLandscape", displayPlayer.bannerUrlLandscape);
                jsonObject.put("gamerTag", displayPlayer.gamerTag);
                jsonObject.put("lastPlayedApp", LastPlayedApp.toJson(displayPlayer.lastPlayedApp));
                jsonObject.put("profileSettings", ProfileSettings.toJson(displayPlayer.profileSettings));
                jsonObject.put("name", Name.toJson(displayPlayer.name));
                jsonObject.put("experienceInfo", ExperienceInfo.toJson(displayPlayer.experienceInfo));
                jsonObject.put("title", displayPlayer.title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }

        public static DisplayPlayer fromJson(String json) {
            DisplayPlayer displayPlayer = new DisplayPlayer();
            try {
                JSONObject jsonObject = new JSONObject(json);
                displayPlayer.kind = jsonObject.optString("kind");
                displayPlayer.playerId = jsonObject.optString("playerId");
                displayPlayer.displayName = jsonObject.optString("displayName");
                displayPlayer.avatarImageUrl = jsonObject.optString("avatarImageUrl");
                displayPlayer.bannerUrlPortrait = jsonObject.optString("bannerUrlPortrait");
                displayPlayer.bannerUrlLandscape = jsonObject.optString("bannerUrlLandscape");
                displayPlayer.gamerTag = jsonObject.optString("gamerTag");
                displayPlayer.lastPlayedApp = LastPlayedApp.fromJson(jsonObject.optString("lastPlayedApp"));
                displayPlayer.profileSettings = ProfileSettings.fromJson(jsonObject.optString("profileSettings"));
                displayPlayer.name = Name.fromJson(jsonObject.optString("name"));
                displayPlayer.experienceInfo = ExperienceInfo.fromJson(jsonObject.optString("experienceInfo"));
                displayPlayer.title = jsonObject.optString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return displayPlayer;
        }


        public static class LastPlayedApp {
            public String kind;
            public String applicationId;
            public String applicationIconUrl;
            public String featuredImageUrl;
            public String applicationName;
            public String timeMillis;

            public static String toJson(LastPlayedApp lastPlayedApp) {
                JSONObject jsonObject = new JSONObject();
                if (lastPlayedApp == null) return jsonObject.toString();
                try {
                    jsonObject.put("kind", lastPlayedApp.kind);
                    jsonObject.put("applicationId", lastPlayedApp.applicationId);
                    jsonObject.put("applicationIconUrl", lastPlayedApp.applicationIconUrl);
                    jsonObject.put("featuredImageUrl", lastPlayedApp.featuredImageUrl);
                    jsonObject.put("applicationName", lastPlayedApp.applicationName);
                    jsonObject.put("timeMillis", lastPlayedApp.timeMillis);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString();
            }

            public static LastPlayedApp fromJson(String json) {
                LastPlayedApp lastPlayedApp = new LastPlayedApp();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    lastPlayedApp.kind = jsonObject.optString("kind");
                    lastPlayedApp.applicationId = jsonObject.optString("applicationId");
                    lastPlayedApp.applicationIconUrl = jsonObject.optString("applicationIconUrl");
                    lastPlayedApp.featuredImageUrl = jsonObject.optString("featuredImageUrl");
                    lastPlayedApp.applicationName = jsonObject.optString("applicationName");
                    lastPlayedApp.timeMillis = jsonObject.optString("timeMillis");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return lastPlayedApp;
            }

        }

        public static class Name {
            public String familyName;
            public String givenName;
            public String fullName;

            public static String toJson(Name name) {
                JSONObject jsonObject = new JSONObject();
                if (name == null) return jsonObject.toString();
                try {
                    jsonObject.put("familyName", name.familyName);
                    jsonObject.put("givenName", name.givenName);
                    jsonObject.put("fullName", name.fullName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString();
            }

            public static Name fromJson(String json) {
                Name name = new Name();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    name.familyName = jsonObject.optString("familyName");
                    name.givenName = jsonObject.optString("givenName");
                    name.fullName = jsonObject.optString("fullName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return name;
            }
        }
    }

    public static class LastPlayed {
        public String kind;
        public String applicationId;
        public String applicationIconUrl;
        public String timeMillis;

        public static String toJson(LastPlayed lastPlayed) {
            JSONObject jsonObject = new JSONObject();
            if (lastPlayed == null) return jsonObject.toString();
            try {
                jsonObject.put("kind", lastPlayed.kind);
                jsonObject.put("applicationId", lastPlayed.applicationId);
                jsonObject.put("applicationIconUrl", lastPlayed.applicationIconUrl);
                jsonObject.put("timeMillis", lastPlayed.timeMillis);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }

        public static LastPlayed fromJson(String json) {
            LastPlayed lastPlayed = new LastPlayed();
            try {
                JSONObject jsonObject = new JSONObject(json);
                lastPlayed.kind = jsonObject.optString("kind");
                lastPlayed.applicationId = jsonObject.optString("applicationId");
                lastPlayed.applicationIconUrl = jsonObject.optString("applicationIconUrl");
                lastPlayed.timeMillis = jsonObject.optString("timeMillis");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lastPlayed;
        }

    }
}
