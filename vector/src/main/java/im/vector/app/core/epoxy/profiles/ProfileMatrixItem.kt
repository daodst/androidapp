

package im.vector.app.core.epoxy.profiles

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.ui.views.PresenceStateImageView
import im.vector.app.core.ui.views.ShieldImageView

@EpoxyModelClass(layout = R2.layout.item_profile_matrix_item)
abstract class ProfileMatrixItem : BaseProfileMatrixItem<ProfileMatrixItem.Holder>() {

    open class Holder : VectorEpoxyHolder() {
        val titleView by bind<TextView>(R.id.matrixItemTitle)
        val subtitleView by bind<TextView>(R.id.matrixItemSubtitle)
        val ignoredUserView by bind<ImageView>(R.id.matrixItemIgnored)
        val powerLabel by bind<TextView>(R.id.matrixItemPowerLevelLabel)
        val presenceImageView by bind<PresenceStateImageView>(R.id.matrixItemPresenceImageView)
        val avatarImageView by bind<ImageView>(R.id.matrixItemAvatar)
        val avatarDecorationImageView by bind<ShieldImageView>(R.id.matrixItemAvatarDecoration)
        val editableView by bind<View>(R.id.matrixItemEditable)
    }
}
