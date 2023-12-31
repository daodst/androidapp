

package im.vector.app.features.home.room.list

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.features.themes.ThemeUtils

@EpoxyModelClass(layout = R2.layout.item_room_category)
abstract class RoomCategoryItem : VectorEpoxyModel<RoomCategoryItem.Holder>() {

    @EpoxyAttribute lateinit var title: String
    @EpoxyAttribute var itemCount: Int = 0
    @EpoxyAttribute var expanded: Boolean = false
    @EpoxyAttribute var unreadNotificationCount: Int = 0
    @EpoxyAttribute var showHighlighted: Boolean = false
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var listener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        val tintColor = ThemeUtils.getColor(holder.rootView.context, R.attr.vctr_content_secondary)
        val expandedArrowDrawableRes = if (expanded) R.drawable.ic_expand_more else R.drawable.ic_expand_less
        val expandedArrowDrawable = ContextCompat.getDrawable(holder.rootView.context, expandedArrowDrawableRes)?.also {
            DrawableCompat.setTint(it, tintColor)
        }
        holder.unreadCounterBadgeView.render(UnreadCounterBadgeView.State(unreadNotificationCount, showHighlighted))
        holder.titleView.text = title
        holder.counterView.text = itemCount.takeIf { it > 0 }?.toString().orEmpty()
        holder.counterView.setCompoundDrawablesWithIntrinsicBounds(null, null, expandedArrowDrawable, null)
        holder.rootView.onClick(listener)
    }

    class Holder : VectorEpoxyHolder() {
        val unreadCounterBadgeView by bind<UnreadCounterBadgeView>(R.id.roomCategoryUnreadCounterBadgeView)
        val titleView by bind<TextView>(R.id.roomCategoryTitleView)
        val counterView by bind<TextView>(R.id.roomCategoryCounterView)
        val rootView by bind<ViewGroup>(R.id.roomCategoryRootView)
    }
}
