

package im.vector.app.features.home.room.detail.timeline.item

import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.onClick

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base) abstract class MessageCusDaoItem : AbsMessageItem<MessageCusDaoItem.Holder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var OnClickListener: ClickListener? = null

    var anim: AnimationDrawable? = null
    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.cusDaoBt.onClick(OnClickListener)
        val context = holder.cusDaoBt.context
        holder.memberNameView.text = context.getString(R.string.cus_vote_stub_nick_title)
        holder.imageView.setImageResource(R.drawable.cus_dao_gif_anim);
        anim = holder.imageView.getDrawable() as AnimationDrawable
        anim?.start()
        holder.cusDaoTimeView.text = attributes.informationData.time
    }

    override fun unbind(holder: Holder) {
        anim?.stop()
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val imageView by bind<ImageView>(R.id.cusDaoIv)
        val cusDaoName by bind<TextView>(R.id.cusDaoName)
        val cusDaoTimeView by bind<TextView>(R.id.cusDaoTimeView)
        val cusDaoBt by bind<LinearLayout>(R.id.cusDaoBt)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusDaoStub
    }
}
