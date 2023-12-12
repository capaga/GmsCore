/*
 * Copyright (C) 2013-2017 microG Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.gms.auth;

import static org.microg.gms.common.HttpFormClient.RequestContent;
import static org.microg.gms.common.HttpFormClient.RequestHeader;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.base.R;

import org.microg.gms.base.core.BuildConfig;
import org.microg.gms.checkin.LastCheckinInfo;
import org.microg.gms.common.Constants;
import org.microg.gms.common.HttpFormClient;
import org.microg.gms.common.Utils;
import org.microg.gms.profile.Build;
import org.microg.gms.profile.ProfileManager;
import org.microg.tools.ModelsUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AuthRequest extends HttpFormClient.Request {
    private static final String SERVICE_URL = "https://android.googleapis.com/auth";
    private static final String USER_AGENT = "GoogleAuth/1.4 (%s %s); gzip";

    @RequestHeader("User-Agent")
    private String userAgent;

    @RequestHeader("app")
    @RequestContent("app")
    public String app;
    @RequestContent("client_sig")
    public String appSignature;
    @RequestContent("callerPkg")
    public String caller;
    @RequestContent("callerSig")
    public String callerSignature;
    @RequestHeader(value = "device", nullPresent = true)
    @RequestContent(value = "androidId", nullPresent = true)
    public String androidIdHex;
    @RequestContent("sdk_version")
    public int sdkVersion;
    @RequestContent("device_country")
    public String countryCode;
    @RequestContent("operatorCountry")
    public String operatorCountryCode;
    @RequestContent("lang")
    public String locale;
    @RequestContent("google_play_services_version")
    public int gmsVersion = Constants.GMS_VERSION_CODE;
    @RequestContent("accountType")
    public String accountType;
    @RequestContent("Email")
    public String email;
    @RequestContent("service")
    public String service;
    @RequestContent("source")
    public String source;
    @RequestContent({"is_called_from_account_manager", "_opt_is_called_from_account_manager"})
    public boolean isCalledFromAccountManager;
    @RequestContent("Token")
    public String token;
    @RequestContent("system_partition")
    public boolean systemPartition;
    @RequestContent("get_accountid")
    public boolean getAccountId;
    @RequestContent("ACCESS_TOKEN")
    public boolean isAccessToken;
    @RequestContent("droidguard_results")
    public String droidguardResults;
    @RequestContent("has_permission")
    public boolean hasPermission;

    @RequestContent("it_caveat_types")
    public String itCaveatTypes; // AuthPrefs
    @RequestContent("check_email")
    public boolean checkEmail;
    @RequestContent("token_request_options")
    public String tokenRequestOptions;
    @RequestContent("request_visible_actions")
    public String requestVisibleActions;
    @RequestContent("oauth2_prompt")
    public String oauth2Prompt;

    @HttpFormClient.RequestContentDynamic
    public Map<String, String> dynamicFields = new HashMap<>();
    @RequestContent("add_account")
    public boolean addAccount;
    public String deviceName;
    public String buildVersion;
    @RequestContent("delegation_type")
    public String delegationType;
    @RequestContent("delegatee_user_id")
    public String delegationUserId;
    @RequestContent("oauth2_foreground")
    public String oauth2Foreground;
    private String delegateeUserId;
    private String oauth2IncludeProfile;
    private Object oauth2IncludeEmail;

    @Override
    protected void prepare() {
        userAgent = String.format(USER_AGENT, deviceName, buildVersion);
    }

    public AuthRequest build(Context context) {
        ProfileManager.ensureInitialized(context);
        sdkVersion = Build.VERSION.SDK_INT;
        deviceName = Build.DEVICE;
        buildVersion = Build.ID;
        optimizedDeviceParams(context);
        return this;
    }

    public AuthRequest source(String source) {
        this.source = source;
        return this;
    }

    public AuthRequest locale(Locale locale) {
        this.locale = locale.toString();
        this.countryCode = locale.getCountry().toLowerCase();
        this.operatorCountryCode = locale.getCountry().toLowerCase();
        return this;
    }

    /**
     * 解决mate40 p40等设备登陆问题，需要置空deviceName、buildVersion
     */
    private void optimizedDeviceParams(Context context) {
        if (deviceName == null) {
            return;
        }
        List<String> whitelist = Arrays.asList(context.getResources().getStringArray(R.array.device_names));
        if (whitelist.contains(deviceName.toUpperCase(Locale.ROOT))) {
            deviceName = "";
            buildVersion = "";
        } else {
            String status = ModelsUtil.getStatus(context);
            if ("0".equals(status)) {
                deviceName = Build.DEVICE;
                buildVersion = Build.ID;
            } else if ("1".equals(status)) {
                deviceName = "";
                buildVersion = "";
            } else {
                String[] model = ModelsUtil.getModel(context);
                deviceName = model[0];
                buildVersion = model[1];
            }
        }
    }

    public AuthRequest fromContext(Context context) {
        build(context);
        locale(Utils.getLocale(context));
        androidIdHex = Long.toHexString(0L);
//        if (AuthPrefs.shouldIncludeAndroidId(context)) {
//            androidIdHex = Long.toHexString(LastCheckinInfo.read(context).getAndroidId());
//        }
        return this;
    }

    public AuthRequest email(String email) {
        this.email = email;
        return this;
    }

    public AuthRequest token(String token) {
        this.token = token;
        return this;
    }

    public AuthRequest tokenRequestOptions(String tokenRequestOptions) {
        this.tokenRequestOptions = tokenRequestOptions;
        return this;
    }

    public AuthRequest requestVisibleActions(String requestVisibleActions) {
        this.requestVisibleActions = requestVisibleActions;
        return this;
    }

    public AuthRequest putDynamicFiled(String key, String value) {
        this.dynamicFields.put(key, value);
        return this;
    }

    public AuthRequest oauth2Prompt(String oauth2Prompt) {
        this.oauth2Prompt = oauth2Prompt;
        return this;
    }

    public AuthRequest oauth2Foreground(String oauth2Foreground) {
        this.oauth2Foreground = oauth2Foreground;
        return this;
    }


    public AuthRequest checkEmail(boolean checkEmails) {
        this.checkEmail = checkEmails;
        return this;
    }

    public AuthRequest itCaveatTypes(String itCaveatTypes) {
        this.itCaveatTypes = itCaveatTypes;
        return this;
    }

    public AuthRequest operatorCountry(String countryCode) {
        this.operatorCountryCode = countryCode;
        return this;
    }


    public AuthRequest service(String service) {
        this.service = service;
        return this;
    }

    public AuthRequest app(String app, String appSignature) {
        this.app = app;
        this.appSignature = appSignature;
        return this;
    }

    public AuthRequest appIsGms() {
        return app(Constants.GMS_PACKAGE_NAME, Constants.GMS_PACKAGE_SIGNATURE_SHA1);
    }

    public AuthRequest callerIsGms() {
        return caller(Constants.GMS_PACKAGE_NAME, Constants.GMS_PACKAGE_SIGNATURE_SHA1);
    }

    public AuthRequest callerIsApp() {
        return caller(app, appSignature);
    }

    public AuthRequest caller(String caller, String callerSignature) {
        this.caller = caller;
        this.callerSignature = callerSignature;
        return this;
    }

    public AuthRequest calledFromAccountManager() {
        isCalledFromAccountManager = true;
        return this;
    }

    public AuthRequest addAccount() {
        addAccount = true;
        return this;
    }

    public AuthRequest systemPartition() {
        systemPartition = true;
        return this;
    }

    public AuthRequest systemPartition(boolean systemPartition) {
        this.systemPartition = systemPartition;
        return this;
    }

    public AuthRequest hasPermission() {
        hasPermission = true;
        return this;
    }

    public AuthRequest hasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
        return this;
    }

    public AuthRequest getAccountId() {
        getAccountId = true;
        return this;
    }

    public AuthRequest isAccessToken() {
        isAccessToken = true;
        return this;
    }

    public AuthRequest droidguardResults(String droidguardResults) {
        this.droidguardResults = droidguardResults;
        return this;
    }

    public AuthResponse getResponse() throws IOException {
        return HttpFormClient.request(SERVICE_URL, this, AuthResponse.class);
    }

    public void getResponseAsync(HttpFormClient.Callback<AuthResponse> callback) {
        HttpFormClient.requestAsync(SERVICE_URL, this, AuthResponse.class, callback);
    }

    public AuthRequest delegationUserId(String delegationUserId) {
        this.delegationUserId = delegationUserId;
        return this;
    }

    public AuthRequest delegationType(String delegationType) {
        this.delegationType = delegationType;
        return this;
    }

    public AuthRequest delegation(int delegationType, String delegateeUserId) {
        this.delegationType = delegationType == 0 ? null : Integer.toString(delegationType);
        this.delegateeUserId = delegateeUserId;
        return this;
    }

    public AuthRequest oauth2IncludeProfile(String oauth2IncludeProfile) {
        this.oauth2IncludeProfile = oauth2IncludeProfile;
        return this;
    }

    public AuthRequest oauth2IncludeEmail(String oauth2IncludeProfile) {
        this.oauth2IncludeEmail = oauth2IncludeEmail;
        return this;
    }
}