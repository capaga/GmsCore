/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.auth.signin

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.microg.gms.auth.AuthConstants
import org.microg.gms.auth.AuthManager
import org.microg.gms.auth.AuthServiceManager
import org.microg.gms.auth.ConsentUrlResponse
import org.microg.gms.common.AccountManagerUtils
import org.microg.gms.common.Constants
import org.microg.gms.games.signin.GamesSignInManager
import org.microg.gms.people.DatabaseHelper
import org.microg.gms.utils.BytesUtils
import org.microg.gms.utils.toHexString
import java.security.MessageDigest
import java.util.Date
import kotlin.math.min

private const val TAG = "AuthSignInExtensions"

val GoogleSignInOptions.scopeUris
    get() = scopes.orEmpty().sortedBy { it.scopeUri }

val GoogleSignInOptions.isGameSignIn
    get() = scopeUris.any { it.scopeUri == Scopes.GAMES_LITE }

val GoogleSignInOptions.includeId
    get() = scopeUris.any { it.scopeUri == Scopes.OPENID } || isGameSignIn

val GoogleSignInOptions.includeEmail
    get() = scopeUris.any { it.scopeUri == Scopes.EMAIL } || isGameSignIn

val GoogleSignInOptions.includeProfile
    get() = scopeUris.any { it.scopeUri == Scopes.PROFILE }

fun oAuthServerService(options: GoogleSignInOptions?): String {
    options?.run {
        if (scopeUris.isNotEmpty()) {
            return "oauth2:${scopeUris.joinToString(" ")}"
        }
    }
    return AuthConstants.SCOPE_EM_OP_PRO
}

fun idTokenServerService(options: GoogleSignInOptions?): String {
    return "audience:server:client_id:${options?.serverClientId}"
}

fun idTokenSaveServerService(options: GoogleSignInOptions?): String {
    return "${idTokenServerService(options)}?include_email=${if (options?.includeEmail == true) "1" else "0"}&include_profile=${if (options?.includeProfile == true) "1" else "0"}"
}

fun authTokenServerService(options: GoogleSignInOptions?): String {
    val scopeUrl = if (options?.scopeUris.isNullOrEmpty()) {
        "email openid profile"
    } else {
        options?.scopeUris?.joinToString(" ")
    }
    return "oauth2:server:client_id:${options?.serverClientId}:api_scope:$scopeUrl"
}

fun authTokenSaveServerService(options: GoogleSignInOptions?): String {
    return "${authTokenServerService(options)}?include_email=${if (options?.includeEmail == true) "1" else "0"}&include_profile=${if (options?.includeProfile == true) "1" else "0"}"
}

private fun Long?.orMaxIfNegative() = this?.takeIf { it >= 0L } ?: Long.MAX_VALUE

private val activeMutexLock = Mutex()

