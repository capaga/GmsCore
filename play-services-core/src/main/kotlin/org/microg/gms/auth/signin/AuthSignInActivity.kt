package org.microg.gms.auth.signin

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.R
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.SignInAccount
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.databinding.SigninConfirmBinding
import com.google.android.gms.databinding.SigninPickerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.microg.gms.auth.AuthConstants
import org.microg.gms.auth.login.LoginActivity
import org.microg.gms.common.Constants
import org.microg.gms.common.Utils
import org.microg.gms.people.DatabaseHelper
import org.microg.gms.people.PeopleManager
import org.microg.gms.utils.getApplicationLabel
import org.microg.tools.ui.CircleTextView

private const val TAG = "AuthSignInActivity"

private const val REQUEST_CODE_ACCOUNT_ADD = 1000

class AuthSignInActivity : AppCompatActivity() {

    private lateinit var accountManager: AccountManager

    private val config: SignInConfiguration?
        get() = runCatching {
            intent?.extras?.also { it.classLoader = SignInConfiguration::class.java.classLoader }?.getParcelable<SignInConfiguration>("config")
        }.getOrNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(CommonStatusCodes.CANCELED)
        Log.d(TAG, "Begin AuthSignIn packageName-> ${config?.packageName} action-> ${intent?.action} ")
        Log.d(TAG, "Config: $config")

        val packageName = config?.packageName
        if (packageName == null || (packageName != callingActivity?.packageName && callingActivity?.packageName != this.packageName)) return finishResult(
            CommonStatusCodes.DEVELOPER_ERROR, "package name mismatch"
        )
        accountManager = getSystemService<AccountManager>() ?: return finishResult(
            CommonStatusCodes.INTERNAL_ERROR, "No account manager"
        )

