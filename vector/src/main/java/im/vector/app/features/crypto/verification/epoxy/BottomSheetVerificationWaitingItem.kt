
package im.vector.app.features.crypto.verification.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel


@EpoxyModelClass(layout = R2.layout.item_verification_waiting)
abstract class BottomSheetVerificationWaitingItem : VectorEpoxyModel<BottomSheetVerificationWaitingItem.Holder>() {

    @EpoxyAttribute
    var title: String = ""

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.title.text = title
    }

    class Holder : VectorEpoxyHolder() {
        val title by bind<TextView>(R.id.itemVerificationWaitingTitle)
    }
}
