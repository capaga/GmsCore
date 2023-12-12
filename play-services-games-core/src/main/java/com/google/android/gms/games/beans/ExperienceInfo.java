package com.google.android.gms.games.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class ExperienceInfo {
    public String kind;
    public String currentExperiencePoints;
    public ExperienceLevel currentLevel;
    public ExperienceLevel nextLevel;
    public String totalUnlockedAchievements;

    public static String toJson(ExperienceInfo experienceInfo) {
        JSONObject jsonObject = new JSONObject();
        if (experienceInfo == null) return jsonObject.toString();
        try {
            jsonObject.put("kind", experienceInfo.kind);
            jsonObject.put("currentExperiencePoints", experienceInfo.currentExperiencePoints);
            jsonObject.put("currentLevel", ExperienceLevel.toJson(experienceInfo.currentLevel));
            jsonObject.put("nextLevel", ExperienceLevel.toJson(experienceInfo.nextLevel));
            jsonObject.put("totalUnlockedAchievements", experienceInfo.totalUnlockedAchievements);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static ExperienceInfo fromJson(String jsonString) {
        ExperienceInfo experienceInfo = new ExperienceInfo();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            experienceInfo.kind = jsonObject.getString("kind");
            experienceInfo.currentExperiencePoints = jsonObject.getString("currentExperiencePoints");
            experienceInfo.currentLevel = ExperienceLevel.fromJson(jsonObject.getString("currentLevel"));
            experienceInfo.nextLevel = ExperienceLevel.fromJson(jsonObject.getString("nextLevel"));
            experienceInfo.totalUnlockedAchievements = jsonObject.getString("totalUnlockedAchievements");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return experienceInfo;
    }
}
