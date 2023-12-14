

package im.vector.app.features.webview

import im.vector.app.core.platform.VectorBaseActivity
import org.matrix.android.sdk.api.session.Session

interface WebViewEventListenerFactory {

    
    fun eventListener(activity: VectorBaseActivity<*>, session: Session): WebViewEventListener
}
