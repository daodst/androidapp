

package im.vector.app.core.epoxy

import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.features.home.room.detail.timeline.item.ItemWithEvents

@EpoxyModelClass(layout = R2.layout.item_timeline_empty)
abstract class TimelineEmptyItem : VectorEpoxyModel<TimelineEmptyItem.Holder>(), ItemWithEvents {

    @EpoxyAttribute lateinit var eventId: String
    @EpoxyAttribute var notBlank: Boolean = false

    override fun isVisible() = false

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.updateLayoutParams {
            
            this.height = if (notBlank) 1 else 0
        }
    }

    override fun getEventIds(): List<String> {
        return listOf(eventId)
    }

    class Holder : VectorEpoxyHolder()
}
