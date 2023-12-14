

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.onClick
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageCusChartItem : AbsMessageItem<MessageCusChartItem.Holder>() {

    @EpoxyAttribute
    var message: EpoxyCharSequence? = null

    @EpoxyAttribute
    var deviceRate: EpoxyCharSequence? = null

    @EpoxyAttribute
    var deviceRateSmall: EpoxyCharSequence? = null

    @EpoxyAttribute
    var deviceRateUp: Boolean = false

    @EpoxyAttribute
    var connRate: EpoxyCharSequence? = null

    @EpoxyAttribute
    var connRateSmall: EpoxyCharSequence? = null

    @EpoxyAttribute
    var connRateUp: Boolean = false

    @EpoxyAttribute
    var newDeviceNum: EpoxyCharSequence? = null

    @EpoxyAttribute
    var dvmNum: EpoxyCharSequence? = null

    @EpoxyAttribute
    var posNum: EpoxyCharSequence? = null

    @EpoxyAttribute
    var day3Pos: EpoxyCharSequence? = null

    @EpoxyAttribute
    var day3Active: EpoxyCharSequence? = null

    @EpoxyAttribute
    var day3Lg: EpoxyCharSequence? = null

    @EpoxyAttribute
    var day7Pos: EpoxyCharSequence? = null

    @EpoxyAttribute
    var day7Active: EpoxyCharSequence? = null

    @EpoxyAttribute
    var day7Lg: EpoxyCharSequence? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var posClickListener: ClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var activeClickListener: ClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var lGClickListener: ClickListener? = null
    override fun bind(holder: Holder) {
        super.bind(holder)

        
        holder.cusChartOnlineRate.text = deviceRate?.charSequence
        holder.cusChartOnlineRateSmall.text = deviceRateSmall?.charSequence
        holder.cusChartConnRate.text = connRate?.charSequence
        holder.cusChartConnRateSmall.text = connRateSmall?.charSequence
        holder.cusChartNewAddNum.text = newDeviceNum?.charSequence
        holder.cusChartDVMNum.text = dvmNum?.charSequence
        holder.cusChartPosNum.text = posNum?.charSequence


        holder.cusChartBuy.text = day3Pos?.charSequence
        holder.cusChartActive.text = day3Active?.charSequence
        holder.cusChartLg.text = day3Lg?.charSequence

        
        holder.cusChartBuyBt.setTag(true)
        holder.cusChartLgBt.setTag(true)
        holder.cusChartActiveBt.setTag(true)
        
        holder.cusChartBuyBt.onClick(posClickListener)
        holder.cusChartLgBt.onClick(activeClickListener)
        holder.cusChartActiveBt.onClick(lGClickListener)



        holder.cusChartRbParent.setOnCheckedChangeListener { _, checkedId ->
            val isDay3 = checkedId == holder.cusChartRb3.id
            holder.cusChartBuyBt.setTag(isDay3)
            holder.cusChartLgBt.setTag(isDay3)
            holder.cusChartActiveBt.setTag(isDay3)
            if (isDay3) {
                holder.cusChartBuy.text = day3Pos?.charSequence
                holder.cusChartActive.text = day3Active?.charSequence
                holder.cusChartLg.text = day3Lg?.charSequence
            } else {

                holder.cusChartBuy.text = day7Pos?.charSequence
                holder.cusChartActive.text = day7Active?.charSequence
                holder.cusChartLg.text = day7Lg?.charSequence
            }
        }
        holder.cusChartTimeView.text = attributes.informationData.time
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val cusChartOnlineRate by bind<TextView>(R.id.cusChartOnlineRate)
        val cusChartOnlineRateSmall by bind<TextView>(R.id.cusChartOnlineRateSmall)
        val cusChartConnRate by bind<TextView>(R.id.cusChartConnRate)
        val cusChartConnRateSmall by bind<TextView>(R.id.cusChartConnRateSmall)
        val cusChartNewAddNum by bind<TextView>(R.id.cusChartNewAddNum)
        val cusChartDVMNum by bind<TextView>(R.id.cusChartDVMNum)
        val cusChartPosNum by bind<TextView>(R.id.cusChartPosNum)
        val cusChartRbParent by bind<RadioGroup>(R.id.cusChartRbParent)
        val cusChartRb3 by bind<RadioButton>(R.id.cusChartRb3)
        val cusChartRb7 by bind<RadioButton>(R.id.cusChartRb7)
        val cusChartBuy by bind<TextView>(R.id.cusChartBuy)
        val cusChartBuyBt by bind<TextView>(R.id.cusChartBuyBt)
        val cusChartActive by bind<TextView>(R.id.cusChartActive)
        val cusChartActiveBt by bind<TextView>(R.id.cusChartActiveBt)
        val cusChartLg by bind<TextView>(R.id.cusChartLg)
        val cusChartLgBt by bind<TextView>(R.id.cusChartLgBt)
        val cusChartTimeView by bind<TextView>(R.id.cusChartTimeView)
    }

    companion object {
        private var STUB_ID = R.id.messageContentCusChartStub
    }
}
