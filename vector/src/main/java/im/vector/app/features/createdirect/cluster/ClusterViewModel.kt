

package im.vector.app.features.createdirect.cluster

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
import im.wallet.router.wallet.pojo.DeviceGroupMember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.raw.RawService
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesContent
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import java.math.BigDecimal

class ClusterViewModel @AssistedInject constructor(@Assisted initialState: ClusterViewState, private val activeSessionHolder: ActiveSessionHolder, val context: Context, val errorFormatter: ErrorFormatter, private val rawService: RawService, val session: Session, val analyticsTracker: AnalyticsTracker) : VectorViewModel<ClusterViewState, ClusterRoomAction, ClusterViewEvents>(
        initialState
) {

    @AssistedFactory interface Factory : MavericksAssistedViewModelFactory<ClusterViewModel, ClusterViewState> {
        override fun create(initialState: ClusterViewState): ClusterViewModel
    }

    init {
        handle(ClusterRoomAction.GetBalance)
        
        if (initialState.mode == 0) {
            initializeForRoom(initialState.roomId!!)
        }
    }

    private fun initializeForRoom(roomId: String) {
        var room = session.getRoom(roomId)!!
        session.getRoomSummary(roomId)?.let { roomSummary ->
            val joinRulesContent = room.getStateEvent(EventType.STATE_ROOM_JOIN_RULES, QueryStringValue.NoCondition)
                    ?.content
                    ?.toModel<RoomJoinRulesContent>()
            var safeRule: RoomJoinRules = joinRulesContent?.joinRules ?: RoomJoinRules.INVITE
            setState {
                copy(
                        currentRoomJoinRules = safeRule
                )
            }
        }
    }

    companion object : MavericksViewModelFactory<ClusterViewModel, ClusterViewState> by hiltMavericksViewModelFactory()

    override fun handle(action: ClusterRoomAction) {
        when (action) {
            is ClusterRoomAction.ChangeRoomRule -> {
                setState {
                    copy(
                            currentRoomJoinRules = action.rule
                    )
                }
            }
            is ClusterRoomAction.GetBalance     -> getBalance()
            is ClusterRoomAction.SetAvatar      -> setAvatar(action)
            is ClusterRoomAction.UpGrade        -> upGradeRoom(action)
            is ClusterRoomAction.Create         -> createRoom(action)
        }
    }

    private fun getBalance() {
        val safeActiveSession = activeSessionHolder.getSafeActiveSession() ?: return
        
        var walletPay: IWalletPay? = null
        context.applicationContext?.takeAs<IApplication>()?.apply {
            walletPay = getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            val params = walletPay?.httpGetDaoParams(context, safeActiveSession.myOriginUId, "") ?: return@launch
            if (params.status != 1) {
                
                setState {
                    copy(asyncCreateRoomRequest = Fail(RuntimeException()))
                }
            } else {
                
                setState {
                    copy(
                            destoryBalance = params.balance ?: "0",
                            freezeBalance = params.freezeNum ?: "0",
                            radio = params.data?.burnPowerRatio ?: "0",
                            salary_range = params.data?.salary_range,
                            device_range = params.data?.device_range,
                            create_cluster_min_burn = params.data?.create_cluster_min_burn,
                    )
                }
            }

        }
    }

    private fun createRoom(action: ClusterRoomAction.Create) = withState { state ->

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
                        setState {
                            copy(asyncCreateRoomRequest = Success(roomId), name = action.name, roomId = roomId, isCreated = true)
                        }
                    },
                    { failure ->
                        setState {
                            copy(asyncCreateRoomRequest = Fail(failure))
                        }
                        _viewEvents.post(ClusterViewEvents.Failure(failure))
                    }
            )
        }

    }

    private fun upGradeRoom(action: ClusterRoomAction.UpGrade) = withState { state ->
        
        viewModelScope.launch {
            val safeActiveSession = activeSessionHolder.getSafeActiveSession() ?: return@launch
            
            var walletPay: IWalletPay? = null
            context.applicationContext?.takeAs<IApplication>()?.apply {
                walletPay = getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay
            }
            val owner = BigDecimal("1").subtract(BigDecimal(action.owner).multiply(BigDecimal("0.01"))).toPlainString()
            val rate = BigDecimal(action.rate).multiply(BigDecimal("0.01")).toPlainString()

            val destroy = if (action.isDestroy) action.destroy else "0"
            val freeze = if (action.isDestroy) "0" else action.destroy

            val room = session.getRoom(state.roomId!!)!!

            val list = withContext(Dispatchers.IO) {
                val list = ArrayList<DeviceGroupMember>()
                room.loadRoomMembers().forEach { (key, value) ->
                    list.add(DeviceGroupMember(key, value["chat_addr"]))
                }
                list
            }
            walletPay?.createDeviceGroup(
                    action.activity, safeActiveSession.myOriginUId, state.roomId, rate, owner,
                    destroy, state.name, freeze, list, object : TranslationListener {
                override fun onFail(errorInfo: String?) {
                    setState {
                        copy(asyncCreateRoomRequest = Fail(RuntimeException(errorInfo ?: "")))
                    }
                }

                override fun onTransSuccess() {
                    setState {
                        copy(asyncCreateRoomRequest = Success(state.roomId), isUpdate = true)
                    }
                }
            })
        }
    }

    private fun setAvatar(action: ClusterRoomAction.SetAvatar) = setState { copy(avatarUri = action.imageUri) }
}
