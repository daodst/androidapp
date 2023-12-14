

package im.vector.app.features.roomdirectory.picker

import android.view.View
import android.widget.TextView
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

@EpoxyModelClass(layout = R2.layout.item_room_directory_server)
abstract class RoomDirectoryServerItem : VectorEpoxyModel<RoomDirectoryServerItem.Holder>() {

    @EpoxyAttribute
    var serverName: String? = null

    @EpoxyAttribute
    var serverDescription: String? = null

    @EpoxyAttribute
    var canRemove: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var removeListener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.nameView.text = serverName
        holder.descriptionView.setTextOrHide(serverDescription)
        holder.deleteView.isVisible = canRemove
        holder.deleteView.onClick(removeListener)
    }

    class Holder : VectorEpoxyHolder() {
        val nameView by bind<TextView>(R.id.itemRoomDirectoryServerName)
        val descriptionView by bind<TextView>(R.id.itemRoomDirectoryServerDescription)
        val deleteView by bind<View>(R.id.itemRoomDirectoryServerRemove)
    }
}
