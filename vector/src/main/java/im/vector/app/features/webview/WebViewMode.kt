

package im.vector.app.features.webview

import im.vector.app.core.platform.VectorBaseActivity
import org.matrix.android.sdk.api.session.Session


enum class WebViewMode : WebViewEventListenerFactory {

    DEFAULT {
        override fun eventListener(activity: VectorBaseActivity<*>, session: Session): WebViewEventListener {
            return DefaultWebViewEventListener()
        }
    },
    CONSENT {
        override fun eventListener(activity: VectorBaseActivity<*>, session: Session): WebViewEventListener {
            return ConsentWebViewEventListener(activity, session, DefaultWebViewEventListener())
        }
    };
}
