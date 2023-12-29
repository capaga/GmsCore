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

import android.util.Log
import org.microg.gms.common.HttpFormClient.ResponseField

class AuthResponse {
    @JvmField
    @ResponseField("SID")
    var Sid: String? = null

    @JvmField
    @ResponseField("LSID")
    var LSid: String? = null

    @JvmField
    @ResponseField("Auth")
    var auth: String? = null

    @JvmField
    @ResponseField("Token")
    var token: String? = null

    @JvmField
    @ResponseField("Email")
    var email: String? = null

    @JvmField
    @ResponseField("services")
    var services: String? = null

    @ResponseField("GooglePlusUpgrade")
    var isGooglePlusUpgrade = false

    @ResponseField("PicasaUser")
    var picasaUserName: String? = null

    @ResponseField("RopText")
    var ropText: String? = null

    @ResponseField("RopRevision")
    var ropRevision = 0

    @JvmField
    @ResponseField("firstName")
    var firstName: String? = null

    @JvmField
    @ResponseField("lastName")
    var lastName: String? = null

    @ResponseField("issueAdvice")
    var issueAdvice: String? = null

    @JvmField
    @ResponseField("accountId")
    var accountId: String? = null

    @JvmField
    @ResponseField("Expiry")
    var expiry: Long = -1

    @ResponseField("storeConsentRemotely")
    var storeConsentRemotely = true

    @ResponseField("Permission")
    var permission: String? = null

    @ResponseField("ScopeConsentDetails")
    var scopeConsentDetails: String? = null

    @JvmField
    @ResponseField("ConsentDataBase64")
    var consentDataBase64: String? = null

    @ResponseField("grantedScopes")
    var grantedScopes: String? = null

    @ResponseField("itMetadata")
    var itMetadata: String? = null

    @JvmField
    @ResponseField("ResolutionDataBase64")
    var resolutionDataBase64: String? = null

    @ResponseField("it")
    var auths: String? = null
    override fun toString(): String {
        val sb = StringBuilder("AuthResponse{")
        sb.append("auth='").append(auth).append('\'')
        if (Sid != null) sb.append(", Sid='").append(Sid).append('\'')
        if (LSid != null) sb.append(", LSid='").append(LSid).append('\'')
        if (token != null) sb.append(", token='").append(token).append('\'')
        if (email != null) sb.append(", email='").append(email).append('\'')
        if (services != null) sb.append(", services='").append(services).append('\'')
        if (isGooglePlusUpgrade) sb.append(", isGooglePlusUpgrade=").append(isGooglePlusUpgrade)
        if (picasaUserName != null) sb.append(", picasaUserName='").append(picasaUserName).append('\'')
        if (ropText != null) sb.append(", ropText='").append(ropText).append('\'')
        if (ropRevision != 0) sb.append(", ropRevision=").append(ropRevision)
        if (firstName != null) sb.append(", firstName='").append(firstName).append('\'')
        if (lastName != null) sb.append(", lastName='").append(lastName).append('\'')
        if (issueAdvice != null) sb.append(", issueAdvice='").append(issueAdvice).append('\'')
        if (accountId != null) sb.append(", accountId='").append(accountId).append('\'')
        if (expiry != -1L) sb.append(", expiry=").append(expiry)
        if (!storeConsentRemotely) sb.append(", storeConsentRemotely=").append(storeConsentRemotely)
        if (permission != null) sb.append(", permission='").append(permission).append('\'')
        if (scopeConsentDetails != null) sb.append(", scopeConsentDetails='").append(scopeConsentDetails).append('\'')
        if (consentDataBase64 != null) sb.append(", consentDataBase64='").append(consentDataBase64).append('\'')
        if (auths != null) sb.append(", auths='").append(auths).append('\'')
        sb.append('}')
        return sb.toString()
    }

    companion object {
        private const val TAG = "GmsAuthResponse"
        fun parse(result: String): AuthResponse {
            val response = AuthResponse()
            val entries = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in entries) {
                val keyValuePair = s.split("=".toRegex(), limit = 2).toTypedArray()
                val key = keyValuePair[0].trim { it <= ' ' }
                val value = keyValuePair[1].trim { it <= ' ' }
                try {
                    for (field in AuthResponse::class.java.declaredFields) {
                        if (field.isAnnotationPresent(ResponseField::class.java) && key == field.getAnnotation(ResponseField::class.java).value) {
                            when (field.type) {
                                String::class.java -> {
                                    field[response] = value
                                }
                                Boolean::class.javaPrimitiveType -> {
                                    field.setBoolean(response, value == "1")
                                }
                                Long::class.javaPrimitiveType -> {
                                    field.setLong(response, value.toLong())
                                }
                                Int::class.javaPrimitiveType -> {
                                    field.setInt(response, value.toInt())
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, e)
                }
            }
            return response
        }
    }
}