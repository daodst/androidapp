

package im.vector.app.features.home.room.detail.timeline.item

import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.epoxy.onLongClickIgnoringLinks
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence
import org.matrix.android.sdk.api.extensions.orFalse

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageGiftsItem : AbsMessageItem<MessageGiftsItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var bindingOptions: BindingOptions? = null


    override fun bind(holder: Holder) {
        
        super.bind(holder)
        holder.messageView.textSize = 44F
        holder.messageView.gravity = Gravity.CENTER_VERTICAL
        renderSendState(holder.messageView, holder.messageView)
        holder.messageView.onClick(attributes.itemClickListener)
        holder.messageView.onLongClickIgnoringLinks(attributes.itemLongClickListener)
        holder.messageView.setTextWithEmojiSupport(message?.charSequence, bindingOptions)


    }

    private fun AppCompatTextView.setTextWithEmojiSupport(message: CharSequence?, bindingOptions: BindingOptions?) {
        if (bindingOptions?.canUseTextFuture.orFalse() && message != null) {
            val textFuture = PrecomputedTextCompat.getTextFuture(message, TextViewCompat.getTextMetricsParams(this), null)
            setTextFuture(textFuture)
        } else {
            setTextFuture(null)
            text = message
        }
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageView by bind<AppCompatTextView>(R.id.messageGiftTextView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentGiftsStub
    }
}
