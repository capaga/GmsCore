package org.microg.gms.games.signin;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.wire.ProtoAdapter;

import org.microg.common.beans.ConsentCookies;
import org.microg.common.beans.ConsentCookiesResponse;
import org.microg.common.beans.ConsentUrlResponse;
import org.microg.common.beans.Cookie;
import org.microg.common.beans.TokenRequestOptions;
import org.microg.gms.auth.AuthConstants;
import org.microg.gms.auth.AuthManager;
import org.microg.gms.auth.AuthRequest;
import org.microg.gms.auth.AuthResponse;
import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.Constants;
import org.microg.gms.games.signin.enums.ApiEnum;
import org.microg.gms.games.signin.utils.BytesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class AuthSdkManager {

    private static final String TAG = AuthSdkManager.class.getSimpleName();
    private static final String OAUTH2_PROMPT = "oauth2Prompt";
    public static final String OAUTH2_FG = "oauth2Foreground";
    public static final String OAUTH2_CONSENT_RESULT = "oauth2ConsentResult";
    public static final String IT_CAVEAT_TYPES = "it_caveat_types";
    public static final String CONSENT_RESULT = "org.microg.gms.auth.sign.CONSENT_RESULT";

    private final Context context;
    private final AccountManager accountManager;


    public AuthSdkManager(Context context) {
        this.context = context;
        accountManager = AccountManager.get(context);
    }

    public void getTokenWithAccountSDK(Account account, RequestParams requestParams, GamesSignInManager.AuthSdkCallBack callBack) {
        GamesSignInManager gamesSignInManager = new GamesSignInManager(context, account, requestParams.getSignInConfiguration());
        if (gamesSignInManager.isGamesSignIn()) {
            gamesSignInManager.gamesSignIn(callBack);
            return;
        }
        String saveServer = String.format("%s:%s?include_email=%d&include_profile=%d",
                "audience:server:client_id", requestParams.getClientId(), requestParams.getIncludeEmail(),
                requestParams.getIncludeProfile());
        String httpServer = String.format("%s:%s", "audience:server:client_id", requestParams.getClientId());
        String oauth2Server = String.format("%s:%s:api_scope:email openid profile",
                "oauth2:server:client_id", requestParams.getClientId());
        String oauth2SaveServer = String.format("%s:%s:api_scope:email openid profile?include_email=%d&include_profile=%d",
                "oauth2:server:client_id", requestParams.getClientId(), requestParams.getIncludeEmail(),
                requestParams.getIncludeProfile());

        try {
            String googleUserId = accountManager.getUserData(account, AccountManagerUtils.GOOGLE_USER_ID);
            // true:: 重置token
            boolean flage = false;
            String oauth2TokenStr = null;
            Map<String, String> tokens = new HashMap<>(2);
            if (!TextUtils.isEmpty(googleUserId)) {
                Log.d(TAG, "googleUserId: " + googleUserId);

                AuthManager saveAuthManager = new AuthManager(context, account.name, requestParams.getPackageName(), saveServer);
                AuthManager oauth2SaveAuthManager = new AuthManager(context, account.name, requestParams.getPackageName(), oauth2SaveServer);

                String peekAuthToken = accountManager.peekAuthToken(account, saveAuthManager.buildTokenKey());
                oauth2TokenStr = accountManager.peekAuthToken(account, oauth2SaveAuthManager.buildTokenKey());
                if (!TextUtils.isEmpty(peekAuthToken)) {
                    Log.d(TAG, "peekAuthToken: " + peekAuthToken + " TYPE::" + 0);
                    String userData = AccountManagerUtils.getInstance(context).getUserData(account
                            , saveAuthManager.buildExpireKey());
                    long expTime = Long.parseLong(userData);
                    long time = new Date().getTime();
                    if (time > expTime) {
                        flage = true;
                    } else {
                        tokens.put(Constants.AUDIENCE_TOKEN, peekAuthToken);
                        tokens.put(Constants.EXP_TIME, userData);
                    }
                } else {
                    flage = true;
                }
                if (requestParams.isOauthToPrompt()) {
                    if (!TextUtils.isEmpty(oauth2TokenStr)) {
                        Log.d(TAG, "Oauth2Token: " + oauth2TokenStr + " TYPE::" + 1);
                        String userData = AccountManagerUtils.getInstance(context).getUserData(account
                                , oauth2SaveAuthManager.buildExpireKey());
                        long expTime = Long.parseLong(userData);
                        long time = new Date().getTime();
                        if (time > expTime) {
                            flage = true;
                        } else {
                            tokens.put(Constants.OAUTH2_TOKEN, oauth2TokenStr);
                        }
                    } else {
                        flage = true;
                    }
                }
            } else {
                flage = true;
            }
            if (flage) {
                tokens.clear();
                httpPre(account, requestParams.getPackageName());
                Log.d(TAG, "Oauth2Token: " + oauth2TokenStr + " TYPE::" + 1);
                AuthResponse audienceToken = getAudienceToken(saveServer, httpServer, requestParams.getPackageName(), null, account);
                if (requestParams.isOauthToPrompt()) {
                    Map<String, Object> option = new HashMap<>();
                    if (requestParams.getSignInConfiguration().getOptions().isForceCodeForRefreshToken()) {
                        option.put(OAUTH2_PROMPT, "consent");
                        AuthResponse consentUrl = getConsentUrl(oauth2SaveServer, oauth2Server, requestParams.getPackageName(), option, account);
                        ProtoAdapter<ConsentUrlResponse> urlProtoAdapter = ConsentUrlResponse.ADAPTER;
                        ConsentUrlResponse signInConsentUrlResponse = urlProtoAdapter.decode(BytesUtils.base64ToBytes(consentUrl.resolutionDataBase64));
                        Log.d(TAG, "getTokenWithAccountSDK: consentUrl: " + signInConsentUrlResponse.consentUrl);


                        final String[] consentResult = {""};
                        CountDownLatch latch = new CountDownLatch(1);
                        IntentFilter filter = new IntentFilter(CONSENT_RESULT);
                        BroadcastReceiver receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                consentResult[0] = intent.getStringExtra("consentResult");
                                latch.countDown();
                            }
                        };
                        this.context.registerReceiver(receiver, filter);
                        ConsentSignInActivity.toConsentSignInActivity(context, signInConsentUrlResponse.consentUrl, getCookies(account, signInConsentUrlResponse));
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.context.unregisterReceiver(receiver);
                        Log.d(TAG, "getTokenWithAccountSDK: consentResult: " + consentResult[0]);
                        option.put("consent_result", consentResult[0]);
                    } else {
                        option.put(OAUTH2_PROMPT, "auto");
                    }
                    AuthResponse oauth2Token = getOauth2Token(oauth2SaveServer, oauth2Server, requestParams.getPackageName(), option, account);
                    if (audienceToken != null && oauth2Token != null) {
                        tokens.put(Constants.AUDIENCE_TOKEN, audienceToken.auth);
                        tokens.put(Constants.OAUTH2_TOKEN, oauth2Token.auth);
                        tokens.put(Constants.EXP_TIME, String.valueOf(audienceToken.expiry));
                    }
                } else {
                    if (audienceToken != null) {
                        tokens.put(Constants.AUDIENCE_TOKEN, audienceToken.auth);
                        tokens.put(Constants.EXP_TIME, String.valueOf(audienceToken.expiry));
                    }
                }

                callBack.success(1, tokens);
            } else {
                callBack.success(0, tokens);
            }


        } catch (IOException e) {
            Log.d(TAG, e.toString());
            callBack.onError("HTTP ERROR");
        } catch (RemoteException e) {
            Log.d(TAG, "getTokenWithAccountSDK: " + e);
        }

    }

    public AuthResponse getConsentUrl(String saveServer, String httpServer,
                                      String packageName, Map<String, Object> map,
                                      Account account) throws IOException {
        return requestAuth2(saveServer, httpServer, account, packageName,
                true, ApiEnum.EXPIRATION.value, true, true,
                false, "", true, map);
    }

    private void httpPre(Account account, String packageName) throws IOException {
        requestAuth2(AuthConstants.SCOPE_GET_SNOWBALL, AuthConstants.SCOPE_EM_OP_PRO, account, packageName, true, ApiEnum.EXPIRATION.value,
                false, false, true, "", true, null);
        requestAuth2(AuthConstants.SCOPE_GET_SNOWBALL, AuthConstants.SCOPE_EM_OP_PRO, account, packageName, true, ApiEnum.EXPIRATION.value,
                false, false, false, "", true, null);

    }

    public AuthResponse getAudienceToken(String saveServer, String httpServer,
                                         String packageName, Map<String, Object> map,
                                         Account account) throws IOException {
        return requestAuth2(saveServer, httpServer, account, packageName,
                true, -1, true, true,
                false, "", true, null);
    }

    public AuthResponse getOauth2Token(String saveServer, String httpServer,
                                       String packageName, Map<String, Object> map,
                                       Account account) throws IOException {
        return requestAuth2(saveServer, httpServer, account, packageName,
                true, ApiEnum.EXPIRATION.value, true, true,
                false, "", true, map);
    }

    public AuthResponse requestAuthWithExtras(String service, Account account, String packageName,
                                              boolean legacy, Map<String, Object> extras) throws IOException {
        if (AuthConstants.SCOPE_GET_SNOWBALL.equals(service)) {
            AuthResponse response = new AuthResponse();
            response.accountId = response.auth = accountManager.getUserData(account, AccountManagerUtils.GOOGLE_USER_ID);
            return response;
        }

        AuthManager authManager = new AuthManager(context, account.name, packageName, service);

        AuthRequest request = new AuthRequest().fromContext(context)
                .app(packageName, authManager.getPackageSignature())
                .email(account.name)
                .token(accountManager.getPassword(account))
                .operatorCountry(null)
                .service(service);
        if (legacy) {
            request.callerIsGms();
        } else {
            request.callerIsApp();
        }
        if (extras != null) {
            Integer itCaveatTypes = (Integer) extras.get(IT_CAVEAT_TYPES);
            if (itCaveatTypes != null) {
                if (itCaveatTypes == -1) {
                    request.itCaveatTypes("" + ApiEnum.EXPIRATION.value);
                } else {
                    request.itCaveatTypes("" + itCaveatTypes);
                }
            }
            String tokenRequestOptions = (String) extras.get("token_request_options");
            if (tokenRequestOptions != null) {
                if (TextUtils.isEmpty(tokenRequestOptions)) {
                    TokenRequestOptions requestOptions = new TokenRequestOptions()
                            .newBuilder().field_1(false).field_7(1).version(3)
                            .sessionId(BytesUtils.generateSessionId().trim()).build();
                    byte[] bytes = requestOptions.encode();
                    request.tokenRequestOptions(BytesUtils.bytesToBase64(bytes));
                } else {
                    request.tokenRequestOptions(tokenRequestOptions);
                }
            }
            Boolean oauth2IncludeProfile = (Boolean) extras.get("oauth2_include_profile");
            if (oauth2IncludeProfile != null) {
                request.putDynamicFiled("oauth2_include_profile", String.valueOf(oauth2IncludeProfile == true ? 1 : 0));
            }
            Boolean checkEmail = (Boolean) extras.get("check_email");
            if (checkEmail != null) {
                request.checkEmail(checkEmail);
            }
            String oauth2Prompt = (String) extras.get("oauth2_prompt");
            if (oauth2Prompt != null) {
                request.oauth2Prompt(oauth2Prompt);
            }
            String requestVisibleActions = (String) extras.get("request_visible_actions");
            if (requestVisibleActions != null) {
                request.requestVisibleActions(requestVisibleActions);
            }
            Boolean oauth2IncludeEmail = (Boolean) extras.get("oauth2_include_email");
            if (oauth2IncludeEmail != null) {
                request.putDynamicFiled("oauth2_include_email", String.valueOf(oauth2IncludeEmail == true ? 1 : 0));
            }
            Boolean oauth2Foreground = (Boolean) extras.get("oauth2_foreground");
            if (oauth2Foreground != null) {
                request.oauth2Foreground(oauth2Foreground ? "0" : "1");
            }
        }

        AuthResponse response = request.getResponse();
        authManager.storeResponse(response);
        return response;
    }

    public AuthResponse requestAuth2(String savaService, String service, Account account, String packageName,
                                     boolean checkEmail, int itCaveatTypes, boolean oauth2IncludeProfile,
                                     boolean oauth2IncludeEmail, boolean hasPermission, String requestVisibleActions,
                                     boolean legacy, Map<String, Object> map) throws IOException {
        if (service.equals(AuthConstants.SCOPE_GET_SNOWBALL)) {
            AuthResponse response = new AuthResponse();
            response.accountId = response.auth = accountManager.getUserData(account, AccountManagerUtils.GOOGLE_USER_ID);
            return response;
        }
        TokenRequestOptions requestOptions = new TokenRequestOptions()
                .newBuilder().field_1(false).field_7(1).version(3)
                .sessionId(BytesUtils.generateSessionId().trim()).build();
        byte[] bytes = requestOptions.encode();

        AuthManager authManager = new AuthManager(context, account.name, packageName, savaService);

        AuthRequest request = new AuthRequest().fromContext(context)
                .app(packageName, authManager.getPackageSignature())
                .email(account.name)
                .tokenRequestOptions(BytesUtils.bytesToBase64(bytes))
                .token(accountManager.getPassword(account))
                .operatorCountry(null)
                .service(service);
        if (authManager.isSystemApp()) request.systemPartition();
        if (hasPermission) {
            request.hasPermission();
        }
        if (checkEmail) {
            request.checkEmail(true);
        }
        if (itCaveatTypes != -1) {
            request.itCaveatTypes("" + ApiEnum.EXPIRATION.value);
        }
        if (oauth2IncludeProfile) {
            request.putDynamicFiled("oauth2_include_profile", "1");
        }
        if (oauth2IncludeEmail) {
            request.putDynamicFiled("oauth2_include_email", "1");
        }
        if (requestVisibleActions != null) {
            request.requestVisibleActions(requestVisibleActions);
        }
        if (map != null && map.size() > 0) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (OAUTH2_PROMPT.equals(next)) {
                    request.oauth2Prompt((String) map.get(next));
                }
                if (OAUTH2_FG.equals(next)) {
                    request.oauth2Foreground((boolean) map.get(next) ? "0" : "1");
                }
                request.putDynamicFiled(next, String.valueOf(map.get(next)));
            }

        }


        if (legacy) {
            request.callerIsGms();
        } else {
            request.callerIsApp();
        }
        AuthResponse response = request.getResponse();
        authManager.storeResponse(response);

        return response;
    }

    private List<Cookie> getCookies(Account account, ConsentUrlResponse signInConsentUrlResponse) throws IOException {
        List<Cookie> cookies = new ArrayList<>();
        AuthResponse authResponse = getConsentAuthResponse(account);
        ProtoAdapter<ConsentCookiesResponse> cookiesProtoAdapter = ConsentCookiesResponse.ADAPTER;
        ConsentCookiesResponse signInConsentCookiesResponse = cookiesProtoAdapter.decode(BytesUtils.base64ToBytes(authResponse.auth));

        cookies.add(signInConsentUrlResponse.cookie);
        ConsentCookies consentCookies = signInConsentCookiesResponse.consentCookies;
        for (Cookie cookie : consentCookies.cookies) {
            if (".google.com".equals(cookie.domain) || "accounts.google.com".equals(cookie.path)) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    private AuthResponse getConsentAuthResponse(Account account) throws IOException {
        TokenRequestOptions requestOptions = new TokenRequestOptions()
                .newBuilder().field_1(false).field_7(1).version(3).build();
        byte[] bytes = requestOptions.encode();
        AuthRequest request = new AuthRequest().fromContext(context)
                .appIsGms()
                .email(account.name)
                .tokenRequestOptions(BytesUtils.bytesToBase64(bytes))
                .token(accountManager.getPassword(account))
                .operatorCountry(null)
                .service("weblogin:url=https://accounts.google.com");
        request.hasPermission();
        request.systemPartition();
        request.oauth2Foreground("1");
        request.callerIsGms();
        return request.getResponse();
    }
}
