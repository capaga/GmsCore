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

public class AuthConstants {
    public static final String DEFAULT_ACCOUNT = "<<default account>>";
    public static final String SCOPE_GET_ACCOUNT_ID = "^^_account_id_^^";
    public static final String PROVIDER_METHOD_GET_ACCOUNTS = "get_accounts";
    public static final String PROVIDER_METHOD_CLEAR_PASSWORD = "clear_password";
    public static final String PROVIDER_EXTRA_CLEAR_PASSWORD = "clear_password";
    public static final String PROVIDER_EXTRA_ACCOUNTS = "accounts";
    public static final String DEFAULT_ACCOUNT_TYPE = "com.google";
    public static final String SCOPE_GET_SNOWBALL = "^^snowballing^^";
    public static final String SCOPE_EM_OP_PRO = "oauth2:email openid profile";
    public static final String DEFAULT_USER_ID = "<<default user id>>";

    public static final String AUDIENCE_TOKEN = "AudienceToken";
    public static final String OAUTH2_TOKEN = "Oauth2Token";
    public static final String EXP_TIME = "EXPTIME";
    public static final String OAUTH2_PROMPT = "oauth2Prompt";
    public static final String OAUTH2_FG = "oauth2Foreground";

    public static final String GOOGLE_USER_ID = "GoogleUserId";
    public static final String GOOGLE_SID = "SID";
    public static final String GOOGLE_L_SID = "LSID";

    public static final String SIGN_IN_CREDENTIAL = "sign_in_credential";
    public static final String STATUS = "status";
    public static final String WEB_LABEL = "weblogin:";
    public static final String WEB_LOGIN = "weblogin:url=https://accounts.google.com";

    public static final String GOOGLE_SIGN_IN_STATUS = "googleSignInStatus";
    public static final String GOOGLE_SIGN_IN_ACCOUNT = "googleSignInAccount";
    public static final String SIGN_IN_ACCOUNT = "signInAccount";

    public static final String CONSENT_KEY_COOKIE = "cookie-";
    public static final String CONSENT_URL = "consentUrl";
    public static final String CONSENT_USER_EXIT = "consent_user_exit";
    public static final String CONSENT_MESSENGER = "messenger";
    public static final String CONSENT_RESULT = "consent_result";

    public static final String ERROR_CODE = "errorCode";

    public static final String OAUTH_INCLUDE_PROFILE = "oauth2_include_profile";
    public static final String OAUTH_INCLUDE_EMAIL = "oauth2_include_email";

    public static final String DELEGATION_TYPE = "delegation_type";
    public static final String DELEGATEE_USER_ID = "delegatee_user_id";
}