suspend fun performSignIn(
    context: Context, packageName: String,
    options: GoogleSignInOptions?,
    account: Account,
    permitted: Boolean = false,
): GoogleSignInAccount? {
    if (options?.isGameSignIn == true) {
        return dealGameSignIn(context, packageName, options, account)
    }

    Log.d(TAG, "performSignIn OAuth request start")
    val authManager =
        AuthServiceManager.instance.getAuthManager(context, packageName, account, oAuthServerService(options))
            .apply { isPermitted = permitted }
    val authResponse = withContext(Dispatchers.IO) {
        authManager.requestAuth(true)
    }
    Log.d(TAG, "performSignIn OAuth request end")
    if (authResponse.auth == null) return null

    val tokens: MutableMap<String, String> = HashMap(3)
    val googleUserId = authManager.getUserData(AccountManagerUtils.GOOGLE_USER_ID)
    tokens[AuthConstants.EXP_TIME] = authResponse.expiry.orMaxIfNegative().toString()

    if (options?.serverClientId == null) {
        Log.d(TAG, "performSignIn options?.serverClientId is null ")
        return buildGoogleSignInAccount(
            context, packageName, account, googleUserId, options, authResponse.grantedScopes, tokens
        )
    }

    if (!googleUserId.isNullOrEmpty()) {
        Log.d(TAG, "performSignIn checkTokenExpTime ")
        val flag = checkTokenExpTime(packageName, account, options, tokens, authManager)
        if (!flag) {
            Log.d(TAG, "performSignIn checkTokenExpTime flag false ")
            return buildGoogleSignInAccount(
                context, packageName, account, googleUserId, options, authResponse.grantedScopes, tokens
            )
        }
    }

    tokens.clear()
    Log.d(TAG, "performSignIn update token start")
    if (options.isIdTokenRequested){
        val audienceToken = AuthServiceManager.instance.getAudienceToken(
            context,
            packageName,
            account,
            idTokenServerService(options),
            permitted = authManager.isPermitted
        )
        tokens[AuthConstants.EXP_TIME] =
            min(authResponse.expiry.orMaxIfNegative(), audienceToken.expiry.orMaxIfNegative()).toString()
        audienceToken.auth?.let { tokens[AuthConstants.AUDIENCE_TOKEN] = it }
    }

    if (!options.isServerAuthCodeRequested) {
        Log.d(TAG, "performSignIn update token end isServerAuthCodeRequested false ")
        return buildGoogleSignInAccount(
            context, packageName, account, googleUserId, options, authResponse.grantedScopes, tokens
        )
    }

    if (!options.isForceCodeForRefreshToken) {
        Log.d(TAG, "performSignIn update token end isForceCodeForRefreshToken false ")
        val oauth2Token = AuthServiceManager.instance.getOauth2Token(
            context,
            packageName,
            account,
            authTokenServerService(options),
            permitted = authManager.isPermitted,
            mutableMapOf(Pair(AuthConstants.OAUTH2_PROMPT, "auto"))
        )
        oauth2Token.auth?.let { tokens[AuthConstants.OAUTH2_TOKEN] = it }
        return buildGoogleSignInAccount(
            context, packageName, account, googleUserId, options, authResponse.grantedScopes, tokens
        )
    }

    return dealConsentSignIn(
        context, packageName, options, account, authManager,
        tokens, googleUserId, authResponse.grantedScopes
    )
}

suspend fun dealGameSignIn(
    context: Context, packageName: String,
    options: GoogleSignInOptions?,
    account: Account
): GoogleSignInAccount {
    val gamesSignInManager = GamesSignInManager(context, account, packageName, options)
    val result = withContext(Dispatchers.IO) {
        Log.d(TAG, "performSignIn isGamesSignIn start")
        val deferred = activeMutexLock.withLock { CompletableDeferred<MutableMap<String, String>?>() }
        gamesSignInManager.gamesSignIn(object : GamesSignInManager.AuthSdkCallBack {
            override fun success(type: Int, tokens: MutableMap<String, String>?) {
                deferred.complete(tokens)
            }

            override fun onError(msg: String?) {
                deferred.complete(null)
            }
        })
        deferred.await()
    }
    Log.d(TAG, "performSignIn isGamesSignIn end")
    val googleUserId = AccountManager.get(context).getUserData(account, AuthConstants.GOOGLE_USER_ID)
    return buildGoogleSignInAccount(
        context, packageName, account, googleUserId, options, options?.scopeUris?.joinToString(" "), result
    )
}

