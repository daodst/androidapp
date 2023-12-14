

package im.vector.app.features.pay4invite

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone

sealed class Pay4InviteAction : VectorViewModelAction {
    data class CheckUserPayStatus(val userByPhones: List<UserByPhone>?, val userByPhone: UserByPhone) : Pay4InviteAction()
}
