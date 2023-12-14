

package im.vector.app.features.roomdirectory.createroom

import androidx.core.net.toFile
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.AppStateHandler
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.plan.CreatedRoom
import im.vector.app.features.raw.wellknown.getElementWellknown
import im.vector.app.features.raw.wellknown.isE2EByDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.raw.RawService
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities
import org.matrix.android.sdk.api.session.room.alias.RoomAliasError
import org.matrix.android.sdk.api.session.room.failure.CreateRoomFailure
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesAllowEntry
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.api.session.room.model.create.RestrictedRoomPreset
import org.matrix.android.sdk.api.session.room.name.RoomNameError
import timber.log.Timber

class CreateRoomViewModel @AssistedInject constructor(
        @Assisted private val initialState: CreateRoomViewState,
        private val session: Session,
        private val rawService: RawService,
        appStateHandler: AppStateHandler,
        private val analyticsTracker: AnalyticsTracker
) : VectorViewModel<CreateRoomViewState, CreateRoomAction, CreateRoomViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<CreateRoomViewModel, CreateRoomViewState> {
        override fun create(initialState: CreateRoomViewState): CreateRoomViewModel
    }

    companion object : MavericksViewModelFactory<CreateRoomViewModel, CreateRoomViewState> by hiltMavericksViewModelFactory()

    init {
        initHomeServerName()
        initAdminE2eByDefault()

        val parentSpaceId = initialState.parentSpaceId ?: appStateHandler.safeActiveSpaceId()

        val restrictedSupport = session.getHomeServerCapabilities().isFeatureSupported(HomeServerCapabilities.ROOM_CAP_RESTRICTED)
        val createRestricted = restrictedSupport == HomeServerCapabilities.RoomCapabilitySupport.SUPPORTED

        val defaultJoinRules = if (parentSpaceId != null && createRestricted) {
            RoomJoinRules.RESTRICTED
        } else {
            RoomJoinRules.INVITE
        }

        setState {
            copy(
                    parentSpaceId = parentSpaceId,
                    supportsRestricted = createRestricted,
                    roomJoinRules = defaultJoinRules,
                    parentSpaceSummary = parentSpaceId?.let { session.getRoomSummary(it) }
            )
        }
    }

    private fun initHomeServerName() {
        setState {
            copy(
                    homeServerName = session.myUserId.getDomain()
            )
        }
    }

    private var adminE2EByDefault = true

    private fun initAdminE2eByDefault() {
        viewModelScope.launch(Dispatchers.IO) {
            adminE2EByDefault = tryOrNull {
                rawService.getElementWellknown(session.sessionParams)
                        ?.isE2EByDefault()
                        ?: true
            } ?: true

            setState {
                copy(
                        hsAdminHasDisabledE2E = !adminE2EByDefault,
                        defaultEncrypted = mapOf(
                                RoomJoinRules.INVITE to adminE2EByDefault,
                                RoomJoinRules.PUBLIC to false,
                                RoomJoinRules.RESTRICTED to adminE2EByDefault
                        )

                )
            }
        }
    }

    override fun handle(action: CreateRoomAction) {
        when (action) {
            is CreateRoomAction.SetAvatar             -> setAvatar(action)
            is CreateRoomAction.SetName               -> setName(action)
            is CreateRoomAction.SetTopic              -> setTopic(action)
            is CreateRoomAction.SetVisibility         -> setVisibility(action)
            is CreateRoomAction.SetRoomAliasLocalPart -> setRoomAliasLocalPart(action)
            is CreateRoomAction.SetIsEncrypted        -> setIsEncrypted(action)
            is CreateRoomAction.Create                -> doCreateRoom()
            CreateRoomAction.Reset                    -> doReset()
            CreateRoomAction.ToggleShowAdvanced       -> toggleShowAdvanced()
            is CreateRoomAction.DisableFederation     -> disableFederation(action)
        }
    }

    private fun disableFederation(action: CreateRoomAction.DisableFederation) {
        setState {
            copy(disableFederation = action.disableFederation)
        }
    }

    private fun toggleShowAdvanced() {
        setState {
            copy(
                    showAdvanced = !showAdvanced,
                    
                    disableFederation = disableFederation && !showAdvanced
            )
        }
    }

    private fun doReset() {
        setState {
            
            avatarUri?.let { tryOrNull { it.toFile().delete() } }

            CreateRoomViewState(
                    isEncrypted = adminE2EByDefault,
                    hsAdminHasDisabledE2E = !adminE2EByDefault,
                    parentSpaceId = this.parentSpaceId
            )
        }

        _viewEvents.post(CreateRoomViewEvents.Quit)
    }

    private fun setAvatar(action: CreateRoomAction.SetAvatar) = setState { copy(avatarUri = action.imageUri) }

    private fun setName(action: CreateRoomAction.SetName) = setState { copy(roomName = action.name) }

    private fun setTopic(action: CreateRoomAction.SetTopic) = setState { copy(roomTopic = action.topic) }

    private fun setVisibility(action: CreateRoomAction.SetVisibility) = setState {
        when (action.rule) {
            RoomJoinRules.PUBLIC     -> {
                copy(
                        roomJoinRules = RoomJoinRules.PUBLIC,
                        
                        asyncCreateRoomRequest = Uninitialized,
                        isEncrypted = false
                )
            }
            RoomJoinRules.RESTRICTED -> {
                copy(
                        roomJoinRules = RoomJoinRules.RESTRICTED,
                        
                        asyncCreateRoomRequest = Uninitialized,
                        isEncrypted = adminE2EByDefault
                )
            }
            else                     -> {
                
                copy(
                        roomJoinRules = RoomJoinRules.INVITE,
                        isEncrypted = adminE2EByDefault
                )
            }
        }
    }

    private fun setRoomAliasLocalPart(action: CreateRoomAction.SetRoomAliasLocalPart) {
        setState {
            copy(
                    aliasLocalPart = action.aliasLocalPart,
                    
                    asyncCreateRoomRequest = Uninitialized
            )
        }
    }

    private fun setIsEncrypted(action: CreateRoomAction.SetIsEncrypted) = setState { copy(isEncrypted = action.isEncrypted) }

    private fun doCreateRoom() = withState { state ->
        if (state.asyncCreateRoomRequest is Loading || state.asyncCreateRoomRequest is Success) {
            return@withState
        }

        if ( state.roomName.isNullOrBlank()) {
            
            setState {
                copy(asyncCreateRoomRequest = Fail(CreateRoomFailure.NameError(RoomNameError.NameNotNull)))
            }
            return@withState
        }

        if (state.roomJoinRules == RoomJoinRules.PUBLIC && state.aliasLocalPart.isNullOrBlank()) {
            
            setState {
                copy(asyncCreateRoomRequest = Fail(CreateRoomFailure.AliasError(RoomAliasError.AliasIsBlank)))
            }
            return@withState
        }

        setState {
            copy(asyncCreateRoomRequest = Loading())
        }

        val createRoomParams = CreateRoomParams()
                .apply {
                    name = state.roomName.takeIf { it.isNotBlank() }
                    topic = state.roomTopic.takeIf { it.isNotBlank() }
                    avatarUri = state.avatarUri

                    if (state.isSubSpace) {
                        
                        roomType = RoomType.SPACE

                        
                        
                        powerLevelContentOverride = PowerLevelsContent(
                                eventsDefault = 100
                        )
                    }

                    when (state.roomJoinRules) {
                        RoomJoinRules.PUBLIC     -> {
                            
                            visibility = RoomDirectoryVisibility.PUBLIC
                            
                            preset = CreateRoomPreset.PRESET_PUBLIC_CHAT
                            roomAliasName = state.aliasLocalPart
                        }
                        RoomJoinRules.RESTRICTED -> {
                            state.parentSpaceId?.let {
                                featurePreset = RestrictedRoomPreset(
                                        session.getHomeServerCapabilities(),
                                        listOf(RoomJoinRulesAllowEntry.restrictedToRoom(state.parentSpaceId))
                                )
                            }
                        }
                        else                     -> {
                            
                            
                            visibility = RoomDirectoryVisibility.PRIVATE
                            
                            preset = CreateRoomPreset.PRESET_PRIVATE_CHAT
                        }
                    }
                    
                    disableFederation = state.disableFederation

                    
                    val shouldEncrypt = when (state.roomJoinRules) {
                        
                        RoomJoinRules.PUBLIC -> false
                        else                 -> state.isEncrypted ?: state.defaultEncrypted[state.roomJoinRules].orFalse()
                    }
                    if (shouldEncrypt) {
                        enableEncryption()
                    }
                }

        
        viewModelScope.launch {
            runCatching { session.createRoom(createRoomParams) }.fold(
                    { roomId ->
                        analyticsTracker.capture(CreatedRoom(isDM = createRoomParams.isDirect.orFalse()))
                        if (state.parentSpaceId != null) {
                            
                            try {
                                session.spaceService()
                                        .getSpace(state.parentSpaceId)
                                        ?.addChildren(roomId, viaServers = null, order = null)
                            } catch (failure: Throwable) {
                                Timber.w(failure, "Failed to add as a child")
                            }
                        }

                        setState {
                            copy(asyncCreateRoomRequest = Success(roomId))
                        }
                    },
                    { failure ->
                        setState {
                            copy(asyncCreateRoomRequest = Fail(failure))
                        }
                        _viewEvents.post(CreateRoomViewEvents.Failure(failure))
                    }
            )
        }
    }
}