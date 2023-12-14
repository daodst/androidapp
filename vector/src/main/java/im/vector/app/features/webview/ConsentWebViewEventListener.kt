

package im.vector.app.features.webview

import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.utils.weak
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber

private const val SUCCESS_URL_SUFFIX = "/_matrix/consent"
private const val RIOT_BOT_ID = "@riot-bot:matrix.org"


class ConsentWebViewEventListener(activity: VectorBaseActivity<*>,
                                  private val session: Session,
                                  private val delegate: WebViewEventListener) :
        WebViewEventListener by delegate {

    private val safeActivity: VectorBaseActivity<*>? by weak(activity)

    override fun onPageFinished(url: String) {
        delegate.onPageFinished(url)
        if (url.endsWith(SUCCESS_URL_SUFFIX)) {
            createRiotBotRoomIfNeeded()
        }
    }

    
    private fun createRiotBotRoomIfNeeded() {
        safeActivity?.let {
            
            it.finish()
            
        }
    }

    
    private val createRiotBotRoomCallback = object : MatrixCallback<String> {
        override fun onSuccess(data: String) {
            Timber.d("## On success : succeed to invite riot-bot")
            safeActivity?.finish()
        }

        override fun onFailure(failure: Throwable) {
            Timber.e("## On error : failed  to invite riot-bot $failure")
            safeActivity?.finish()
        }
    }
}
