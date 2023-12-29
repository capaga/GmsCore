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
package org.microg.gms.auth

import android.content.Context
import org.microg.gms.base.core.R
import org.microg.gms.common.Constants
import org.microg.gms.common.HttpFormClient
import org.microg.gms.common.HttpFormClient.RequestContent
import org.microg.gms.common.HttpFormClient.RequestContentDynamic
import org.microg.gms.common.Utils
import org.microg.gms.profile.Build
import org.microg.gms.profile.ProfileManager.ensureInitialized
import java.io.IOException
import java.util.Locale

class AuthRequest : HttpFormClient.Request() {
    @HttpFormClient.RequestHeader("User-Agent")
    private var userAgent: String? = null

    @HttpFormClient.RequestHeader("app")
    @RequestContent("app")
    var app: String? = null

    @RequestContent("client_sig")
    var appSignature: String? = null

    @RequestContent("callerPkg")
    var caller: String? = null

    @RequestContent("callerSig")
    var callerSignature: String? = null

    @HttpFormClient.RequestHeader(value = ["device"], nullPresent = true)
    @RequestContent(value = ["androidId"], nullPresent = true)
    var androidIdHex: String? = null

    @RequestContent("sdk_version")
    var sdkVersion = 0

    @RequestContent("device_country")
    var countryCode: String? = null

    @RequestContent("operatorCountry")
    var operatorCountryCode: String? = null

    @RequestContent("lang")
    var locale: String? = null

    @RequestContent("google_play_services_version")
    var gmsVersion = Constants.GMS_VERSION_CODE

    @RequestContent("accountType")
    var accountType: String? = null

    @RequestContent("Email")
    var email: String? = null

    @RequestContent("service")
    var service: String? = null

    @RequestContent("source")
    var source: String? = null

    @RequestContent("is_called_from_account_manager", "_opt_is_called_from_account_manager")
    var isCalledFromAccountManager = false

    @RequestContent("Token")
    var token: String? = null

    @RequestContent("system_partition")
    var systemPartition = false

    @RequestContent("get_accountid")
    var getAccountId = false

    @RequestContent("ACCESS_TOKEN")
    var isAccessToken = false

    @RequestContent("droidguard_results")
    var droidguardResults: String? = null

    @RequestContent("has_permission")
    var hasPermission = false

    @RequestContent("it_caveat_types")
    var itCaveatTypes: String? = null // AuthPrefs

    @RequestContent("check_email")
    var checkEmail = false

    @RequestContent("token_request_options")
    var tokenRequestOptions: String? = null

    @RequestContent("request_visible_actions")
    var requestVisibleActions: String? = null

    @RequestContent("oauth2_prompt")
    var oauth2Prompt: String? = null

    @RequestContentDynamic
    var dynamicFields: MutableMap<String, String> = HashMap()

    @RequestContent("add_account")
    var addAccount = false

    @RequestContent("delegation_type")
    var delegationType: String? = null

    @RequestContent("delegatee_user_id")
    var delegationUserId: String? = null

    @RequestContent("oauth2_foreground")
    var oauth2Foreground: String? = null

    @RequestContent("oauth2_include_profile")
    var oauth2IncludeProfile: String? = null

    @RequestContent("oauth2_include_email")
    var oauth2IncludeEmail: String? = null

    var deviceName: String? = null
    var buildVersion: String? = null
    var isGmsApp:Boolean = false

    override fun prepare() {
        userAgent = String.format(USER_AGENT, deviceName, buildVersion)
    }

    fun build(context: Context) = apply {
        ensureInitialized(context)
        sdkVersion = Build.VERSION.SDK_INT
        deviceName = Build.DEVICE
        buildVersion = Build.ID
        optimizedDeviceParams(context)
    }

    fun source(source: String?) = apply {
        this.source = source
    }

    fun email(email: String?) = apply {
        this.email = email
    }

    fun token(token: String?) = apply {
        this.token = token
    }

    fun hasPermission() = apply {
        this.hasPermission = true
    }

    fun service(service: String?) = apply {
        this.service = service
    }

    fun addAccount() = apply {
        this.addAccount = true
    }

    fun accessToken() = apply {
        this.isAccessToken = true
    }

    fun droidguardResults(result: String?) = apply {
        this.droidguardResults = result
    }

    fun systemPartition() = apply {
        this.systemPartition = true
    }

    fun locale(locale: Locale) = apply {
        this.locale = locale.toString()
        countryCode = locale.country.lowercase(Locale.getDefault())
        operatorCountryCode = locale.country.lowercase(Locale.getDefault())
    }

    /**
     * 解决mate40 p40等设备登陆问题，需要置空deviceName、buildVersion
     */
    private fun optimizedDeviceParams(context: Context) {
        if (deviceName == null) {
            return
        }
        val whitelist = listOf(*context.resources.getStringArray(R.array.device_names))
        if (whitelist.contains(deviceName!!.uppercase())) {
            deviceName = ""
            buildVersion = ""
        } else {
            deviceName = Build.DEVICE
            buildVersion = Build.ID
        }
    }