suspend fun dealConsentSignIn(
    context: Context, packageName: String,
    options: GoogleSignInOptions?,
    account: Account, authManager: AuthManager,
    tokens: MutableMap<String, String>,
    googleUserId: String?, scopes: String?
): GoogleSignInAccount? {
    Log.d(TAG, "performSignIn request consent url start ")
    val option = mutableMapOf(Pair(AuthConstants.OAUTH2_PROMPT, "consent"))
    val response = AuthServiceManager.instance.getConsentUrl(
        context, packageName, account, authTokenServerService(options), option
    )
    val consentUrlResponse = ConsentUrlResponse.ADAPTER.decode(BytesUtils.base64ToBytes(response.resolutionDataBase64))
    Log.d(TAG, "performSignIn: consentUrl -> " + consentUrlResponse.consentUrl)

    val result = withContext(Dispatchers.IO) {
        val deferred = activeMutexLock.withLock { CompletableDeferred<String>() }
        Intent(context, ConsentSignInActivity::class.java).apply {
            putExtra(AuthConstants.CONSENT_URL, consentUrlResponse.consentUrl)
            putExtra(AuthConstants.CONSENT_MESSENGER, Messenger(object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    val content = msg.obj
                    Log.d(TAG, "performSignIn: ConsentSignInActivity deferred ")
                    deferred.complete(content?.toString() ?: AuthConstants.CONSENT_USER_EXIT)
                }
            }))
            AuthServiceManager.instance.getConsentCookies(context, packageName, account, consentUrlResponse)
                .forEachIndexed { index, cookie ->
                    putExtra(AuthConstants.CONSENT_KEY_COOKIE + index, "${cookie?.cookieName}=${cookie?.cookieValue};")
                }
        }.let {
            Log.d(TAG, "performSignIn: start ConsentSignInActivity")
            withContext(Dispatchers.Main) { context.startActivity(it) }
        }
        deferred.await()
    }

    Log.d(TAG, "performSignIn: end ConsentSignInActivity result -> $result")

    if (AuthConstants.CONSENT_USER_EXIT == result) {
        Log.d(TAG, "performSignIn: user cancel sign in ")
        return null
    }

    val oauth2Token = AuthServiceManager.instance.getOauth2Token(context,
        packageName,
        account,
        authTokenServerService(options),
        permitted = authManager.isPermitted,
        option.apply { put(AuthConstants.CONSENT_RESULT, result) })

    oauth2Token.auth?.let { tokens[AuthConstants.OAUTH2_TOKEN] = it }

    Log.d(TAG, "performSignIn: dealConsentSignIn oauth2Token -> $oauth2Token")

    return buildGoogleSignInAccount(
        context, packageName, account, googleUserId, options, scopes, tokens
    )
}

suspend fun buildGoogleSignInAccount(
    context: Context,
    packageName: String,
    account: Account,
    googleUserId: String?,
    options: GoogleSignInOptions?,
    scopes: String?,
    tokens: MutableMap<String, String>?
): GoogleSignInAccount {
    Log.d(TAG, "buildGoogleSignInAccount start ")
    val id = if (options?.includeId == true) googleUserId else null
    val tokenId =
        if (tokens != null && tokens.contains(AuthConstants.AUDIENCE_TOKEN)) tokens[AuthConstants.AUDIENCE_TOKEN] else null  //audienceToken
    val serverAuthCode =
        if (tokens != null && tokens.contains(AuthConstants.OAUTH2_TOKEN)) tokens[AuthConstants.OAUTH2_TOKEN] else null //oauth2token
    val expirationTime = if (tokens != null) tokens[AuthConstants.EXP_TIME]?.toLong() ?: 0L else 0L
    val obfuscatedIdentifier: String =
        MessageDigest.getInstance("MD5").digest("$googleUserId:$packageName".encodeToByteArray()).toHexString()
            .uppercase()
    val grantedScopes = scopes?.split(" ").orEmpty().map { Scope(it) }.toSet()
    val (givenName, familyName, displayName, photoUrl) = if (options?.includeProfile == true) {
        withContext(Dispatchers.IO) {
            val databaseHelper = DatabaseHelper(context)
            val cursor = databaseHelper.getOwner(account.name)
            try {
                if (cursor.moveToNext()) {
                    listOf(
                        cursor.getColumnIndex("given_name").takeIf { it >= 0 }?.let { cursor.getString(it) },
                        cursor.getColumnIndex("family_name").takeIf { it >= 0 }?.let { cursor.getString(it) },
                        cursor.getColumnIndex("display_name").takeIf { it >= 0 }?.let { cursor.getString(it) },
                        cursor.getColumnIndex("avatar").takeIf { it >= 0 }?.let { cursor.getString(it) },
                    )
                } else listOf(null, null, null, null)
            } finally {
                cursor.close()
                databaseHelper.close()
            }
        }
    } else listOf(null, null, null, null)
    SignInConfigurationService.setDefaultAccount(context, packageName, account)
    return GoogleSignInAccount(
        id,
        tokenId,
        account.name,
        displayName,
        photoUrl?.let { Uri.parse(it) },
        serverAuthCode,
        expirationTime,
        obfuscatedIdentifier,
        ArrayList(grantedScopes),
        givenName,
        familyName
    ).apply {
        Log.d(TAG, "buildGoogleSignInAccount end -> ${toString()}")
    }
}

