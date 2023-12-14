

package im.vector.app.features.userdirectory

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_contact_action)
abstract class ActionItem : VectorEpoxyModel<ActionItem.Holder>() {

    @EpoxyAttribute var title: String? = null
    @EpoxyAttribute @DrawableRes var actionIconRes: Int? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var clickAction: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.onClick(clickAction)
        
        holder.actionTitleText.setTextOrHide(title)
        if (actionIconRes != null) {
            holder.actionTitleImageView.setImageResource(actionIconRes!!)
        } else {
            holder.actionTitleImageView.setImageDrawable(null)
        }
    }

    class Holder : VectorEpoxyHolder() {
        val actionTitleText by bind<TextView>(R.id.actionTitleText)
        val actionTitleImageView by bind<ImageView>(R.id.actionIconImageView)
    }
}
