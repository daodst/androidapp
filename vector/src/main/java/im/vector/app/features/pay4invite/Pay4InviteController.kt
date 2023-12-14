

package im.vector.app.features.pay4invite

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.pay4invite.item.pay4InviteItem
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject


class Pay4InviteController @Inject constructor(
        private val avatarRenderer: AvatarRenderer,
) : TypedEpoxyController<Pay4InviteViewState>() {

    var callback: Callback? = null

    interface Callback {
        fun payClick(userByPhone: UserByPhone)
    }

    override fun buildModels(data: Pay4InviteViewState?) {
        data ?: return
        buildUserActions(data.userByPhones)
    }

    private fun buildUserActions(userByPhones: List<UserByPhone>?) {

        userByPhones?.forEach { user ->
            buildProfileAction(user, avatarRenderer, callback)
        }
    }

    fun buildProfileAction(
            user: UserByPhone,
            avatarRenderer: AvatarRenderer,
            action: Callback? = null) {
        pay4InviteItem {
            id(user.userId)
            user(user)
            avatarRenderer(avatarRenderer)
            matrixItem(MatrixItem.UserItem(user.userId, user.display_name, user.avatar_url))
            listener {
                action?.payClick(user)
            }
        }
    }
}
