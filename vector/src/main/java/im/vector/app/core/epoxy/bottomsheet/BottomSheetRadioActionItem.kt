
package im.vector.app.core.epoxy.bottomsheet

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setAttributeTintedImageResource
import im.vector.app.core.extensions.setTextOrHide


@EpoxyModelClass(layout = R2.layout.item_bottom_sheet_radio)
abstract class BottomSheetRadioActionItem : VectorEpoxyModel<BottomSheetRadioActionItem.Holder>() {

    @EpoxyAttribute
    var title: String? = null

    @StringRes
    @EpoxyAttribute
    var titleRes: Int? = null

    @EpoxyAttribute
    var selected = false

    @EpoxyAttribute
    var description: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: ClickListener

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.onClick(listener)
        if (titleRes != null) {
            holder.titleText.setText(titleRes!!)
        } else {
            holder.titleText.text = title
        }
        holder.descriptionText.setTextOrHide(description)

        if (selected) {
            holder.radioImage.setAttributeTintedImageResource(R.drawable.ic_radio_on, R.attr.colorPrimary)
            holder.radioImage.contentDescription = holder.view.context.getString(R.string.a11y_checked)
        } else {
            holder.radioImage.setImageDrawable(ContextCompat.getDrawable(holder.view.context, R.drawable.ic_radio_off))
            holder.radioImage.contentDescription = holder.view.context.getString(R.string.a11y_unchecked)
        }
    }

    class Holder : VectorEpoxyHolder() {
        val titleText by bind<TextView>(R.id.actionTitle)
        val descriptionText by bind<TextView>(R.id.actionDescription)
        val radioImage by bind<ImageView>(R.id.radioIcon)
    }
}
