

package im.vector.app.features.home.room.detail.timeline.item

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder

@EpoxyModelClass(layout = R2.layout.item_timeline_event_day_separator)
abstract class DaySeparatorItem : EpoxyModelWithHolder<DaySeparatorItem.Holder>() {

    @EpoxyAttribute lateinit var formattedDay: String

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.dayTextView.text = formattedDay
        holder.itemDayTextViewLine.isVisible = false
        val context = holder.dayTextView.context

        holder.dayTextView.setBackgroundResource(R.drawable.bg_item_timeline_day)
        holder.dayTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    class Holder : VectorEpoxyHolder() {
        val dayTextView by bind<TextView>(R.id.itemDayTextView)
        val itemDayTextViewLine by bind<View>(R.id.itemDayTextViewLine)
    }
}
