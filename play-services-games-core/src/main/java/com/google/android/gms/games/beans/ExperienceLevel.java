package com.google.android.gms.games.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class ExperienceLevel {
    public String kind;
    public Integer level;
    public String minExperiencePoints;
    public String maxExperiencePoints;

    public static String toJson(ExperienceLevel experienceLevel) {
        JSONObject jsonObject = new JSONObject();
        if (experienceLevel == null) return jsonObject.toString();
        try {
            jsonObject.put("kind", experienceLevel.kind);
            jsonObject.put("level", experienceLevel.level);
            jsonObject.put("minExperiencePoints", experienceLevel.minExperiencePoints);
            jsonObject.put("maxExperiencePoints", experienceLevel.maxExperiencePoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static ExperienceLevel fromJson(String jsonString) {
        ExperienceLevel nextLevel = new ExperienceLevel();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            nextLevel.kind = jsonObject.getString("kind");
            nextLevel.level = jsonObject.getInt("level");
            nextLevel.minExperiencePoints = jsonObject.getString("minExperiencePoints");
            nextLevel.maxExperiencePoints = jsonObject.getString("maxExperiencePoints");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nextLevel;
    }
}
