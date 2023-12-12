package com.google.android.gms.games.player;


import com.google.android.gms.games.beans.ExperienceInfo;
import com.google.android.gms.games.beans.FirstPartPlayer;
import com.google.android.gms.games.beans.GamesPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class LevelInfo {
    private long currentXpTotal;
    private long lastLevelUpTimestamp;
    private Level currentLevel;
    private Level nextLevel;

    public LevelInfo() {}

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



    public static class Level {
        private int levelNumber;
        private long minXp;
        private long maxXp;

        public Level() {}

        public Level(int levelNumber, long minXp, long maxXp) {
            this.levelNumber = levelNumber;
            this.minXp = minXp;
            this.maxXp = maxXp;
        }

        public int getLevelNumber() {
            return levelNumber;
        }

        public void setLevelNumber(int levelNumber) {
            this.levelNumber = levelNumber;
        }

        public long getMinXp() {
            return minXp;
        }

        public void setMinXp(long minXp) {
            this.minXp = minXp;
        }

        public long getMaxXp() {
            return maxXp;
        }

        public void setMaxXp(long maxXp) {
            this.maxXp = maxXp;
        }

        public static String toJson(Level level) {
            JSONObject jsonObject = new JSONObject();
            if (level == null) return jsonObject.toString();
            try {
                jsonObject.put("levelNumber", level.getLevelNumber());
                jsonObject.put("minXp", level.getMinXp());
                jsonObject.put("maxXp", level.getMaxXp());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }

        public static Level fromJson(String json) {
            Level level = new Level();
            try {
                JSONObject jsonObject = new JSONObject(json);
                level.setLevelNumber(jsonObject.getInt("levelNumber"));
                level.setMinXp(jsonObject.getLong("minXp"));
                level.setMaxXp(jsonObject.getLong("maxXp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return level;
        }

    }
}
