/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */
package org.microg.gms.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import org.microg.gms.auth.AuthPrefs.isTrustGooglePermitted
import org.microg.gms.common.HttpFormClient
import org.microg.gms.common.PackageUtils
import java.io.IOException
import java.lang.ref.WeakReference
import android.accounts.Account
import android.accounts.AccountManager
import java.util.stream.Collectors

private const val TAG = "GmsAuthManager"
private const val PERMISSION_TREE_BASE = "com.google.android.googleapps.permission.GOOGLE_AUTH."
private const val ONE_HOUR_IN_SECONDS = 60 * 60

class AuthManager(val context: Context, val accountName:String, val packageName: String, private val serverService: String, val request: AuthRequest? = null) {

    private var account: Account? = null

    private val accountManager by lazy { AccountManager.get(context) }

    fun getService(): String = serverService

    private var packageSignature: String? = null
        get() {
            if (field == null) field = PackageUtils.firstSignatureDigest(context, packageName)
            return field
        }

    var isPermitted: Boolean
        get() {
            if (!serverService.startsWith("oauth")) {
                if (context.packageManager.checkPermission(
                        PERMISSION_TREE_BASE + serverService,
                        packageName
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                }
            }
            return "1" == getUserData(buildPermKey())
        }
        set(value) {
            setUserData(buildPermKey(), if (value) "1" else "0")
            if (Build.VERSION.SDK_INT >= 26 && value) {
                // Make account persistently visible as we already granted access
                accountManager.setAccountVisibility(getAccount(), packageName, AccountManager.VISIBILITY_VISIBLE)
            }
        }

    private var expiry: Long
        get() {
            val data = getUserData(buildExpireKey()) ?: return -1
            return data.toLong()
        }
        set(expiry) {
            setUserData(buildExpireKey(), expiry.toString())
        }

    var authToken: String?
        get() {
            if (serverService.startsWith(AuthConstants.WEB_LABEL)) return null
            if (System.currentTimeMillis() / 1000L >= expiry - 300L) {
                Log.d(TAG, "token present, but expired")
                return null
            }
            return peekAuthToken()
        }
        set(auth) {
            setAuthToken(serverService, auth)
        }

    private val isSystemApp: Boolean
        get() = try {
            val flags = context.packageManager.getApplicationInfo(packageName, 0).flags
            flags and ApplicationInfo.FLAG_SYSTEM > 0 || flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP > 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    fun getAccountType() = request?.accountType ?: AuthConstants.DEFAULT_ACCOUNT_TYPE

    private fun getAccount(): Account? {
        if (account == null) {
            account = Account(accountName, getAccountType())
        }
        return account
    }

    private fun buildPermKey() = "perm.${buildTokenKey()}"

    fun getUserData(key: String?): String? {
        return accountManager.getUserData(getAccount(), key)
    }

    fun getUserData(account: Account, key: String?): String? {
        return accountManager.getUserData(account, key)
    }

    private fun setUserData(key: String?, value: String?) {
        accountManager.setUserData(getAccount(), key, value)
    }

    fun peekAuthToken(): String? {
        return accountManager.peekAuthToken(getAccount(), buildTokenKey())
    }

    fun peekAuthToken(account: Account, key: String?): String? {
        return accountManager.peekAuthToken(account, key)
    }

    @JvmOverloads
    fun buildTokenKey(service: String = serverService): String {
        return buildTokenKey(packageName, service, request?.delegationUserId, request?.delegationType)
    }

    fun accountExists(): Boolean {
        for (refAccount in accountManager.getAccountsByType(getAccountType())) {
            if (refAccount.name.equals(accountName, ignoreCase = true)) return true
        }
        return false
    }

    fun buildExpireKey(): String {
        return buildExpireKey(buildTokenKey())
    }

    private fun setAuthToken(service: String, auth: String?) {
        accountManager.setAuthToken(getAccount(), buildTokenKey(service), auth)
        if (Build.VERSION.SDK_INT >= 26 && auth != null) {
            // Make account persistently visible as we already granted access
            accountManager.setAccountVisibility(getAccount(), packageName, AccountManager.VISIBILITY_VISIBLE)
        }
    }

    fun invalidateAuthToken() {
        val authToken = peekAuthToken()
        invalidateAuthToken(authToken)
    }

    @SuppressLint("MissingPermission")
    fun invalidateAuthToken(auth: String?) {
        accountManager.invalidateAuthToken(getAccountType(), auth)
    }

    fun buildExpireKey(tokenKey: String): String = "EXP.$tokenKey"

    fun buildTokenKey(
        packageName: String, service: String, delegatedUserId: String? = null, delegationType: String? = null
    ): String {
        val packageSignature = buildPackageSignature(packageName)
        val tokenRequestOptions = buildTokenRequestOptions(delegatedUserId, delegationType)
        return "$packageName:$packageSignature:$service$tokenRequestOptions"
    }

    private fun buildPackageSignature(packageName: String): String? {
        return PackageUtils.firstSignatureDigest(context, packageName)
    }

    private fun buildTokenRequestOptions(delegatedUserId: String?, delegationType: String?): String {
        val treeMap = HashMap<String, String>()
        delegatedUserId?.let { treeMap[AuthConstants.DELEGATEE_USER_ID] = it }
        delegationType?.let { treeMap[AuthConstants.DELEGATION_TYPE] = it }
        var options: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            options = treeMap.entries.stream().map { (key, value): Map.Entry<String, String> -> "$key=$value" }
                .collect(Collectors.joining("&"))
        } else {
            val result = StringBuilder()
            for ((key, value) in treeMap) {
                result.append(key).append("=").append(value).append("&")
            }
            options = result.toString()
            if (options.endsWith("&")) {
                options = options.substring(0, result.length - 1)
            }
        }
        return if (treeMap.isEmpty()) "" else ":^^snowballing^^?$options"
    }

    fun storeResponse(response: AuthResponse) {
        if (serverService.startsWith(AuthConstants.WEB_LABEL)) return
        if (response.accountId != null) setUserData(AuthConstants.GOOGLE_USER_ID, response.accountId)
        if (response.Sid != null) setAuthToken(AuthConstants.GOOGLE_SID, response.Sid)
        if (response.LSid != null) setAuthToken(AuthConstants.GOOGLE_L_SID, response.LSid)
        if (response.auth != null && (response.expiry != 0L || response.storeConsentRemotely)) {
            authToken = response.auth
            expiry = if (response.expiry > 0) {
                response.expiry
            } else {
                System.currentTimeMillis() / 1000 + ONE_HOUR_IN_SECONDS // make valid for one hour by default
            }
        }
    }

    private fun buildAuthRequest(): AuthRequest {
        Log.d(TAG, "buildAuthRequest service -> $serverService")
        return AuthRequest().fromContext(context).apply {
            app(packageName, packageSignature)
            token = accountManager.getPassword(getAccount())
            source = "android"
            email = accountName
            service = serverService
            systemPartition = isSystemApp
            hasPermission = isPermitted
        }.copyFromRequest(request)
    }

    @Throws(IOException::class)
    fun requestAuth(
        calledIsGms: Boolean,
        calledFromAccountManager: Boolean = true,
        needPermitted: Boolean = true
    ): AuthResponse {
        if (serverService == AuthConstants.SCOPE_GET_ACCOUNT_ID || serverService == AuthConstants.SCOPE_GET_SNOWBALL) {
            val response = AuthResponse().apply {
                auth = accountManager.getUserData(getAccount(), AuthConstants.GOOGLE_USER_ID)
                accountId = auth
            }
            return response
        }
        if (needPermitted && (isPermitted || isTrustGooglePermitted(context))) {
            val token = authToken
            if (token != null) {
                val response = AuthResponse()
                response.issueAdvice = "stored"
                response.auth = token
                if (serverService.startsWith("oauth2:")) {
                    response.grantedScopes = serverService.substring(7)
                }
                response.expiry = expiry
                return response
            }
        }
        val authResponse = buildAuthRequest().run {
            if (calledIsGms) {
                callerIsGms()
                isCalledFromAccountManager = calledFromAccountManager
            } else {
                callerIsApp()
            }
            Log.d(TAG, toString())
            response
        }.also {
            if (needPermitted && (!isPermitted && !isTrustGooglePermitted(context))) {
                it.auth = null
            } else {
                storeResponse(it)
            }
        }
        Log.d(TAG, authResponse.toString())
        return authResponse
    }

    fun requestAuthAsync(
        calledIsGms: Boolean,
        calledFromAccountManager: Boolean = true,
        callback: HttpFormClient.Callback<AuthResponse>?
    ) {
        buildAuthRequest().run {
            isCalledFromAccountManager = calledFromAccountManager
            if (calledIsGms) {
                callerIsGms()
            } else {
                callerIsApp()
            }
            Log.d(TAG, toString())
            getResponseAsync(WeakReference(callback).get())
        }
    }

}