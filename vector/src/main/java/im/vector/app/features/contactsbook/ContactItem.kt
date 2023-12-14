

package im.vector.app.features.contactsbook

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.contacts.MappedContact
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.features.home.AvatarRenderer

@EpoxyModelClass(layout = R2.layout.item_contact_main)
abstract class ContactItem : VectorEpoxyModel<ContactItem.Holder>() {

    @EpoxyAttribute lateinit var avatarRenderer: AvatarRenderer
    @EpoxyAttribute lateinit var mappedContact: MappedContact

    override fun bind(holder: Holder) {
        super.bind(holder)
        
        holder.nameView.text = mappedContact.displayName
        avatarRenderer.render(mappedContact, holder.avatarImageView)
    }

    class Holder : VectorEpoxyHolder() {
        val nameView by bind<TextView>(R.id.contactDisplayName)
        val avatarImageView by bind<ImageView>(R.id.contactAvatar)
    }
}
