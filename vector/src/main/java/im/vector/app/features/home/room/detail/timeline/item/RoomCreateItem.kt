

package im.vector.app.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence
import me.saket.bettermovementmethod.BetterLinkMovementMethod

@EpoxyModelClass(layout = R2.layout.item_timeline_event_create)
abstract class RoomCreateItem : VectorEpoxyModel<RoomCreateItem.Holder>() {

    @EpoxyAttribute lateinit var text: EpoxyCharSequence

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.description.movementMethod = BetterLinkMovementMethod.getInstance()
        holder.description.text = text.charSequence
    }

    class Holder : VectorEpoxyHolder() {
        val description by bind<TextView>(R.id.roomCreateItemDescription)
    }
}
