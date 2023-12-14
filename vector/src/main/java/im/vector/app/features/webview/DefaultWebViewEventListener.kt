

package im.vector.app.features.webview

import timber.log.Timber



class DefaultWebViewEventListener : WebViewEventListener {

    override fun pageWillStart(url: String) {
        Timber.v("On page will start: $url")
    }

    override fun onPageStarted(url: String) {
        Timber.d("On page started: $url")
    }

    override fun onPageFinished(url: String) {
        Timber.d("On page finished: $url")
    }

    override fun onPageError(url: String, errorCode: Int, description: String) {
        Timber.e("On received error: $url - errorCode: $errorCode - message: $description")
    }

    override fun shouldOverrideUrlLoading(url: String): Boolean {
        Timber.v("Should override url: $url")
        return false
    }
}
