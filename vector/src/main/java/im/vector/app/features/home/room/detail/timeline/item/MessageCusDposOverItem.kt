

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusDposOverItem : AbsMessageItem<MessageCusDposOverItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null


    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.messageView.setText(message?.charSequence)
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageView by bind<TextView>(R.id.messageCusDposOverTextView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusDposOverStub
    }
}
