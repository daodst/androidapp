

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
abstract class MessageCusLordItem : AbsMessageItem<MessageCusLordItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null


    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var invitateClickListener: ClickListener? = null


    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var manageClickListener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.messageView.setText(message?.charSequence)

        holder.lordInvitateBt.onClick(invitateClickListener)

        holder.lordManageBt.onClick(manageClickListener)
    }

    override fun unbind(holder: Holder) {
        holder.lordInvitateBt.setOnClickListener(null)
        holder.lordManageBt.setOnClickListener(null)
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageView by bind<TextView>(R.id.messageCusLordTextView)
        val lordInvitateBt by bind<TextView>(R.id.messageCusLordInvitateBt)
        val lordManageBt by bind<TextView>(R.id.messageCusLordManageBt)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusLordStub
    }
}
