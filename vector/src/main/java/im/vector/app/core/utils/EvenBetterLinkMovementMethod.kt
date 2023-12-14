

package im.vector.app.core.utils

import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.widget.TextView
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class EvenBetterLinkMovementMethod(private val onLinkClickListener: OnLinkClickListener? = null) : BetterLinkMovementMethod() {

    interface OnLinkClickListener {
        
        fun onLinkClicked(textView: TextView, span: ClickableSpan, url: String, actualText: String): Boolean
    }

    override fun dispatchUrlClick(textView: TextView, clickableSpan: ClickableSpan) {
        val spanned = textView.text as Spanned
        val actualText = textView.text.subSequence(spanned.getSpanStart(clickableSpan), spanned.getSpanEnd(clickableSpan)).toString()
        val url = (clickableSpan as? URLSpan)?.url ?: actualText

        if (onLinkClickListener == null || !onLinkClickListener.onLinkClicked(textView, clickableSpan, url, actualText)) {
            
            clickableSpan.onClick(textView)
        }
    }
}
