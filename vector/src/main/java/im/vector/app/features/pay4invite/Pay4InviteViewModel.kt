

package im.vector.app.features.pay4invite

import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone


class Pay4InviteViewModel @AssistedInject constructor(
        @Assisted private val initialState: Pay4InviteViewState,
        private val stringProvider: StringProvider,
        private val session: Session
) : VectorViewModel<Pay4InviteViewState, Pay4InviteAction, Pay4InviteViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<Pay4InviteViewModel, Pay4InviteViewState> {
        override fun create(initialState: Pay4InviteViewState): Pay4InviteViewModel
    }

    companion object : MavericksViewModelFactory<Pay4InviteViewModel, Pay4InviteViewState> by hiltMavericksViewModelFactory()

    init {
        setState {
            copy()
        }
    }

    override fun handle(action: Pay4InviteAction) {
        when (action) {
            is Pay4InviteAction.CheckUserPayStatus -> handleCheckUserPayStatus(action.userByPhones, action.userByPhone)
        }
    }

    private fun handleCheckUserPayStatus(userByPhones: List<UserByPhone>?, userByPhone: UserByPhone) {

        val list = userByPhones?.toMutableList()
        list?.remove(userByPhone)
        val copy = userByPhone.copy(can_we_talk = true, can_pay_talk = false, payed = true, shouldSendFlowers = true)
        list?.add(copy)
        setState {
            copy(userByPhones = list)
        }

    }
}
