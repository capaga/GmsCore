package com.google.android.gms.games.player;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelableCreatorAndWriter;

import org.json.JSONException;
import org.json.JSONObject;

@SafeParcelable.Class
public class Level extends AbstractSafeParcelable {
    @Field(value = 1, getterName = "getLevelNumber")
    private int levelNumber;
    @Field(value = 2, getterName = "getMinXp")
    private long minXp;
    @Field(value = 3, getterName = "getMaxXp")
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

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        CREATOR.writeToParcel(this, dest, flags);
    }
    public static final SafeParcelableCreatorAndWriter<Level> CREATOR = findCreator(Level.class);
}