

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageRedPacketItem : AbsMessageItem<MessageRedPacketItem.Holder>() {

    @EpoxyAttribute
    var desc: String? = null

    @EpoxyAttribute
    var transferNum: String? = null

    @EpoxyAttribute
    var redPacketId: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        renderSendState(holder.view, null)

        val num = transferNum ?: "- -"
        holder.redPacketNumTextView.text = num
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val redPacketBgImageView by bind<ImageView>(R.id.redPacketBgImageView)
        val redPakcetPinImageView by bind<ImageView>(R.id.redPakcetPinImageView)
        val redPacketNumTextView by bind<TextView>(R.id.redPacketNumTextView)
        val redPacketDescTextView by bind<TextView>(R.id.redPacketDescTextView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentRedPacketStub
    }
}
