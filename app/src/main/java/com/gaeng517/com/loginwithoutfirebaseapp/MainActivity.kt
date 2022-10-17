package com.gaeng517.com.loginwithoutfirebaseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.gaeng517.com.loginwithoutfirebaseapp.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivity"
    private val EMAIL = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        registerFacebookLCallbackManager()

        binding.loginButton.setReadPermissions(Arrays.asList(EMAIL))
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    Log.i(TAG, "[registerCallback] login success: $result")

                    updateUI()
                }

                override fun onCancel() {
                    Log.i(TAG, "[registerCallback] login Cancel")
                }

                override fun onError(exception: FacebookException) {
                    Log.e(TAG, "[registerCallback] login Error: ${exception.message}")
                }
            })
    }

    private fun registerFacebookLCallbackManager() {
        callbackManager = create()

        /*LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.i(TAG, "[registerCallback] login success: $loginResult")

                    updateUI()
                }

                override fun onCancel() {
                    Log.i(TAG, "[registerCallback] login Cancel")
                }

                override fun onError(exception: FacebookException) {
                    Log.e(TAG, "[registerCallback] login Error: ${exception.message}")
                }
            })*/
    }

    private fun updateUI() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        binding.accessTokenContents.text = accessToken?.token
        binding.isLoginedContents.text = isLoggedIn.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }

}