

package im.vector.app.features.widgets.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import im.vector.app.R
import im.vector.app.features.themes.ThemeUtils
import im.vector.app.features.webview.VectorWebViewClient
import im.vector.app.features.webview.WebViewEventListener

@SuppressLint("NewApi")
fun WebView.setupForWidget(webViewEventListener: WebViewEventListener) {
    
    setBackgroundColor(ThemeUtils.getColor(context, R.attr.colorSurface))

    
    clearHistory()
    clearFormData()

    
    settings.javaScriptEnabled = true

    
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true

    
    settings.builtInZoomControls = true

    
    settings.domStorageEnabled = true

    @Suppress("DEPRECATION")
    settings.allowFileAccessFromFileURLs = true
    @Suppress("DEPRECATION")
    settings.allowUniversalAccessFromFileURLs = true

    settings.displayZoomControls = false

    
    webChromeClient = object : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest) {
            WebviewPermissionUtils.promptForPermissions(R.string.room_widget_resource_permission_title, request, context)
        }
    }
    webViewClient = VectorWebViewClient(webViewEventListener)

    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptThirdPartyCookies(this, false)
}

fun WebView.clearAfterWidget() {
    
    (parent as? ViewGroup)?.removeAllViews()
    webChromeClient = null
    clearHistory()
    
    loadUrl("about:blank")
    removeAllViews()
    destroy()
}
