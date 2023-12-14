package im.vector.app.features.createdirect.migrate

import android.content.Context
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.plan.CreatedRoom
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import im.wallet.router.listener.TranslationListener
import im.wallet.router.wallet.IWalletPay
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.raw.RawService
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset

class MigrateGroupViewModel @AssistedInject constructor(@Assisted initialState: MigrateGroupViewState, private val activeSessionHolder: ActiveSessionHolder, val context: Context, val errorFormatter: ErrorFormatter, private val rawService: RawService, val session: Session, val analyticsTracker: AnalyticsTracker) : VectorViewModel<MigrateGroupViewState, MigrateGroupAction, MigrateGroupViewEvents>(
        initialState
) {

    @AssistedFactory interface Factory : MavericksAssistedViewModelFactory<MigrateGroupViewModel, MigrateGroupViewState> {
        override fun create(initialState: MigrateGroupViewState): MigrateGroupViewModel
    }

    companion object : MavericksViewModelFactory<MigrateGroupViewModel, MigrateGroupViewState> by hiltMavericksViewModelFactory()

    override fun handle(action: MigrateGroupAction) {

        when (action) {
            is MigrateGroupAction.SetAvatar -> setAvatar(action)
            is MigrateGroupAction.Create    -> create(action)
        }
    }

    private fun create(action: MigrateGroupAction.Create) = withState { state ->

        setState {
            copy(asyncCreateRoomRequest = Loading())
        }


        val createRoomParams = CreateRoomParams()
                .apply {
                    name = action.name
                    topic = action.topic
                    avatarUri = state.avatarUri

                    
                    
                    visibility = RoomDirectoryVisibility.PRIVATE
                    
                    preset = CreateRoomPreset.PRESET_PRIVATE_CHAT

                    
                    disableFederation = false

                    
                    enableEncryption()
                }
        viewModelScope.launch {
            runCatching { session.createRoom(createRoomParams) }.fold(
                    { roomId ->
                        analyticsTracker.capture(CreatedRoom(isDM = createRoomParams.isDirect.orFalse()))
                        updateChain(action, roomId)
                    },
                    { failure ->
                        setState {
                            copy(asyncCreateRoomRequest = Fail(failure))
                        }
                    }
            )
        }
    }

    private fun updateChain(action: MigrateGroupAction.Create, roomId: String) = withState { state ->
        viewModelScope.launch {
            val safeActiveSession = activeSessionHolder.getSafeActiveSession() ?: return@launch
            
            var walletPay: IWalletPay? = null
            context.applicationContext?.takeAs<IApplication>()?.apply {
                walletPay = getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay
            }

            walletPay?.changeGroupId(action.activity, safeActiveSession.myOriginUId, state.roomId, roomId, object : TranslationListener {
                override fun onFail(errorInfo: String?) {
                    setState {
                        copy(asyncCreateRoomRequest = Fail(RuntimeException("errorInfo")))
                    }
                }

                override fun onTransSuccess() {
                    setState {
                        copy(asyncCreateRoomRequest = Success(roomId))
                    }
                }
            })
        }
    }

    private fun setAvatar(action: MigrateGroupAction.SetAvatar) = setState { copy(avatarUri = action.imageUri) }
}
