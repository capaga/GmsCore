package com.google.android.gms.games.player;


import com.google.android.gms.common.internal.safeparcel.SafeParcelableCreatorAndWriter;
import com.google.android.gms.games.beans.ExperienceInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.microg.gms.common.Hide;
import org.microg.safeparcel.AutoSafeParcelable;

public class LevelInfo extends AutoSafeParcelable {
    public static final SafeParcelableCreatorAndWriter<LevelInfo> CREATOR = findCreator(LevelInfo.class);
    @Field(1)
    private long currentXpTotal;
    @Field(2)
    private long lastLevelUpTimestamp;
    @Field(3)
    private Level currentLevel;
    @Field(4)
    private Level nextLevel;

    public LevelInfo() {}

    @Hide
    public LevelInfo(long currentXpTotal, long lastLevelUpTimestamp, Level currentLevel, Level nextLevel) {
        this.currentXpTotal = currentXpTotal;
        this.lastLevelUpTimestamp = lastLevelUpTimestamp;
        this.currentLevel = currentLevel;
        this.nextLevel = nextLevel;
    }

    public LevelInfo(ExperienceInfo experienceInfo) {
        this.currentXpTotal = Long.parseLong(experienceInfo.currentExperiencePoints);
        this.lastLevelUpTimestamp = 0;
        this.currentLevel = new Level(experienceInfo.currentLevel.level, Long.parseLong(experienceInfo.currentLevel.minExperiencePoints),
                Long.parseLong(experienceInfo.currentLevel.maxExperiencePoints));
        this.nextLevel = new Level(experienceInfo.nextLevel.level, Long.parseLong(experienceInfo.nextLevel.minExperiencePoints),
                Long.parseLong(experienceInfo.nextLevel.maxExperiencePoints));
    }

    public long getCurrentXpTotal() {
        return currentXpTotal;
    }

    public void setCurrentXpTotal(long currentXpTotal) {
        this.currentXpTotal = currentXpTotal;
    }

    public long getLastLevelUpTimestamp() {
        return lastLevelUpTimestamp;
    }

    public void setLastLevelUpTimestamp(long lastLevelUpTimestamp) {
        this.lastLevelUpTimestamp = lastLevelUpTimestamp;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Level getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(Level nextLevel) {
        this.nextLevel = nextLevel;
    }

    public static String toJson(LevelInfo levelInfo) {
        JSONObject jsonObject = new JSONObject();
        if (levelInfo == null) return jsonObject.toString();
        try {
            jsonObject.put("currentXpTotal", levelInfo.getCurrentXpTotal());
            jsonObject.put("lastLevelUpTimestamp", levelInfo.getLastLevelUpTimestamp());
            jsonObject.put("currentLevel", Level.toJson(levelInfo.getCurrentLevel()));
            jsonObject.put("nextLevel", Level.toJson(levelInfo.getNextLevel()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static LevelInfo fromJson(String json) {
        LevelInfo levelInfo = new LevelInfo();
        try {
            JSONObject jsonObject = new JSONObject(json);
            levelInfo.setCurrentXpTotal(jsonObject.getLong("currentXpTotal"));
            levelInfo.setLastLevelUpTimestamp(jsonObject.getLong("lastLevelUpTimestamp"));
            levelInfo.setCurrentLevel(Level.fromJson(jsonObject.getJSONObject("currentLevel").toString()));
            levelInfo.setNextLevel(Level.fromJson(jsonObject.getJSONObject("nextLevel").toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return levelInfo;
    }




}
