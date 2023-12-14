
package im.vector.app.features.settings.threepids

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.onClick

@EpoxyModelClass(layout = R2.layout.item_settings_three_pid)
abstract class ThreePidItem : EpoxyModelWithHolder<ThreePidItem.Holder>() {

    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    @DrawableRes
    var iconResId: Int? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var deleteClickListener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        val safeIconResId = iconResId
        if (safeIconResId != null) {
            holder.icon.isVisible = true
            holder.icon.setImageResource(safeIconResId)
        } else {
            holder.icon.isVisible = false
        }

        holder.title.text = title
        holder.delete.onClick(deleteClickListener)
        holder.delete.isVisible = deleteClickListener != null
    }

    class Holder : VectorEpoxyHolder() {
        val icon by bind<ImageView>(R.id.item_settings_three_pid_icon)
        val title by bind<TextView>(R.id.item_settings_three_pid_title)
        val delete by bind<View>(R.id.item_settings_three_pid_delete)
    }
}
