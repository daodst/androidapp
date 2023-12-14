

package im.vector.app.features.webview

import android.content.Context
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebView
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivityVectorWebViewBinding
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject


@AndroidEntryPoint
class VectorWebViewActivity : VectorBaseActivity<ActivityVectorWebViewBinding>() {

    override fun getBinding() = ActivityVectorWebViewBinding.inflate(layoutInflater)

    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    val session: Session by lazy {
        activeSessionHolder.getActiveSession()
    }

    override fun initUiAndData() {
        setupToolbar(views.webviewToolbar)
                .allowBack()
        waitingView = views.simpleWebviewLoader

        views.simpleWebview.settings.apply {
            
            javaScriptEnabled = true

            
            useWideViewPort = true
            loadWithOverviewMode = true

            
            builtInZoomControls = true

            
            domStorageEnabled = true

            @Suppress("DEPRECATION")
            allowFileAccessFromFileURLs = true
            @Suppress("DEPRECATION")
            allowUniversalAccessFromFileURLs = true

            displayZoomControls = false
        }

        val cookieManager = android.webkit.CookieManager.getInstance()
        cookieManager.setAcceptThirdPartyCookies(views.simpleWebview, true)

        val url = intent.extras?.getString(EXTRA_URL) ?: return
        val title = intent.extras?.getString(EXTRA_TITLE, USE_TITLE_FROM_WEB_PAGE)
        if (title != USE_TITLE_FROM_WEB_PAGE) {
            setTitle(title)
        }

        val webViewMode = intent.extras?.getSerializable(EXTRA_MODE) as WebViewMode
        val eventListener = webViewMode.eventListener(this, session)
        views.simpleWebview.webViewClient = VectorWebViewClient(eventListener)
        views.simpleWebview.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                if (title == USE_TITLE_FROM_WEB_PAGE) {
                    setTitle(title)
                }
            }
        }
        views.simpleWebview.loadUrl(url)
    }

    

    override fun onBackPressed() {
        if (views.simpleWebview.canGoBack()) {
            views.simpleWebview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_MODE = "EXTRA_MODE"

        private const val USE_TITLE_FROM_WEB_PAGE = ""

        fun getIntent(context: Context,
                      url: String,
                      title: String = USE_TITLE_FROM_WEB_PAGE,
                      mode: WebViewMode = WebViewMode.DEFAULT): Intent {
            return Intent(context, VectorWebViewActivity::class.java)
                    .apply {
                        putExtra(EXTRA_URL, url)
                        putExtra(EXTRA_TITLE, title)
                        putExtra(EXTRA_MODE, mode)
                    }
        }
    }
}
