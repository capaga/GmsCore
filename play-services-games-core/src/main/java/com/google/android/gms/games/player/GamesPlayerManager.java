package com.google.android.gms.games.player;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.games.beans.FirstPartPlayer;
import com.google.android.gms.games.beans.GamesPlayer;

import java.util.HashMap;
import java.util.Map;

public class GamesPlayerManager {
    private static volatile GamesPlayerManager instance = null;
    private static final String PREF_NAME = "game_player_list";
    private final SharedPreferences sharedPreferences;
    private final Map<Account, PlayerEntity> players = new HashMap<>();

    private GamesPlayerManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadPlayers();
    }

    public static GamesPlayerManager getInstance(Context context) {
        if (instance == null) {
            synchronized (GamesPlayerManager.class) {
                if (instance == null) {
                    instance = new GamesPlayerManager(context);
                }
            }
        }
        return instance;
    }

    private void savePlayers() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        players.forEach(((account, playerEntity) -> {
            editor.putString(account.name + ":" + account.type, PlayerEntity.toJson(playerEntity));
        }));
        editor.apply();
    }

    private void loadPlayers() {
        Map<String, String> players = (Map<String, String>) sharedPreferences.getAll();
        players.forEach((key, value) -> {
            String[] split = key.split(":");
            Account account = new Account(split[0], split[1]);
            this.players.put(account, PlayerEntity.fromJson(value));
        });
    }

    public synchronized void putPlayer(Account account, FirstPartPlayer firstPartPlayer) {
        if (account == null) {
            return;
        }
        players.put(account, new PlayerEntity(firstPartPlayer));
        savePlayers();
    }

    public synchronized void updatePlayer(Account account, GamesPlayer gamesPlayer) {
        if (account == null)
            return;
        PlayerEntity player = players.get(account);
        if (player == null)
            return;
        player.update(gamesPlayer);
        savePlayers();
    }

    public synchronized PlayerEntity getPlayer(Account account) {
        if (account == null)
            return null;
        return players.get(account);
    }
}
