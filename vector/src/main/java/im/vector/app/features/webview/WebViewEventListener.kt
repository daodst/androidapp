

package im.vector.app.features.webview

interface WebViewEventListener {

    
    fun pageWillStart(url: String) {
        
    }

    
    fun onPageStarted(url: String) {
        
    }

    
    fun onPageFinished(url: String) {
        
    }

    
    fun onPageError(url: String, errorCode: Int, description: String) {
        
    }

    
    fun onHttpError(url: String, errorCode: Int, description: String) {
        
    }

    
    fun shouldOverrideUrlLoading(url: String): Boolean {
        return false
    }
}
