

package im.vector.app.features.home.room.detail.timeline.item

import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence
import org.matrix.android.sdk.api.session.room.send.CUS_TEXT_TYPE_NORMAL
import org.matrix.android.sdk.api.session.room.send.CUS_TEXT_TYPE_WARNING


@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusJoinItem : AbsMessageItem<MessageCusJoinItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var nickName: EpoxyCharSequence? = null

    @EpoxyAttribute
    var title: EpoxyCharSequence? = null

    @EpoxyAttribute
    var forHtml: Boolean = false

    @EpoxyAttribute
    var type: Int = CUS_TEXT_TYPE_NORMAL
    override fun bind(holder: Holder) {
        super.bind(holder)

        if (forHtml) {
            holder.messageView.setText(Html.fromHtml((message?.charSequence ?: "") as String))
        } else {
            holder.messageView.setText(message?.charSequence)
        }
        holder.messageCusTxtTitle.setText(title?.charSequence)
        val context = holder.messageView.context
        holder.memberNameView.text = nickName?.charSequence ?: context.getString(R.string.system_msg_title)
        if (type == CUS_TEXT_TYPE_WARNING) {
            holder.messageCusTxtTitle.setTextColor(ContextCompat.getColor(context, R.color.color_FF6701))
            holder.cusMsgCk.setImageResource(R.drawable.cus_msg_ck2)
        } else {
            holder.cusMsgCk.setImageResource(R.drawable.cus_msg_ck)
            holder.messageCusTxtTitle.setTextColor(ContextCompat.getColor(context, R.color.color03B384))
        }
        holder.cusTimeView.text = attributes.informationData.time
    }


    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageView by bind<TextView>(R.id.messageCusTxtTextView)
        val messageCusTxtTitle by bind<TextView>(R.id.messageCusTxtTitle)
        val cusMsgCk by bind<ImageView>(R.id.cusMsgCk)
        val cusTimeView by bind<TextView>(R.id.messageCusTextViewTimeView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusTxtStub
    }
}
