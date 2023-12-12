package org.microg.tools

import android.accounts.Account
import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.microg.gms.auth.AuthManager
import org.microg.gms.common.AccountManagerUtils
import org.microg.gms.games.signin.RequestParams
import org.microg.gms.games.signin.callback.ClientConfigCallBackImpl
import org.microg.gms.games.signin.callback.IUpdateCallback
import org.microg.gms.games.signin.utils.GrpcUtils
import java.util.*


class ViewCallbackImpl(activity: Activity, clientPackageName: String, clientPackageNameSignerBase: String,
                       account: Account, requestParams: RequestParams, callback: IUpdateCallback
) {
    private val activity: Activity
    private val clientPackageName: String
    private val clientPackageNameSignerBase: String
    private val account: Account
    private val requestParams: RequestParams
    private val callback: IUpdateCallback

    init {
        this.activity = activity
        this.callback = callback
        this.clientPackageName = clientPackageName
        this.clientPackageNameSignerBase = clientPackageNameSignerBase
        this.account = account
        this.requestParams = requestParams
    }

    fun start() {
        Toast.makeText(activity, "loading....", Toast.LENGTH_SHORT).show()
        GlobalScope.launch(context = Dispatchers.IO) {
            val saveServer = String.format("%s:%s?include_email=%d&include_profile=%d",
                    "audience:server:client_id", requestParams.clientId,
                    requestParams.includeEmail, requestParams.includeProfile)
            val peekAuthToken = AccountManagerUtils.getInstance(activity).peekAuthToken(account, saveServer)
            Log.d(TAG, "ViewCallbackImpl run: $peekAuthToken save_server:$saveServer")
            if (!TextUtils.isEmpty(peekAuthToken)) {
                val authManager = AuthManager(activity, account.name, requestParams.clientPackageName, saveServer);
                val userData = AccountManagerUtils.getInstance(activity).getUserData(account,
                        authManager.buildExpireKey())
                val expTime = userData.toLong()
                val time = Date().time
                if (time > expTime) {
                    GrpcUtils.startClientConfigGrpc(GrpcUtils.GET_DISPLAY_BRAND, clientPackageName,
                            clientPackageNameSignerBase, ClientConfigCallBackImpl(activity, callback)
                    )
                } else {
                    callback.onSuccess()
                }
            } else {
                GrpcUtils.startClientConfigGrpc(GrpcUtils.GET_DISPLAY_BRAND, clientPackageName,
                        clientPackageNameSignerBase, ClientConfigCallBackImpl(activity, callback))
            }
        }
    }

    private companion object {
        val TAG: String = ViewCallbackImpl::class.java.simpleName
    }
}