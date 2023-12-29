package org.microg.gms.auth

import android.accounts.Account
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.microg.gms.utils.BytesUtils

class AuthServiceManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.NONE) {
            AuthServiceManager()
        }
        private const val TAG = "AuthServiceManager"
    }

    fun getAuthManager(
        context: Context,
        packageName: String,
        account: Account,
        httpServer: String,
    ): AuthManager {
        return AuthManager(context, account.name, packageName, httpServer)
    }

    suspend fun getAudienceToken(
        context: Context,
        packageName: String,
        account: Account,
        httpServer: String,
        permitted: Boolean = false,
        map: MutableMap<String, String>? = null
    ) = withContext(Dispatchers.IO) {
        AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
            val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3)
                .sessionId(BytesUtils.generateSessionId().trim()).build()
            this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
            this.hasPermission = false
            this.checkEmail = true
            this.itCaveatTypes = "-1"
            this.oauth2IncludeProfile = "1"
            this.oauth2IncludeEmail = "1"
            map?.let { this.dynamicFields = it }
        }).apply {
            isPermitted = permitted
            Log.d(TAG, "getAudienceToken start ")
        }.requestAuth(
            calledIsGms = true, calledFromAccountManager = true, needPermitted = permitted
        )
    }

    suspend fun getOauth2Token(
        context: Context,
        packageName: String,
        account: Account,
        httpServer: String,
        permitted: Boolean = false,
        map: MutableMap<String, String>? = null
    ) = withContext(Dispatchers.IO) {
        AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
            val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3)
                .sessionId(BytesUtils.generateSessionId().trim()).build()
            this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
            this.hasPermission = false
            this.checkEmail = true
            this.itCaveatTypes = "2"
            this.oauth2IncludeProfile = "1"
            this.oauth2IncludeEmail = "1"
            map?.let { this.dynamicFields = it }
        }).apply {
            isPermitted = permitted
            Log.d(TAG, "getOauth2Token start ")
        }.requestAuth(
            calledIsGms = true, calledFromAccountManager = true, needPermitted = permitted
        )
    }

    suspend fun getConsentUrl(
        context: Context,
        packageName: String,
        account: Account,
        httpServer: String,
        map: MutableMap<String, String>? = null
    ) = withContext(Dispatchers.IO) {
        AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
            val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3)
                .sessionId(BytesUtils.generateSessionId().trim()).build()
            this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
            this.hasPermission = false
            this.checkEmail = true
            this.itCaveatTypes = "2"
            this.oauth2IncludeProfile = "1"
            this.oauth2IncludeEmail = "1"
            map?.let { this.dynamicFields = it }
        }).apply {
            Log.d(TAG, "getConsentUrl start ")
        }.requestAuth(calledIsGms = true, calledFromAccountManager = false, needPermitted = false)
    }

    suspend fun getConsentCookies(
        context: Context, packageName: String, account: Account, signInConsentUrlResponse: ConsentUrlResponse
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "getConsentCookies start ")
        val consentAuthResponse =
            AuthManager(context, account.name, packageName, AuthConstants.WEB_LOGIN, AuthRequest().apply {
                val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3).build()
                this.isGmsApp = true
                this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
                this.hasPermission = true
                this.systemPartition = true
                this.oauth2Foreground = "1"
            }).requestAuth(calledIsGms = true, calledFromAccountManager = false, needPermitted = false)

        val cookiesResponse = ConsentCookiesResponse.ADAPTER.decode(BytesUtils.base64ToBytes(consentAuthResponse.auth))
        arrayListOf(signInConsentUrlResponse.cookie).apply {
            cookiesResponse.consentCookies?.cookies?.filter { ".google.com" == it.domain || "accounts.google.com" == it.path }
                ?.forEach { add(it) }
        }
    }

    fun requestSharedDataAuth(
        context: Context,
        packageName: String,
        account: Account,
        httpServer: String,
    ) = AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
        val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3)
            .sessionId(BytesUtils.generateSessionId().trim()).build()
        this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
        this.hasPermission = true
        this.checkEmail = true
        this.itCaveatTypes = "-1"
        this.oauth2IncludeProfile = "0"
        this.oauth2IncludeEmail = "0"
        this.oauth2Foreground = "1"
    }).requestAuth(
        calledIsGms = true, calledFromAccountManager = false, needPermitted = false
    )

    fun getGameOauth2Token(
        context: Context,
        httpServer: String,
        account: Account,
        packageName: String,
        callerIsGms: Boolean,
    ) = AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
        val requestOptions = RequestOptions().newBuilder().field_1(false).field_4(1).field_5(2).field_7(1).version(3)
            .sessionId(BytesUtils.generateSessionId().trim()).build()
        this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
        this.itCaveatTypes = "2"
        this.checkEmail = true
    }).requestAuth(calledIsGms = callerIsGms, calledFromAccountManager = false, needPermitted = false)

    fun getGameFirstPartyToken(
        context: Context,
        httpServer: String,
        account: Account,
        packageName: String,
        callerIsGms: Boolean,
    ) = AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
        val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3)
            .sessionId(BytesUtils.generateSessionId().trim()).build();
        this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
        this.checkEmail = true
        this.oauth2Foreground = "1"
    }).requestAuth(calledIsGms = callerIsGms, calledFromAccountManager = false, needPermitted = false)

    fun getGameServerAuthToken(
        context: Context,
        httpServer: String,
        account: Account,
        packageName: String,
        callerIsGms: Boolean,
    ) = AuthManager(context, account.name, packageName, httpServer, AuthRequest().apply {
        val requestOptions = RequestOptions().newBuilder().field_1(false).field_7(1).version(3)
            .sessionId(BytesUtils.generateSessionId().trim()).build()
        this.tokenRequestOptions = BytesUtils.bytesToBase64(requestOptions.encode())
        this.itCaveatTypes = "2"
        this.oauth2IncludeProfile = "0"
        this.checkEmail = true
        this.oauth2Prompt = "auto"
        this.oauth2IncludeEmail = "0"
    }).requestAuth(calledIsGms = callerIsGms, calledFromAccountManager = false, needPermitted = false)

}