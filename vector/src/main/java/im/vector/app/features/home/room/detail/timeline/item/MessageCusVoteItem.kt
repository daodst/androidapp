

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
abstract class MessageCusVoteItem : AbsMessageItem<MessageCusVoteItem.Holder>() {

    @EpoxyAttribute
    var title: EpoxyCharSequence? = null

    @EpoxyAttribute
    var time: EpoxyCharSequence? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var OnClickListener: ClickListener? = null


    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.cusVoteTitle.setText(title?.charSequence)
        holder.view.onClick(OnClickListener)
        holder.cusVoteTime.setText(time?.charSequence)
        GlideApp.with(holder.cusVoteIv)
                .asGif()
                .load(R.drawable.cus_vote_gf)
                .into(holder.cusVoteIv)
        holder.cusVoteTimeView.text = attributes.informationData.time
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val cusVoteName by bind<TextView>(R.id.cusVoteName)
        val cusVoteTitle by bind<TextView>(R.id.cusVoteTitle)
        val cusVoteTime by bind<TextView>(R.id.cusVoteTime)
        val cusVoteTimeView by bind<TextView>(R.id.cusVoteTimeView)
        val cusVoteIv by bind<ImageView>(R.id.cusVoteIv)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusVoteStub
    }
}
