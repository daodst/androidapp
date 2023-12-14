

package im.vector.app.features.html

import android.text.Spanned
import android.text.style.MetricAffectingSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import im.vector.app.EmojiSpanify
import im.vector.app.features.home.room.detail.timeline.item.BindingOptions
import javax.inject.Inject

class SpanUtils @Inject constructor(
        private val emojiSpanify: EmojiSpanify
) {
    fun getBindingOptions(charSequence: CharSequence): BindingOptions {
        val emojiCharSequence = emojiSpanify.spanify(charSequence)

        if (emojiCharSequence !is Spanned) {
            return BindingOptions()
        }

        return BindingOptions(
                canUseTextFuture = canUseTextFuture(emojiCharSequence)
        )
    }

    
    private fun canUseTextFuture(spanned: Spanned): Boolean {
        return spanned
                .getSpans(0, spanned.length, Any::class.java)
                .all { it !is StrikethroughSpan && it !is UnderlineSpan && it !is MetricAffectingSpan }
    }
}
