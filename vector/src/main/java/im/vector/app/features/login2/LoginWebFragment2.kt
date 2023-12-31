

@file:Suppress("DEPRECATION")

package im.vector.app.features.login2

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.airbnb.mvrx.activityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.utils.AssetReader
import im.vector.app.databinding.FragmentLoginWebBinding
import im.vector.app.features.login.JavascriptResponse
import im.vector.app.features.signout.soft.SoftLogoutAction
import im.vector.app.features.signout.soft.SoftLogoutViewModel
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.util.MatrixJsonParser
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject


class LoginWebFragment2 @Inject constructor(
        private val assetReader: AssetReader
) : AbstractLoginFragment2<FragmentLoginWebBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginWebBinding {
        return FragmentLoginWebBinding.inflate(inflater, container, false)
    }

    private val softLogoutViewModel: SoftLogoutViewModel by activityViewModel()

    private var isWebViewLoaded = false
    private var isForSessionRecovery = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(views.loginWebToolbar)
                .allowBack()
    }

    override fun updateWithState(state: LoginViewState2) {
        setupTitle(state)

        isForSessionRecovery = state.deviceId?.isNotBlank() == true

        if (!isWebViewLoaded) {
            setupWebView(state)
            isWebViewLoaded = true
        }
    }

    private fun setupTitle(state: LoginViewState2) {
        toolbar?.title = when (state.signMode) {
            SignMode2.SignIn -> getString(R.string.login_signin)
            else             -> getString(R.string.login_signup)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(state: LoginViewState2) {
        views.loginWebWebView.settings.javaScriptEnabled = true

        
        views.loginWebWebView.settings.domStorageEnabled = true
        views.loginWebWebView.settings.databaseEnabled = true

        
        
        views.loginWebWebView.settings.userAgentString = "Mozilla/5.0 Google"

        
        val cookieManager = android.webkit.CookieManager.getInstance()

        
        if (cookieManager == null) {
            launchWebView(state)
        } else {
            if (!cookieManager.hasCookies()) {
                launchWebView(state)
            } else {
                try {
                    cookieManager.removeAllCookies { launchWebView(state) }
                } catch (e: Exception) {
                    Timber.e(e, " cookieManager.removeAllCookie() fails")
                    launchWebView(state)
                }
            }
        }
    }

    private fun launchWebView(state: LoginViewState2) {
        val url = loginViewModel.getFallbackUrl(state.signMode == SignMode2.SignIn, state.deviceId) ?: return

        views.loginWebWebView.loadUrl(url)

        views.loginWebWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler,
                                            error: SslError) {
                MaterialAlertDialogBuilder(requireActivity())
                        .setMessage(R.string.ssl_could_not_verify)
                        .setPositiveButton(R.string.ssl_trust) { _, _ -> handler.proceed() }
                        .setNegativeButton(R.string.ssl_do_not_trust) { _, _ -> handler.cancel() }
                        .setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                                handler.cancel()
                                dialog.dismiss()
                                return@OnKeyListener true
                            }
                            false
                        })
                        .setCancelable(false)
                        .show()
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)

                loginViewModel.handle(LoginAction2.PostViewEvent(LoginViewEvents2.OnWebLoginError(errorCode, description, failingUrl)))
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                toolbar?.subtitle = url
            }

            override fun onPageFinished(view: WebView, url: String) {
                
                if (url.startsWith("http")) {
                    
                    assetReader.readAssetFile("sendObject.js")?.let { view.loadUrl(it) }

                    if (state.signMode == SignMode2.SignIn) {
                        
                        assetReader.readAssetFile("onLogin.js")?.let { view.loadUrl(it) }
                    } else {
                        
                        
                        assetReader.readAssetFile("onRegistered.js")?.let { view.loadUrl(it) }
                    }
                }
            }

            
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                if (url == null) return super.shouldOverrideUrlLoading(view, url as String?)

                if (url.startsWith("js:")) {
                    var json = url.substring(3)
                    var javascriptResponse: JavascriptResponse? = null

                    try {
                        
                        json = URLDecoder.decode(json, "UTF-8")
                        val adapter = MatrixJsonParser.getMoshi().adapter(JavascriptResponse::class.java)
                        javascriptResponse = adapter.fromJson(json)
                    } catch (e: Exception) {
                        Timber.e(e, "## shouldOverrideUrlLoading() : fromJson failed")
                    }

                    
                    if (javascriptResponse != null) {
                        val action = javascriptResponse.action

                        if (state.signMode == SignMode2.SignIn) {
                            if (action == "onLogin") {
                                javascriptResponse.credentials?.let { notifyViewModel(it) }
                            }
                        } else {
                            
                            
                            if (action == "onRegistered") {
                                javascriptResponse.credentials?.let { notifyViewModel(it) }
                            }
                        }
                    }
                    return true
                }

                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    private fun notifyViewModel(credentials: Credentials) {
        if (isForSessionRecovery) {
            softLogoutViewModel.handle(SoftLogoutAction.WebLoginSuccess(credentials))
        } else {
            loginViewModel.handle(LoginAction2.WebLoginSuccess(credentials))
        }
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction2.ResetSignin)
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        return when {
            toolbarButton                     -> super.onBackPressed(toolbarButton)
            views.loginWebWebView.canGoBack() -> views.loginWebWebView.goBack().run { true }
            else                              -> super.onBackPressed(toolbarButton)
        }
    }
}
