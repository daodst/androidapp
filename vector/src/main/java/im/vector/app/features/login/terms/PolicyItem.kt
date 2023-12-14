

package im.vector.app.features.login.terms

import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setHorizontalPadding

@EpoxyModelClass(layout = R2.layout.item_policy)
abstract class PolicyItem : EpoxyModelWithHolder<PolicyItem.Holder>() {
    @EpoxyAttribute
    var checked: Boolean = false

    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    var subtitle: String? = null

    @EpoxyAttribute
    var horizontalPadding: Int? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var checkChangeListener: CompoundButton.OnCheckedChangeListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        horizontalPadding?.let { holder.view.setHorizontalPadding(it) }
        holder.checkbox.isChecked = checked
        holder.checkbox.setOnCheckedChangeListener(checkChangeListener)
        holder.title.text = title
        holder.subtitle.text = subtitle
        holder.view.onClick(clickListener)
    }

    
    override fun unbind(holder: Holder) {
        super.unbind(holder)
        holder.checkbox.setOnCheckedChangeListener(null)
    }

    class Holder : VectorEpoxyHolder() {
        val checkbox by bind<CheckBox>(R.id.adapter_item_policy_checkbox)
        val title by bind<TextView>(R.id.adapter_item_policy_title)
        val subtitle by bind<TextView>(R.id.adapter_item_policy_subtitle)
    }
}