        val accounts = accountManager.getAccountsByType(AuthConstants.DEFAULT_ACCOUNT_TYPE)
        if (accounts.isNotEmpty()) {
            val account = config?.options?.account
            if (account != null) {
                if (account in accounts) {
                    showSignInConfirm(packageName, account)
                } else {
                    finishResult(CommonStatusCodes.INVALID_ACCOUNT)
                }
            } else {
                openAccountPicker(packageName)
            }
        } else {
            openAddAccount()
        }
    }

    private fun showSignInConfirm(packageName: String, account: Account) {
        val binding = SigninConfirmBinding.inflate(layoutInflater)
        binding.appName = packageManager.getApplicationLabel(packageName).toString()
        binding.appIcon = packageManager.getApplicationIcon(packageName)
        bindAccountRow(binding.root, account) { view, bitmap -> view.setImageBitmap(bitmap) }
        binding.button2.setOnClickListener {
            finishResult(CommonStatusCodes.CANCELED)
        }
        binding.button1.setOnClickListener {
            binding.button1.isEnabled = false
            binding.button2.isEnabled = false
            lifecycleScope.launchWhenStarted {
                try {
                    signIn(account)
                } catch (e: Exception) {
                    Log.w(TAG, e)
                    finishResult(CommonStatusCodes.INTERNAL_ERROR)
                }
            }
        }
        setContentView(binding.root)
    }

    private fun openAccountPicker(packageName: String) {
        val binding = SigninPickerBinding.inflate(layoutInflater)
        binding.appName = packageManager.getApplicationLabel(packageName).toString()
        binding.appIcon = packageManager.getApplicationIcon(packageName)
        val accounts =
            getSystemService<AccountManager>()!!.getAccountsByType(AuthConstants.DEFAULT_ACCOUNT_TYPE) + Account(
                AuthConstants.DEFAULT_ACCOUNT, AuthConstants.DEFAULT_ACCOUNT_TYPE
            )
        binding.pickerList.adapter = object : ArrayAdapter<Account>(this, 0, accounts) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = convertView ?: layoutInflater.inflate(R.layout.signin_account_row, parent, false)
                getItem(position)?.let {
                    if (v.tag != null) { return v }
                    bindAccountRow(v, it) { _, _ -> notifyDataSetChanged() }
                    v.tag = it
                }
                return v
            }
        }
        binding.pickerList.setOnItemClickListener { _, _, position, _ ->
            binding.listProgressSpinner = true
            if (accounts[position].name == AuthConstants.DEFAULT_ACCOUNT) {
                openAddAccount()
            } else {
                lifecycleScope.launchWhenStarted {
                    try {
                        signIn(accounts[position])
                    } catch (e: Exception) {
                        Log.d(TAG, "signIn exception -> ${e.message}")
                        finishResult(CommonStatusCodes.INTERNAL_ERROR)
                    }
                }
            }
        }
        setContentView(binding.root)
    }

    private fun openAddAccount() {
        Intent(this, LoginActivity::class.java).apply {
            `package` = Constants.GMS_PACKAGE_NAME
        }.run {
            startActivityForResult(this, REQUEST_CODE_ACCOUNT_ADD)
        }
    }

    private fun bindAccountRow(root: View, account: Account, updateAction: (ImageView, Bitmap) -> Unit) {
        val photoView = root.findViewById<ImageView>(R.id.account_photo)
        val displayNameView = root.findViewById<TextView>(R.id.account_display_name)
        val emailView = root.findViewById<TextView>(R.id.account_email)
        val photoIcon = root.findViewById<CircleTextView>(R.id.account_icon)
        if (account.name != AuthConstants.DEFAULT_ACCOUNT) {
            lifecycleScope.launchWhenStarted {
                val photo = PeopleManager.getOwnerAvatarBitmap(this@AuthSignInActivity, account.name, false)
                if (photo == null) {
                    withContext(Dispatchers.IO) {
                        PeopleManager.getOwnerAvatarBitmap(this@AuthSignInActivity, account.name, true)
                    }?.let {
                        updateAction(photoView, it)
                    }
                }
                if (photo == null) {
                    photoIcon.visibility = View.VISIBLE
                    photoView.visibility = View.GONE
                    photoIcon.setRandomBackgroundColor()
                } else {
                    photoView.setImageBitmap(photo)
                    photoView.visibility = View.VISIBLE
                    photoIcon.visibility = View.GONE
                }
                var displayName = withContext(Dispatchers.IO) {
                    val databaseHelper = DatabaseHelper(this@AuthSignInActivity)
                    val cursor = databaseHelper.getOwner(account.name)
                    try {
                        if (cursor.moveToNext()) {
                            cursor.getColumnIndex("display_name").takeIf { it >= 0 }?.let { cursor.getString(it) }
                                .takeIf { !it.isNullOrBlank() }
                        } else null
                    } finally {
                        cursor.close()
                        databaseHelper.close()
                    }
                }

                val lastName = AccountManager.get(this@AuthSignInActivity).getUserData(account, "lastName")
                val firstName = AccountManager.get(this@AuthSignInActivity).getUserData(account, "firstName")
                if (displayName == null){
                    displayName = StringBuilder(lastName).append(firstName).toString()
                }

                val photoText = if(!firstName.isNullOrEmpty()){
                    firstName
                } else if(!lastName.isNullOrEmpty()){
                    lastName
                } else{
                    account.name.substring(0)
                }
                photoIcon.text = photoText

                displayNameView.text = displayName
                emailView.text = account.name
                emailView.visibility = View.VISIBLE
            }
        } else {
            photoIcon.visibility = View.GONE
            photoView.setImageResource(R.drawable.add_account)
            displayNameView.setText(R.string.signin_picker_add_account_label)
            emailView.visibility = View.GONE
        }
    }

    private suspend fun signIn(account: Account) {
        val googleSignInAccount = performSignIn(this, config?.packageName!!, config?.options, account, true)
        if (googleSignInAccount != null) {
            finishResult(CommonStatusCodes.SUCCESS, account = account, googleSignInAccount = googleSignInAccount)
        } else {
            finishResult(CommonStatusCodes.INTERNAL_ERROR, "Sign in failed")
        }
    }

    private fun finishResult(
        statusCode: Int,
        message: String? = null,
        account: Account? = null,
        googleSignInAccount: GoogleSignInAccount? = null
    ) {
        Log.d(
            TAG,
            "finishResult -> statusCode:$statusCode message:$message ${account.toString()} ${googleSignInAccount.toString()}"
        )
        val data = Intent()
        if (statusCode != CommonStatusCodes.SUCCESS) data.putExtra(AuthConstants.ERROR_CODE, statusCode)
        data.putExtra(AuthConstants.GOOGLE_SIGN_IN_STATUS, Status(statusCode, message))
        data.putExtra(AuthConstants.GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount)
        val bundle = Bundle()
        if (googleSignInAccount != null) {
            SignInAccount().apply {
                email= googleSignInAccount.email ?: account?.name
                this.googleSignInAccount = googleSignInAccount
                userId = googleSignInAccount.id ?: accountManager.getUserData(account, AuthConstants.GOOGLE_USER_ID)
            }.let {
                data.putExtra(AuthConstants.SIGN_IN_ACCOUNT, it)
            }
            SignInCredential().apply {
                setEmail(googleSignInAccount.email)
                setAccountName(googleSignInAccount.displayName)
                setFirstName(googleSignInAccount.familyName)
                setLastName(googleSignInAccount.givenName)
                setAuthToken(googleSignInAccount.idToken)
            }.let {
                bundle.putByteArray(AuthConstants.SIGN_IN_CREDENTIAL, Utils.safeParcelableInstanceToBytesArray(it))
                bundle.putByteArray(AuthConstants.STATUS, Utils.safeParcelableInstanceToBytesArray(Status.SUCCESS))
            }
        } else {
            bundle.putByteArray(AuthConstants.STATUS, Utils.safeParcelableInstanceToBytesArray(Status.CANCELED))
        }
        data.putExtras(bundle)
        Log.d(TAG, "Result: ${data.extras?.also { it.keySet() }}")
        setResult(if (statusCode == CommonStatusCodes.SUCCESS) RESULT_OK else statusCode, data)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ACCOUNT_ADD) {
            val accounts = accountManager.getAccountsByType(AuthConstants.DEFAULT_ACCOUNT_TYPE)
            if (accounts.isNotEmpty()) {
                openAccountPicker(config?.packageName!!)
            } else {
                finishResult(CommonStatusCodes.CANCELED, "No account and creation cancelled")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "End AuthSignIn packageName-> ${config?.packageName} ")
    }

}