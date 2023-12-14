

package im.vector.app.features.userdirectory

import im.vector.app.features.displayname.getBestName
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.util.toMatrixItem

sealed class PendingSelection {
    data class UserPendingSelection(val user: User) : PendingSelection()
    data class ThreePidPendingSelection(val threePid: ThreePid) : PendingSelection()

    fun getBestName(): String {
        return when (this) {
            is UserPendingSelection     -> user.toMatrixItem().getBestName()
            is ThreePidPendingSelection -> threePid.value
        }
    }

    fun getAvatar(): String {
        return when (this) {
            is UserPendingSelection     -> user.toMatrixItem().avatarUrl ?: ""
            
            is ThreePidPendingSelection -> threePid.value
        }
    }

    fun getMxId(): String {
        return when (this) {
            is UserPendingSelection     -> user.userId
            is ThreePidPendingSelection -> threePid.value
        }
    }
}

internal object PendingSelectionMapper {

    fun map(selection: PendingSelection): UserByPhone? {
        return tryOrNull {
            UserByPhone(
                    display_name = selection.getBestName(),
                    avatar_url = selection.getAvatar(),
                    
                    localpart = getAddressByUid(selection.getMxId()),
                    can_we_talk = false,
                    can_pay_talk = false
            )
        }
    }
}

fun PendingSelection.asDomain(): UserByPhone? {
    
    return PendingSelectionMapper.map(this)
}
