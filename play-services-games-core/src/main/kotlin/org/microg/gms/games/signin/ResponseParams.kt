package org.microg.gms.games.signin

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.SignInAccount
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.microg.gms.auth.AuthConstants
import org.microg.gms.common.AccountManagerUtils
import org.microg.gms.common.Constants
import org.microg.gms.games.signin.utils.MD5
import org.microg.gms.people.DatabaseHelper

class ResponseParams {
    private var resultIntent: Intent? = null
    fun requestSdkGoogleSignInfo(activity: Context, account: Account, requestParams: RequestParams, callBack: ResponseCallBack?) {
        Log.d(TAG, "requestSdkGoogleSignInfo: " + requestParams.isIdToken)
        val firstName = AccountManagerUtils.getInstance(activity).getUserData(account, AccountManagerUtils.FIRST_NAME)
        val lastName = AccountManagerUtils.getInstance(activity).getUserData(account, AccountManagerUtils.LAST_NAME)
        val googleUserId = AccountManagerUtils.getInstance(activity).getUserData(account, AccountManagerUtils.GOOGLE_USER_ID)
        val name = lastName + firstName
        val email = account.name
        val encodeId = MD5.dest(googleUserId, requestParams.packageName)
        val peekAuthToken: String? = null
        resultIntent = Intent()
        // 是否是以IDTOKen 登录
        if (requestParams.isIdToken) {
            Log.d(TAG, "requestSdkGoogleSignInfo1: ")
//            peekAuthToken = accountManager.peekAuthToken(account, accountType);
            val authSdkManager = AuthSdkManager(activity)
            CoroutineScope(Dispatchers.IO).launch {
                authSdkManager.getTokenWithAccountSDK(account, requestParams, object :
                    GamesSignInManager.AuthSdkCallBack {
                    override fun success(type: Int, tokens: Map<String, String>) {
                        val googleSignInAccount = detailData(tokens, requestParams,
                            activity, account, googleUserId, name, encodeId, firstName, lastName)

                        Log.d(TAG, "callbackDetail signInAccount:")
                        CoroutineScope(Dispatchers.Main).launch {
                            callbackDetail(googleSignInAccount, activity, callBack)
                        }
                    }

                    override fun onError(msg: String) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            if (activity is Activity) {
                                activity.setResult(Activity.RESULT_OK, resultIntent)
                                activity.finish()
                            }
                        }
                    }
                })
            }
            Thread {

            }.start()
        } else {
            val googleSignInAccount = createGoogleSignInAccount(googleUserId, peekAuthToken,
                email, name, lastName, firstName, encodeId, requestParams.list)
            callbackDetail(googleSignInAccount, activity, callBack)
        }
    }

    interface ResponseCallBack {
        @Throws(RemoteException::class)
        fun result(signInAccount: SignInAccount?)
    }

    private fun callbackDetail(googleSignInAccount: GoogleSignInAccount, context: Context, callBack: ResponseCallBack?) {
        val signInAccount = SignInAccount(AuthConstants.DEFAULT_ACCOUNT, googleSignInAccount, AuthConstants.DEFAULT_USER_ID)
        resultIntent!!.putExtra(GamesSignInActivity.KEY_SIGN_IN_ACCOUNT, signInAccount)
        Log.d(TAG, "callbackDetail signInAccount:xxxxxx")
        Log.d(TAG, "callbackDetail signInAccount:" + googleSignInAccount)
        if (context is Activity) {
            Log.d(TAG, "callbackDetail is activity")
            context.setResult(Activity.RESULT_OK, resultIntent)
            context.finish()
        }
        if (callBack != null) {
            try {
                callBack.result(signInAccount)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun detailData(tokens: Map<String, String>, requestParams: RequestParams,
                           context: Context, account: Account, googleUserId: String,
                           name: String, encodeId: String, firstName: String, lastName: String): GoogleSignInAccount {
        val audienceToken = tokens[Constants.AUDIENCE_TOKEN]
        val oauth2token = tokens[Constants.OAUTH2_TOKEN]
        val expTime = tokens[Constants.EXP_TIME]
        val email = account.name
        Log.d(TAG, "type peekAuthToken::$audienceToken")
        Log.d(TAG, "type oauth2token::$oauth2token")
        val googleSignInAccount: GoogleSignInAccount
        var avatarUrl: Uri? = null
        if (requestParams.isOauthToPrompt) {
            val time = expTime!!.toLong()
            val databaseHelper = DatabaseHelper(context)
            val owner = databaseHelper.getOwner(account.name)
            if (owner.count != 0) {
                if (owner.moveToFirst()) {
                    val avatarCount = owner.getColumnIndex("avatar")
                    avatarUrl = Uri.parse(owner.getString(avatarCount))
                }
            }
            owner.close()
            Log.d(TAG, "avatar::$avatarUrl")
            googleSignInAccount = GoogleSignInAccount(googleUserId, audienceToken,
                email, name, avatarUrl, oauth2token, time, encodeId,
                ArrayList(requestParams.list), firstName, lastName)
        } else {
            googleSignInAccount = createGoogleSignInAccount(googleUserId, audienceToken,
                email, name, lastName, firstName, encodeId, requestParams.list)
        }
        return googleSignInAccount
    }

    private companion object {
        const val TAG = "ResponseParams"
        fun createGoogleSignInAccount(androidId: String?, peekAuth: String?, email: String?,
                                      name: String?, lastName: String?, firstName: String?,
                                      encodeId: String?, options: Set<Scope>?): GoogleSignInAccount {
            val time = System.currentTimeMillis() / 1000
            require(!TextUtils.isEmpty(encodeId)) { "Given String is empty or null" }
            if (options == null) {
                throw NullPointerException("null reference")
            }
            return GoogleSignInAccount(androidId, peekAuth, email, name, null, null, time, encodeId, ArrayList(options), lastName, firstName)
        }
    }
}