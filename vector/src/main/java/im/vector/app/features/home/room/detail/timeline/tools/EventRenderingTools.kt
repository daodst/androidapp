

package im.vector.app.features.home.room.detail.timeline.tools

import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.toSpannable
import im.vector.app.core.linkify.VectorLinkify
import im.vector.app.core.utils.EvenBetterLinkMovementMethod
import im.vector.app.core.utils.isValidUrl
import im.vector.app.features.home.room.detail.timeline.TimelineEventController
import im.vector.app.features.html.PillImageSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.session.permalinks.MatrixLinkify
import org.matrix.android.sdk.api.session.permalinks.MatrixPermalinkSpan

fun CharSequence.findPillsAndProcess(scope: CoroutineScope, processBlock: (PillImageSpan) -> Unit) {
    scope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            toSpannable().let { spannable ->
                spannable.getSpans(0, spannable.length, PillImageSpan::class.java)
            }
        }.forEach { processBlock(it) }
    }
}

fun CharSequence.linkify(callback: TimelineEventController.UrlClickCallback?): CharSequence {
    val text = this.toString()
    
    val spannable = SpannableStringBuilder(this)
    MatrixLinkify.addLinks(spannable, object : MatrixPermalinkSpan.Callback {
        override fun onUrlClicked(url: String) {
            callback?.onUrlClicked(url, text)
        }
    })
    VectorLinkify.addLinks(spannable, true)
    return spannable
}

fun createLinkMovementMethod(urlClickCallback: TimelineEventController.UrlClickCallback?): EvenBetterLinkMovementMethod {
    return EvenBetterLinkMovementMethod(object : EvenBetterLinkMovementMethod.OnLinkClickListener {
        override fun onLinkClicked(textView: TextView, span: ClickableSpan, url: String, actualText: String): Boolean {
            
            return url.isValidUrl() && urlClickCallback?.onUrlClicked(url, actualText) == true
        }
    })
            .apply {
                
                setOnLinkLongClickListener { tv, url ->
                    
                    
                    if (url.isValidUrl() && urlClickCallback?.onUrlLongClicked(url) == true) {
                        tv.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
                        true
                    } else {
                        false
                    }
                }
            }
}
