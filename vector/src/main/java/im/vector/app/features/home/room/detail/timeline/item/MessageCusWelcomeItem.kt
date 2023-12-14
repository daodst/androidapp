

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.glide.GlideApp
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence


@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusWelcomeItem : AbsMessageItem<MessageCusWelcomeItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var title: EpoxyCharSequence? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.messageView.setText(message?.charSequence)
        holder.messageCusTitle.setText(title?.charSequence)
        GlideApp.with(holder.messageCusIv)
                .asGif()
                .load(R.drawable.cus_pos_gf)
                .into(holder.messageCusIv)
        holder.cusTimeView.text = attributes.informationData.time
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageCusIv by bind<ImageView>(R.id.messageCusWellcomeIv)
        val messageCusTitle by bind<TextView>(R.id.messageCusWellcomeTitle)
        val messageView by bind<TextView>(R.id.messageCusWellcomeContent)
        val cusTimeView by bind<TextView>(R.id.messageCusWellcomeTimeView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusWelcomeStub
    }
}
