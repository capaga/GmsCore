package com.google.android.gms.games.player;

import android.content.Context;
import android.content.SharedPreferences;

public class GamesConfigManager {
    private static final String PREF_NAME = "games_config";
    private static final String KEY_X_PLAY_GAMES_TOKEN = "x_play_games_token";
    private static volatile GamesConfigManager instance = null;
    private final SharedPreferences sharedPreferences;

    private GamesConfigManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static GamesConfigManager getInstance(Context context) {
        if (instance == null) {
            synchronized (GamesConfigManager.class) {
                if (instance == null) {
                    instance = new GamesConfigManager(context);
                }
            }
        }
        return instance;
    }

    public void saveXPlayGamesToken(String xPlayGamesToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (xPlayGamesToken != null) {
            editor.putString(KEY_X_PLAY_GAMES_TOKEN, xPlayGamesToken);
        }
        editor.apply();
    }
}
