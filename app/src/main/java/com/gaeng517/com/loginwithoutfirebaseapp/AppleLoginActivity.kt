package com.gaeng517.com.loginwithoutfirebaseapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*


class AppleLoginActivity : AppCompatActivity() {
    private var appleAuthURLFull: String? = null
    private var appleLoginDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apple_login)
        val state = UUID.randomUUID().toString()
        appleAuthURLFull =
            "$AUTHURL?response_type=code&v=1.1.6&response_mode=form_post&client_id=$CLIENT_ID&scope=$SCOPE&state=$state&redirect_uri=$REDIRECT_URI"

        val appleLogin = findViewById<Button>(R.id.appleLogin)

        appleLogin.setOnClickListener { view: View? ->
            Log.i("TAG", "onCreate: Auth url $appleAuthURLFull")
            openWebViewDialog(appleAuthURLFull)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebViewDialog(url: String?) {
        appleLoginDialog = Dialog(this)
        val webView = WebView(this)
        webView.isVerticalFadingEdgeEnabled
        webView.isHorizontalScrollBarEnabled = false
        webView.isVerticalScrollBarEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.webViewClient = AppleLoginWebView()
        }
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url!!)
        appleLoginDialog!!.setContentView(webView)
        appleLoginDialog!!.show()
    }

    //FIXME. 로그인 진행 후, 에러 발생 - Unable to process request due to missing initial state. This may happen if browser sessionStorage is inaccessible or accidentally cleared.
    private inner class AppleLoginWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            Log.i("TAG", request.url.toString())
            try {
                val values = getUrlValues(request.url.toString())

                // Get Values Fro URL and use the values as needed
                appleLoginDialog!!.dismiss()
            } catch (e: UnsupportedEncodingException) {
                Log.e("Error", e.message!!)
            }
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.i("TAG", url)
            if (url.startsWith(REDIRECT_URI)) {
                // Close the dialog after getting the authorization code
                if (url.contains("success=")) {
                    appleLoginDialog!!.dismiss()
                }
                return true
            }
            return false
        }

        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            Log.i("TAG", "shouldInterceptRequest: request url is " + request.url.toString())
            try {
                val values = getUrlValues(request.url.toString())
                val email = values["email"]
                val idToken = values["idToken"]
                Log.e("TAG", "email: $email, idToken: $idToken")

                if (values["email"] != null) {
                    Log.d("email", email!!)
                    appleLoginDialog!!.dismiss()
                }
            } catch (e: UnsupportedEncodingException) {
                Log.e("Error", e.message!!)
            }
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            val displayRectangle = Rect()
            val window = window
            window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
            val layoutparms = view.layoutParams
            layoutparms.height = (displayRectangle.height() * 0.9f).toInt()
            view.layoutParams = layoutparms
        }

        @Throws(UnsupportedEncodingException::class)
        fun getUrlValues(url: String): Map<String, String?> {
            val i = url.indexOf("?")
            val paramsMap: MutableMap<String, String?> = HashMap()
            if (i > -1) {
                val searchURL = url.substring(url.indexOf("?") + 1)
                val params = searchURL.split("&").toTypedArray()
                for (param in params) {
                    val temp = param.split("=").toTypedArray()
                    paramsMap[temp[0]] = URLDecoder.decode(temp[1], "UTF-8")
                }
            }
            return paramsMap
        }
    }
}