

package im.vector.app.features.pay4invite.item

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.themes.ThemeUtils
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.util.MatrixItem
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow


@EpoxyModelClass(layout = R2.layout.item_p4invite_item)
abstract class Pay4InviteItem : VectorEpoxyModel<Pay4InviteItem.Holder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var listener: ClickListener? = null

    @EpoxyAttribute
    lateinit var user: UserByPhone
    @EpoxyAttribute lateinit var avatarRenderer: AvatarRenderer

    @EpoxyAttribute lateinit var matrixItem: MatrixItem

    private fun dip2px(context: Context, dipValue: Float): Int {
        val scale: Float = context.getResources().getDisplayMetrics().density
        return (dipValue * scale + 0.5f).toInt()
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.onClick(listener)
        if (listener == null) {
            holder.view.isClickable = false
        }

        val context = holder.needPay.context
        
        avatarRenderer.render(matrixItem, holder.icon)
        
        holder.title.text = user.display_name
        
        holder.did.visibility = View.GONE
        if (user.payed) {
            holder.label.visibility = View.VISIBLE
            holder.title.setPadding(0, 0, dip2px(context, 50f), 0)
        } else {
            holder.label.visibility = View.GONE
            holder.title.setPadding(0, 0, 0, 0)
        }

        user.tel_numbers?.firstOrNull()?.let {
            holder.did.visibility = View.VISIBLE
            holder.did.text = "DID：${it.toBigDecimal().toPlainString()}"
        }

        holder.address.text = "${context.getString(R.string.p4invite_address)}${user.localpart}"

        if (user.can_we_talk) {
            
            holder.needPay.visibility = View.GONE
            
            holder.status.visibility = View.VISIBLE
            holder.status.setImageResource(R.drawable.p4invite_item_pay_ok)
            
            holder.priceOrTips.visibility = View.GONE
        } else if (user.can_pay_talk) {
            
            holder.needPay.visibility = View.VISIBLE
            
            holder.priceOrTips.visibility = View.VISIBLE
            
            holder.status.visibility = View.GONE
            holder.priceOrTips.text = span {
                span(context.getString(R.string.pay_talk_tips_new)) {
                    textColor = ContextCompat.getColor(context, R.color.pay_talk_tips_new)
                }
                span(" ${getTenDecimalValue(user.chat_fee)} ${BuildConfig.EVMOS_FAKE_UNINT} ") {
                    textColor = ThemeUtils.getColor(context, R.attr.colorAccent)
                }
            }
        } else {
            holder.needPay.visibility = View.GONE
            
            holder.status.visibility = View.VISIBLE
            holder.status.setImageResource(R.drawable.p4invite_item_pay_cannot)
            
            holder.priceOrTips.visibility = View.VISIBLE
            holder.priceOrTips.text = span {
                span(user.reason) {
                    textColor = ContextCompat.getColor(context, R.color.pay_talk_tips_prohibit)
                }
            }
        }
    }

    private fun getTenDecimalValue(bigNum: String): String? {
        return if (TextUtils.isEmpty(bigNum)) {
            bigNum
        } else BigDecimal(bigNum).divide(BigDecimal(10.0.pow(18.0)), 18, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }

    class Holder : VectorEpoxyHolder() {
        val icon by bind<ImageView>(R.id.p4invite_item_ico)

        val title by bind<TextView>(R.id.p4invite_item_title)

        
        val label by bind<TextView>(R.id.p4invite_item_pay_label)

        val did by bind<TextView>(R.id.p4invite_item_did)

        val address by bind<TextView>(R.id.p4invite_item_adress)

        
        val priceOrTips by bind<TextView>(R.id.p4invite_item_price)

        
        val needPay by bind<TextView>(R.id.p4invite_item_pay)

        
        val status by bind<ImageView>(R.id.p4invite_item_status)
    }
}
