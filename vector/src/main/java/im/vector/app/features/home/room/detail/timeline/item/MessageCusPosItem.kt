

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.glide.GlideApp
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusPosItem : AbsMessageItem<MessageCusPosItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var title: EpoxyCharSequence? = null

    @EpoxyAttribute
    var btStr: EpoxyCharSequence? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var OnClickListener: ClickListener? = null

    @EpoxyAttribute
    var nickName: EpoxyCharSequence? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        val context = holder.messageView.context
        holder.memberNameView.text = nickName?.charSequence ?: context.getString(R.string.system_msg_title)
        holder.messageView.setText(message?.charSequence)
        holder.messageCusPosTitle.setText(title?.charSequence)
        holder.messageCusPosBt.setText(btStr?.charSequence)
        holder.messageCusPosBt.onClick(OnClickListener)
        GlideApp.with(holder.messageCusPosIv)
                .asGif()
                .load(R.drawable.cus_pos_gf)
                .into(holder.messageCusPosIv)
        holder.messageCusPosTimeView.text = attributes.informationData.time
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageCusPosIv by bind<ImageView>(R.id.messageCusPosIv)
        val messageCusPosTitle by bind<TextView>(R.id.messageCusPosTitle)
        val messageView by bind<TextView>(R.id.messageCusPosContent)
        val messageCusPosBt by bind<TextView>(R.id.messageCusPosBt)
        val messageCusPosTimeView by bind<TextView>(R.id.messageCusPosTimeView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusPosStub
    }
}
