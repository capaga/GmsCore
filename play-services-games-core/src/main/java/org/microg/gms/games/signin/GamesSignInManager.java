package org.microg.gms.games.signin;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.games.beans.FirstPartPlayer;
import com.google.android.gms.games.beans.GamesPlayer;
import com.google.android.gms.games.player.GamesConfigManager;
import com.google.android.gms.games.player.GamesPlayerManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.microg.gms.auth.AuthConstants;
import org.microg.gms.auth.AuthResponse;
import org.microg.gms.auth.AuthServiceManager;
import org.microg.gms.checkin.LastCheckinInfo;
import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.Constants;
import org.microg.gms.common.Utils;
import org.microg.gms.games.signin.utils.MD5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GamesSignInManager {
    private static final String TAG = GamesSignInManager.class.getSimpleName();
    public static final String KEY_RESULT_DATA = "resultData";
    private final Context context;
    private AuthToken firstPartToken;
    private AuthToken oauth2Token;
    private Account account;
    private String packageName;
    private GoogleSignInOptions googleSignInOptions;

    private class AuthToken {
        public String token;
        public long expiry;

        public AuthToken(String token, long expiry) {
            this.token = token;
            this.expiry = expiry;
        }
    }

    private enum GetOAuth2Result {
        RESULT_SUCCESS,
        RESULT_FAILED,
        RESULT_NEED_CREATE_PROFILE
    }

    public GamesSignInManager(Context context, Account account, String packageName, GoogleSignInOptions googleSignInOptions) {
        this.context = context;
        this.account = account;
        this.packageName = packageName;
        this.googleSignInOptions = googleSignInOptions;
    }

    public boolean isGamesSignIn() {
        return googleSignInOptions != null && googleSignInOptions.getScopes()
                .contains(new Scope(Scopes.GAMES_LITE));
    }

    private void startPlay() {
        String url = "https://www.googleapis.com/games/v1/applications/played";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("User-Agent", "Mozilla 5.0 (Linux; U; Android 13; zh_CN_#Hans; V2231A; Build/TQ2A.230505.002); com.google.android.gms/220221045; FastParser/1.1; Games Android SDK/1.0-4368; com.google.android.play.games/0; (gzip); Games module/220221000");
        headers.put("X-Device-ID", getAndroidId());
        headers.put("Authorization", "OAuth " + oauth2Token.token);
        try {
            requestHttp("POST", url, headers, null, null);
        } catch (IOException e) {
            Log.w(TAG, "startPlay", e);
        }
    }

    private Map putProfileSetting(Map randomGamerTag) {
        String url = "https://www.googleapis.com/games/v1whitelisted/players/me/profilesettings?language=" + Utils.getLocale(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("User-Agent", "Mozilla 5.0 (Linux; U; Android 13; zh_CN_#Hans; V2231A; Build/TQ2A.230505.002); com.google.android.gms/220221045; FastParser/1.1; Games Android SDK/1.0-4368; com.google.android.play.games/0; (gzip); Games module/220221000");
        headers.put("X-Device-ID", getAndroidId());
        headers.put("Authorization", "OAuth " + firstPartToken.token);
        Map<String, Object> requestMap = new HashMap();
        requestMap.put("alwaysAutoSignIn", true);
        requestMap.put("autoSignIn", true);
        requestMap.put("gamerTagIsDefault", true);
        requestMap.put("gamerTagIsExplicitlySet", true);
        requestMap.put("gamesLitePlayerStatsEnabled", true);
        requestMap.put("profileDiscoverableViaGoogleAccount", false);
        requestMap.put("profileVisibilityWasChosenByPlayer", true);
        requestMap.put("profileVisible", true);
        if (randomGamerTag.containsKey("gamerTag")) {
            requestMap.put("gamerTag", randomGamerTag.get("gamerTag"));
        }
        if (randomGamerTag.containsKey("stockGamerAvatarUrl")) {
            requestMap.put("stockGamerAvatarUrl", randomGamerTag.get("stockGamerAvatarUrl"));
        }
        try {
            Map<String, List<String>> responseHeaders = new HashMap<>();
            String requestMapString = mapToJsonString(requestMap);
            
            byte[] response = requestHttp("PUT", url, headers, requestMapString.getBytes("UTF-8"), responseHeaders);
            List<String> XPlayGamesToken = responseHeaders.get("X-Play-Games-Token");
            if (XPlayGamesToken != null && XPlayGamesToken.size() > 0) {
                GamesConfigManager.getInstance(context).saveXPlayGamesToken(XPlayGamesToken.get(0));
            }
            if (response.length == 0) {
                Log.w(TAG, "putProfileSetting failed, response is empty!");
                return null;
            }
            String jsonString = new String(response, StandardCharsets.UTF_8);
            Map<String, Object> map = new HashMap<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObject.get(key);
                    map.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return map;
        } catch (IOException e) {
            Log.w(TAG, "putProfileSetting", e);
        }
        return null;
    }

    private String mapToJsonString(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private boolean createProfile() {
        if (firstPartToken == null) {
            if (!getFirstPartyToken()) {
                return false;
            }
        }

        Map randomGamerTag = requestRandomGamerTag();
        if (randomGamerTag == null || !randomGamerTag.containsKey("gamerTag")) {
            return false;
        }

        Map result = putProfileSetting(randomGamerTag);
        if (result == null) {
            return false;
        }
        return true;
    }

    private GetOAuth2Result getOauth2Token() {
        String service = "oauth2:https://www.googleapis.com/auth/games_lite";
        try {
            AuthResponse response = AuthServiceManager.Companion.getInstance()
                    .getGameOauth2Token(context, service, account, packageName, true);
            if (response.auth != null) {
                Log.d(TAG, "getOauth2Token success result=" + response.auth);
                oauth2Token = new AuthToken(response.auth, response.expiry);
                return GetOAuth2Result.RESULT_SUCCESS;
            } else if (response.resolutionDataBase64 != null) {
                Log.w(TAG, "getOauth2Token first signIn need create profile");
                return GetOAuth2Result.RESULT_NEED_CREATE_PROFILE;
            } else {
                Log.w(TAG, "getOauth2Token unknown Error: " + response);
            }
        } catch (Exception e) {
            Log.w(TAG, "getOauth2Token", e);
        }
        return GetOAuth2Result.RESULT_FAILED;
    }

    private boolean getFirstPartyToken() {
        String service = "oauth2:https://www.googleapis.com/auth/games.firstparty";
        AuthResponse response = AuthServiceManager.Companion.getInstance()
                .getGameFirstPartyToken(context, service, account, Constants.GMS_PACKAGE_NAME, true);
        if (response.auth == null) {
            Log.w(TAG, "getFirstPartyToken failed");
        } else {
            Log.d(TAG, "getFirstPartyToken success result=" + response.auth);
            firstPartToken = new AuthToken(response.auth, response.expiry);
            return true;
        }
        return false;
    }

    private String getAndroidId() {
        return Long.toHexString(LastCheckinInfo.read(context).getAndroidId());
    }

    private Map requestRandomGamerTag() {
        String url = String.format("https://www.googleapis.com/games/v1whitelisted/players/me/profilesettings?language=%s&requestRandomGamerTag=true", Utils.getLocale(context));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla 5.0 (Linux; U; Android 13; zh_CN_#Hans; V2231A; Build/TQ2A.230505.002); com.google.android.gms/220221045; FastParser/1.1; Games Android SDK/1.0-4368; com.google.android.play.games/0; (gzip); Games module/220221000");
        headers.put("X-Device-ID", getAndroidId());
        headers.put("Authorization", "OAuth " + firstPartToken.token);
        try {
            byte[] response = requestHttp("GET", url, headers, null, null);
            if (response.length <= 0) {
                Log.e(TAG, "requestRandomGamerTag failed, response is empty!");
                return null;
            }
            String jsonString = new String(response, StandardCharsets.UTF_8);
            Map<String, Object> map = new HashMap<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObject.get(key);
                    map.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return map;
        } catch (IOException e) {
            Log.e(TAG, "requestRandomGamerTag", e);
        }
        return null;
    }

    private GamesPlayer getCurrentPlayer() {
        String url = "https://www.googleapis.com/games/v1/players/me?language=" + Utils.getLocale(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla 5.0 (Linux; U; Android 13; zh_CN_#Hans; V2231A; Build/TQ2A.230505.002); com.google.android.gms/220221045; FastParser/1.1; Games Android SDK/1.0-4368; com.google.android.play.games/0; (gzip); Games module/220221000");
        headers.put("X-Device-ID", getAndroidId());
        headers.put("Authorization", "OAuth " + oauth2Token.token);
        headers.put("X-Play-Games-Token", "1686534061463576");
        try {
            byte[] response = requestHttp("GET", url, headers, null, null);
            if (response.length == 0) {
                Log.e(TAG, "getCurrentPlayer failed, response is empty!");
                return null;
            }
            return GamesPlayer.fromJson(new String(response, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e(TAG, "getCurrentPlayer", e);
        }
        return null;
    }

    private FirstPartPlayer getFirstPartPlayer() {
        if (firstPartToken == null) {
            if (!getFirstPartyToken()) {
                return null;
            }
        }
        String url = "https://www.googleapis.com/games/v1whitelisted/players/me?language=" + Utils.getLocale(context);
        Map headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla 5.0 (Linux; U; Android 13; zh_CN_#Hans; V2231A; Build/TQ2A.230505.002); com.google.android.gms/220221045; FastParser/1.1; Games Android SDK/1.0-4368; com.google.android.play.games/0; (gzip); Games module/220221000");
        headers.put("X-Device-ID", getAndroidId());
        headers.put("Authorization", "OAuth " + firstPartToken.token);
        try {
            byte[] response = requestHttp("GET", url, headers, null, null);
            if (response.length == 0) {
                Log.e(TAG, "getFirstPartPlayer failed, response is empty!");
                return null;
            }
            return FirstPartPlayer.fromJson(new String(response, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e(TAG, "getFirstPartPlayer", e);
        }
        return null;
    }

    private AuthToken requestServerAuthCode() {
        String service = String.format("oauth2:server:client_id:%s:api_scope:https://www.googleapis.com/auth/games_lite", googleSignInOptions.getServerClientId());
        try {
            AuthResponse response = AuthServiceManager.Companion.getInstance()
                    .getGameServerAuthToken(context, service, account, packageName, true);
            return new AuthToken(response.auth, response.expiry);
        } catch (Exception e) {
            Log.e(TAG, "requestServerAuthCode", e);
        }
        return null;
    }

    public void gamesSignIn(AuthSdkCallBack callBack) {
        Log.d(TAG, String.format("gamesSignIn(account=%s, googleSignInOptions=%s)", account, googleSignInOptions));
        if (!isGamesSignIn()) {
            Log.e(TAG, "Not Game SignIn");
            callBack.onError("Not Game SignIn");
            return;
        }
        Map<String, String> tokens = new HashMap<>(2);

        GetOAuth2Result result = getOauth2Token();
        if (result == GetOAuth2Result.RESULT_NEED_CREATE_PROFILE) {

            if (!createProfile()) {
                Log.e(TAG, "createProfile failed");
                callBack.onError("createProfile failed");
                return;
            }
            result = getOauth2Token();
            if (result != GetOAuth2Result.RESULT_SUCCESS) {
                Log.e(TAG, "second get oauth2 token failed");
                callBack.onError("second get oauth2 token failed");
                return;
            }
        } else if (result == GetOAuth2Result.RESULT_FAILED) {
            Log.e(TAG, "first get oauth2 token failed");
            callBack.onError("first get oauth2 token failed");
            return;
        }
        if (GamesPlayerManager.getInstance(context).getPlayer(account) == null) { // 还没获取过玩家信息

            FirstPartPlayer firstPartPlayer = getFirstPartPlayer();
            if (firstPartPlayer == null) {
                Log.e(TAG, "getFirstPartPlayer failed");
                callBack.onError("getFirstPartPlayer failed");
                return;
            }
            GamesPlayerManager.getInstance(context).putPlayer(account, firstPartPlayer);
        }

        GamesPlayer gamesPlayer = getCurrentPlayer();
        if (gamesPlayer != null) {
            GamesPlayerManager.getInstance(context).updatePlayer(account, gamesPlayer);
        }
        startPlay();
        if (googleSignInOptions.isIdTokenRequested()) {
            tokens.put(Constants.AUDIENCE_TOKEN, oauth2Token.token);
            tokens.put(Constants.EXP_TIME, String.valueOf(oauth2Token.expiry));
        }
        if (googleSignInOptions.isServerAuthCodeRequested()) {
            AuthToken serverAuthCode = requestServerAuthCode();
            if (serverAuthCode != null) {
                tokens.put(Constants.OAUTH2_TOKEN, serverAuthCode.token);
                tokens.put(Constants.EXP_TIME, String.valueOf(serverAuthCode.expiry));
            }
        }
        try {
            callBack.success(1, tokens);
        } catch (Exception e) {
            Log.e(TAG, "gamesSignIn", e);
            callBack.onError(e.toString());
        }
    }

    static public Account checkAccount(Context context, String pkgName, Account account) {
        Account newAccount = account;
        if (account == null || AuthConstants.DEFAULT_ACCOUNT.equals(account.name)) {
            newAccount = AccountManagerUtils.getInstance(context).getDefaultAccount(pkgName);
        }
        Log.d(TAG, "checkAccount: " + newAccount);
        return newAccount;
    }

    static public void silentSignIn(Context context, Account account, SignInConfiguration signInConfiguration, ResultReceiver resultReceiver) {
        account = checkAccount(context, signInConfiguration.packageName, account);
        if (account == null) {
            Log.e(TAG, "silentSignIn account is null");
            resultReceiver.send(0, Bundle.EMPTY);
            return;
        }
        final Account newAccount = account;
        new Thread(() -> {
            GamesSignInManager gamesSignInManager = new GamesSignInManager(context, newAccount, signInConfiguration.packageName, signInConfiguration.options);
            gamesSignInManager.gamesSignIn(new AuthSdkCallBack() {
                @Override
                public void success(int type, Map<String, String> tokens) {
                    String firstName = AccountManagerUtils.getInstance(context).getUserData(newAccount, AccountManagerUtils.FIRST_NAME);
                    String lastName = AccountManagerUtils.getInstance(context).getUserData(newAccount, AccountManagerUtils.LAST_NAME);
                    String GoogleUserId = AccountManagerUtils.getInstance(context).getUserData(newAccount, AccountManagerUtils.GOOGLE_USER_ID);
                    String name = lastName + firstName;
                    String email = newAccount.name;
                    String encodeId = MD5.dest(GoogleUserId, signInConfiguration.packageName);
                    String AudienceToken = tokens.get(Constants.AUDIENCE_TOKEN);
                    String oauth2token = tokens.get(Constants.OAUTH2_TOKEN);
                    String expTime = tokens.get(Constants.EXP_TIME);
                    GoogleSignInAccount googleSignInAccount = new GoogleSignInAccount(GoogleUserId, AudienceToken, email, name,
                            null, oauth2token, Long.parseLong(expTime), encodeId, new ArrayList(signInConfiguration.options.getScopes()), firstName, lastName);
                    SignInAccount signInAccount = new SignInAccount(AuthConstants.DEFAULT_ACCOUNT, googleSignInAccount, AuthConstants.DEFAULT_USER_ID);
                    Bundle result = new Bundle();
                    result.putParcelable(KEY_RESULT_DATA, signInAccount);
                    resultReceiver.send(1, result);
                }

                @Override
                public void onError(String msg) {
                    Log.e(TAG, "silentSignIn error: " + msg);
                    resultReceiver.send(0, Bundle.EMPTY);
                }
            });
        }).start();
    }

    public interface AuthSdkCallBack {

        void success(int type, Map<String, String> tokens) throws RemoteException;

        void onError(String msg);

    }


    private static byte[] requestHttp(String method, String url, Map<String, String> headers
            , byte[] content, Map<String, List<String>> responseHeaders) throws IOException {
        URL objUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) objUrl.openConnection();

        conn.setConnectTimeout(7000);
        conn.setReadTimeout(7000);

        conn.setRequestMethod(method);

        for (String header : headers.keySet()) {
            conn.setRequestProperty(header, headers.get(header));
        }
        if (content != null) {
            conn.setDoOutput(true);
            conn.getOutputStream().write(content);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
        }
        InputStream inputStream = conn.getInputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            if (responseHeaders != null) {
                responseHeaders.putAll(conn.getHeaderFields());
            }
        }
        inputStream.close();
        return outputStream.toByteArray();
    }
}