    fun fromContext(context: Context) = apply {
        build(context)
        locale(Utils.getLocale(context))
        androidIdHex = java.lang.Long.toHexString(0L)
        //        if (AuthPrefs.shouldIncludeAndroidId(context)) {
//            androidIdHex = Long.toHexString(LastCheckinInfo.read(context).getAndroidId());
//        }
    }

    fun tokenRequestOptions(tokenRequestOptions: String?) = apply {
        this.tokenRequestOptions = tokenRequestOptions
    }

    fun requestVisibleActions(requestVisibleActions: String?) = apply {
        this.requestVisibleActions = requestVisibleActions
    }

    fun putDynamicFiled(key: String, value: String) = apply {
        dynamicFields[key] = value
    }

    fun app(app: String?, appSignature: String?) = apply {
        this.app = app
        this.appSignature = appSignature
    }

    fun appIsGms() = apply {
        app(Constants.GMS_PACKAGE_NAME, Constants.GMS_PACKAGE_SIGNATURE_SHA1)
    }

    fun callerIsGms() = apply {
        caller(Constants.GMS_PACKAGE_NAME, Constants.GMS_PACKAGE_SIGNATURE_SHA1)
    }

    fun callerIsApp() = apply {
        caller(app, appSignature)
    }

    fun caller(caller: String?, callerSignature: String?) = apply {
        this.caller = caller
        this.callerSignature = callerSignature
    }

    val accountId: AuthRequest
        get() {
            getAccountId = true
            return this
        }

    @get:Throws(IOException::class)
    val response: AuthResponse
        get() = HttpFormClient.request(SERVICE_URL, this, AuthResponse::class.java)

    fun getResponseAsync(callback: HttpFormClient.Callback<AuthResponse>?) {
        HttpFormClient.requestAsync(SERVICE_URL, this, AuthResponse::class.java, callback)
    }

    fun copyFromRequest(request: AuthRequest?) = apply {
        request?.isGmsApp?.let { if(it) appIsGms() }

        request?.checkEmail?.let { checkEmail = it }
        request?.addAccount?.let { addAccount = it }
        request?.getAccountId?.let { getAccountId = it }
        request?.isAccessToken?.let { isAccessToken = it }

        request?.token?.let { token = it }
        request?.systemPartition?.let { systemPartition = it }
        request?.hasPermission?.let { hasPermission = it }
        request?.delegationType?.let { delegationType = it }
        request?.delegationUserId?.let { delegationUserId = it }

        request?.tokenRequestOptions?.let { tokenRequestOptions = it }
        request?.requestVisibleActions?.let { requestVisibleActions = it }
        request?.oauth2Prompt?.let { oauth2Prompt = it }
        request?.oauth2Foreground?.let { oauth2Foreground = it }
        request?.itCaveatTypes?.let { itCaveatTypes = it }
        request?.operatorCountryCode?.let { operatorCountryCode = it }

        request?.droidguardResults?.let { droidguardResults = it }
        request?.operatorCountryCode?.let { operatorCountryCode = it }
        request?.oauth2IncludeProfile?.let { oauth2IncludeProfile = it }
        request?.oauth2IncludeEmail?.let { oauth2IncludeEmail = it }

        request?.dynamicFields?.let {
            if (it.isNotEmpty()) {
                val mutableIterator = it.iterator()
                while (mutableIterator.hasNext()) {
                    val entry = mutableIterator.next()
                    if (AuthConstants.OAUTH2_PROMPT == entry.key) {
                        this.oauth2Prompt = entry.value
                        continue
                    }
                    this.putDynamicFiled(entry.key, entry.value)
                }
            }
        }
    }

    override fun toString(): String {
        return "AuthRequest(userAgent=$userAgent, app=$app, appSignature=$appSignature, caller=$caller, callerSignature=$callerSignature, androidIdHex=$androidIdHex, sdkVersion=$sdkVersion, countryCode=$countryCode, operatorCountryCode=$operatorCountryCode, locale=$locale, gmsVersion=$gmsVersion, accountType=$accountType, email=$email, service=$service, source=$source, isCalledFromAccountManager=$isCalledFromAccountManager, token=$token, systemPartition=$systemPartition, getAccountId=$getAccountId, isAccessToken=$isAccessToken, droidguardResults=$droidguardResults, hasPermission=$hasPermission, itCaveatTypes=$itCaveatTypes, checkEmail=$checkEmail, tokenRequestOptions=$tokenRequestOptions, requestVisibleActions=$requestVisibleActions, oauth2Prompt=$oauth2Prompt, dynamicFields=$dynamicFields, addAccount=$addAccount, deviceName=$deviceName, buildVersion=$buildVersion, delegationType=$delegationType, delegationUserId=$delegationUserId, oauth2Foreground=$oauth2Foreground, oauth2IncludeProfile=$oauth2IncludeProfile, oauth2IncludeEmail=$oauth2IncludeEmail)"
    }

    companion object {
        private const val SERVICE_URL = "https://android.googleapis.com/auth"
        private const val USER_AGENT = "GoogleAuth/1.4 (%s %s); gzip"
    }
}