fun checkTokenExpTime(
    packageName: String,
    account: Account,
    options: GoogleSignInOptions?,
    tokens: MutableMap<String, String>,
    authManager: AuthManager
): Boolean {
    Log.d(TAG, "checkTokenExpTime start")

    val idTokenService = idTokenSaveServerService(options)

    var flag = with(idTokenService) {
        val buildTokenKey = authManager.buildTokenKey(packageName, this)
        val peekAuthToken = authManager.peekAuthToken(account, buildTokenKey)
        if (peekAuthToken.isNullOrEmpty()) {
            Log.d(TAG, "checkTokenExpTime idTokenService peekAuthToken isEmpty ")
            return@with true
        }
        val buildExpireKey = authManager.buildExpireKey(buildTokenKey)
        val userData = authManager.getUserData(account, buildExpireKey)
        if (userData.isNullOrEmpty()) {
            Log.d(TAG, "checkTokenExpTime idTokenService userData isNullOrEmpty ")
            return@with true
        }
        val expTime = userData.toLong()
        val time = Date().time
        if (time > expTime) {
            Log.d(TAG, "checkTokenExpTime idTokenService time > expTime ")
            return@with true
        } else {
            tokens[Constants.AUDIENCE_TOKEN] = peekAuthToken
            tokens[Constants.EXP_TIME] = userData
        }
        return@with false
    }

    if (options?.isServerAuthCodeRequested == false) {
        Log.d(TAG, "checkTokenExpTime isServerAuthCodeRequested false end flag -> $flag")
        return flag
    }

    val authTokenService = authTokenSaveServerService(options)

    flag = with(authTokenService) {
        val buildTokenKey = authManager.buildTokenKey(packageName, this)
        val peekAuthToken = authManager.peekAuthToken(account, buildTokenKey)
        if (peekAuthToken.isNullOrEmpty()) {
            Log.d(TAG, "checkTokenExpTime authTokenService peekAuthToken isEmpty ")
            return@with true
        }
        val buildExpireKey = authManager.buildExpireKey(buildTokenKey)
        val userData = authManager.getUserData(account, buildExpireKey)
        if (userData.isNullOrEmpty()) {
            Log.d(TAG, "checkTokenExpTime authTokenService userData isEmpty ")
            return@with true
        }
        val expTime = userData.toLong()
        val time = Date().time
        if (time > expTime) {
            Log.d(TAG, "checkTokenExpTime authTokenService time > expTime ")
            return@with true
        } else {
            tokens[Constants.OAUTH2_TOKEN] = peekAuthToken
        }
        return@with false
    }

    Log.d(TAG, "checkTokenExpTime end flag -> $flag")
    return flag
}

fun performSignOut(context: Context, packageName: String, options: GoogleSignInOptions?, account: Account) {
    Log.d(TAG, "performSignOut -> ${account.name}")
    AuthServiceManager.instance.getAuthManager(context, packageName, account, oAuthServerService(options)).let {
        it.isPermitted = false
        it.invalidateAuthToken()
    }
    AuthServiceManager.instance.getAuthManager(context, packageName, account, idTokenServerService(options)).let {
        it.isPermitted = false
        it.invalidateAuthToken()
    }
    AuthServiceManager.instance.getAuthManager(context, packageName, account, authTokenServerService(options)).let {
        it.isPermitted = false
        it.invalidateAuthToken()
    }
}
