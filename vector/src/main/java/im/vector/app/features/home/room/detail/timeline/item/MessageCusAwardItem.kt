

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.onClick
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence


@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusAwardItem : AbsMessageItem<MessageCusAwardItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var receive: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.btView.onClick(clickListener)

        val context = holder.view.context
        holder.btView.text = if (receive) context.getString(R.string.cus_arard_bt_received) else context.getString(R.string.cus_arard_bt_unreceived)
        holder.btView.isEnabled = !receive
    }

    override fun unbind(holder: Holder) {
        holder.btView.setOnClickListener(null)
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val btView by bind<TextView>(R.id.cusAwardBt)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusAwardStub
    }
}
