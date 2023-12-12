package org.microg.tools

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.microg.gms.auth.AuthConstants
import org.microg.gms.common.AccountManagerUtils
import org.microg.gms.common.Constants
import org.microg.gms.games.signin.RequestParams
import org.microg.gms.games.signin.callback.UpdateCallbackImpl
import org.microg.tools.ui.CircleTextView


class AccountSelectionActivity : AppCompatActivity() {
    private lateinit var ivLogo: ImageView
    private lateinit var tvPlatform: TextView
    private lateinit var tvInfo: TextView
    private lateinit var llAdd: LinearLayout
    private lateinit var accountsListAdapter: AccountsListAdapter
    private lateinit var accounts: MutableList<Account>
    private lateinit var accountsListView: ListView
    private var appName: String = ""
    private var appIcon: Drawable? = null
    private var clientPackageName: String? = ""
    private var clientPackageNameSignerBase: String? = ""
    private lateinit var requestParams: RequestParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(true)
        setContentView(R.layout.activity_account_selection)
        initView()
        initParams()
        getAccountData()
    }

    private fun initParams() {
        requestParams = RequestParams()
        requestParams.initData(this, intent)
        clientPackageName = requestParams.clientPackageName
        clientPackageNameSignerBase = requestParams.clientPackageNameSignerBase
        tvInfo.text = resources.getString(R.string.protobuf2)
        getAppInfo(clientPackageName)
    }

    private fun getAppInfo(packageName: String?) {
        if (packageName.isNullOrEmpty()) {
            return
        }
        try {
            val packageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES)
            val applicationInfo = packageInfo?.applicationInfo
            appName = packageManager.getApplicationLabel(applicationInfo!!).toString()
            appIcon = packageManager.getApplicationIcon(applicationInfo)
            ivLogo.setImageDrawable(appIcon)
            tvPlatform.text = tvPlatform.text.toString()
                .replace(resources.getString(R.string.platform_placeholder), appName)
            tvInfo.text = tvInfo.text.toString()
                .replace(resources.getString(R.string.platform_placeholder), appName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getAccountData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val accountManager = AccountManager.get(this@AccountSelectionActivity)
            val ac = accountManager.getAccountsByType(AuthConstants.DEFAULT_ACCOUNT_TYPE)
            if (ac.isNotEmpty()) {
                accounts.clear()
                accounts.addAll(ac)
                withContext(Dispatchers.Main) {
                    accountsListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initView() {
        ivLogo = findViewById(R.id.iv_logo)
        tvPlatform = findViewById(R.id.tv_platform)
        tvInfo = findViewById(R.id.tv_info)
        accountsListView = findViewById(R.id.accounts_lv)
        llAdd = findViewById(R.id.ll_add_account)
        accounts = arrayListOf()

        accountsListAdapter = AccountsListAdapter()
        accountsListView.adapter = accountsListAdapter
        accountsListView.setOnItemClickListener { _, _, position, _ ->
            val account = accounts[position]
            val intent = Intent("com.mg.action.account.receiver")
            intent.putExtra("account", account)
            sendBroadcast(intent)
            AccountManagerUtils.getInstance(this)
                    .saveDefaultAccount(requestParams.packageName, account)
            if (!clientPackageName.isNullOrEmpty() && !clientPackageNameSignerBase.isNullOrEmpty()) {
                val viewCallbackImpl = ViewCallbackImpl(
                    this@AccountSelectionActivity,
                    clientPackageName!!,
                    clientPackageNameSignerBase!!,
                    account!!,
                    requestParams,
                    UpdateCallbackImpl(this@AccountSelectionActivity, requestParams, account)
                )
                viewCallbackImpl.start()
            }
        }

    }

    fun addOneMoreAccount(view: View) {
        val intent = Intent().apply {
            action = "com.google.android.gms.auth.login.LOGIN"
            setPackage(Constants.GMS_PACKAGE_NAME)
        }
        startActivityForResult(intent, 1005)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1005) {
            getAccountData()
        }
    }

    private inner class AccountsListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return accounts.size
        }

        override fun getItem(position: Int): Account {
            return accounts[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_account, parent, false)

            val account:Account = getItem(position)

            val circleTextView = itemView.findViewById<CircleTextView>(R.id.account_icon)
            circleTextView.setRandomBackgroundColor()

            val firstName =
                AccountManagerUtils.getInstance(this@AccountSelectionActivity)
                    .getUserData(account, "firstName")
            val lastName = AccountManagerUtils.getInstance(this@AccountSelectionActivity)
                .getUserData(account, "lastName")
            val nameSb = StringBuilder(lastName)
            nameSb.append(firstName)

            if (lastName != null && !TextUtils.isEmpty(lastName)) {
                circleTextView.text = lastName
            } else if (firstName != null && !TextUtils.isEmpty(firstName)) {
                circleTextView.text = firstName
            }

            circleTextView.setTextColor(resources.getColor(android.R.color.white))

            val accountNameTv = itemView.findViewById<TextView>(R.id.account_name)
            accountNameTv.text = nameSb

            val accountEmailTv = itemView.findViewById<TextView>(R.id.account_email)
            accountEmailTv.text = account.name

            return itemView
        }

    }
}