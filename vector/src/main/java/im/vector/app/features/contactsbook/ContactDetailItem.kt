

package im.vector.app.features.contactsbook

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_contact_detail)
abstract class ContactDetailItem : VectorEpoxyModel<ContactDetailItem.Holder>() {

    @EpoxyAttribute lateinit var threePid: String
    @EpoxyAttribute var matrixId: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.onClick(clickListener)
        holder.nameView.text = threePid
        holder.matrixIdView.setTextOrHide(matrixId)
    }

    class Holder : VectorEpoxyHolder() {
        val nameView by bind<TextView>(R.id.contactDetailName)
        val matrixIdView by bind<TextView>(R.id.contactDetailMatrixId)
    }
}
