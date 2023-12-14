
package im.vector.app.core.ui.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setTextOrHide
import im.vector.app.features.themes.ThemeUtils
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence


@EpoxyModelClass(layout = R2.layout.item_generic_with_value)
abstract class GenericWithValueItem : VectorEpoxyModel<GenericWithValueItem.Holder>() {

    @EpoxyAttribute
    var title: EpoxyCharSequence? = null

    @EpoxyAttribute
    var value: String? = null

    @EpoxyAttribute
    @ColorInt
    var valueColorInt: Int? = null

    @EpoxyAttribute
    @DrawableRes
    var titleIconResourceId: Int = -1

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var itemClickAction: ClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var itemLongClickAction: View.OnLongClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.titleText.setTextOrHide(title?.charSequence)

        if (titleIconResourceId != -1) {
            holder.titleIcon.setImageResource(titleIconResourceId)
            holder.titleIcon.isVisible = true
        } else {
            holder.titleIcon.isVisible = false
        }

        holder.valueText.setTextOrHide(value)

        if (valueColorInt != null) {
            holder.valueText.setTextColor(valueColorInt!!)
        } else {
            holder.valueText.setTextColor(ThemeUtils.getColor(holder.view.context, R.attr.vctr_content_primary))
        }

        holder.view.onClick(itemClickAction)
        holder.view.setOnLongClickListener(itemLongClickAction)
    }

    class Holder : VectorEpoxyHolder() {
        val titleIcon by bind<ImageView>(R.id.itemGenericWithValueTitleIcon)
        val titleText by bind<TextView>(R.id.itemGenericWithValueLabelText)
        val valueText by bind<TextView>(R.id.itemGenericWithValueValueText)
    }
}
