

package im.vector.app.features.home.room.detail.timeline.item

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.room.detail.TIMELINE_AWARD_TYPE_AIRDROP
import im.vector.app.features.home.room.detail.TIMELINE_AWARD_TYPE_DVM
import im.vector.app.features.home.room.detail.TIMELINE_AWARD_TYPE_POS
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence
import org.matrix.android.sdk.api.session.room.send.CUS_AWARD_DEFAULT
import org.matrix.android.sdk.api.session.room.send.CUS_AWARD_INVALID
import org.matrix.android.sdk.api.session.room.send.CUS_AWARD_RECEIVED
import org.matrix.android.sdk.api.session.room.send.CUS_AWARD_TAKE_BACK


@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusPledgeAwardItem : AbsMessageItem<MessageCusPledgeAwardItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var status: Int? = null

    @EpoxyAttribute
    var balance: String? = null

    @EpoxyAttribute
    var type: Int? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: ClickListener? = null
    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.cusPledgeAwardBg.onClick(clickListener)

        val context = holder.view.context
        holder.btView.text =
                if (status == CUS_AWARD_TAKE_BACK) {
                    context.getString(R.string.cus_arard_bt_take_back)
                } else if (status == CUS_AWARD_RECEIVED) {
                    context.getString(R.string.cus_arard_bt_received)
                } else if (status == CUS_AWARD_INVALID) {
                    context.getString(R.string.cus_arard_bt_invalid)
                } else {
                    context.getString(R.string.cus_arard_bt_unreceived)
                }

        val padding = if (status == CUS_AWARD_TAKE_BACK) {
            DimensionConverter(context.resources).dpToPx(0);
        } else if (status == CUS_AWARD_RECEIVED) {
            DimensionConverter(context.resources).dpToPx(0);
        } else if (status == CUS_AWARD_INVALID) {
            DimensionConverter(context.resources).dpToPx(0);
        } else {
            DimensionConverter(context.resources).dpToPx(15);
        }

        holder.btView.setPadding(padding, 0, padding, 0)
        holder.cusPledgeAwardBalanceParent.isVisible = status != CUS_AWARD_DEFAULT
        holder.cusPledgeAwardBalanceHolder.isVisible = status == CUS_AWARD_DEFAULT
        holder.btView.background = if (status == CUS_AWARD_DEFAULT) {
            ContextCompat.getDrawable(context, R.drawable.bg_award_bt)
        } else {
            null
        }
        holder.cusPledgeAwardBalance.text = if (balance.isNullOrEmpty()) "0.00 " else "$balance "
        holder.cusPledgeAwardBg.isEnabled = status == CUS_AWARD_DEFAULT
        holder.memberNameView.text = context.getString(R.string.cus_pledge_award_stub_title)
        if (type == TIMELINE_AWARD_TYPE_AIRDROP) {
            holder.cusPledgeAwardTitle.text = context.getString(R.string.bg_item_timeline_event_cus_airdrop)
            holder.cusPledgeAwardBg.setBackgroundResource(R.drawable.bg_item_timeline_event_cus_airdrop)
        } else if (type == TIMELINE_AWARD_TYPE_POS) {
            holder.cusPledgeAwardTitle.text = context.getString(R.string.bg_item_timeline_event_cus_pos)
            holder.cusPledgeAwardBg.setBackgroundResource(R.drawable.bg_item_timeline_event_cus_pos)
        } else if (type == TIMELINE_AWARD_TYPE_DVM) {
            holder.cusPledgeAwardTitle.text = context.getString(R.string.bg_item_timeline_event_cus_dvm)
            holder.cusPledgeAwardBg.setBackgroundResource(R.drawable.bg_item_timeline_event_cus_dvm)
        } else {
            holder.cusPledgeAwardTitle.text = context.getString(R.string.bg_item_timeline_event_cus_dao)
            holder.cusPledgeAwardBg.setBackgroundResource(R.drawable.bg_item_timeline_event_cus_dao)
        }

        holder.cusPledgeAwardBtTimeView.text = attributes.informationData.time
    }

    override fun unbind(holder: Holder) {
        holder.cusPledgeAwardBg.setOnClickListener(null)
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val cusPledgeAwardBalanceParent by bind<View>(R.id.cusPledgeAwardBalanceParent)
        val cusPledgeAwardBalanceHolder by bind<View>(R.id.cusPledgeAwardBalanceHolder)
        val cusPledgeAwardBalance by bind<TextView>(R.id.cusPledgeAwardBalance)
        val cusPledgeAwardBg by bind<LinearLayout>(R.id.cusPledgeAwardBg)
        val cusPledgeAwardTitle by bind<TextView>(R.id.cusPledgeAwardTitle)
        val btView by bind<TextView>(R.id.cusPledgeAwardBt)
        val cusPledgeAwardBtTimeView by bind<TextView>(R.id.cusPledgeAwardBtTimeView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusPledgeAwardStub
    }
}
