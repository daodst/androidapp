

package im.vector.app.features.call.conference

import androidx.lifecycle.asFlow
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.widgets.model.Widget
import org.matrix.android.sdk.api.session.widgets.model.WidgetType

class JitsiCallViewModel @AssistedInject constructor(
        @Assisted initialState: JitsiCallViewState,
        private val session: Session,
        private val jitsiService: JitsiService
) : VectorViewModel<JitsiCallViewState, JitsiCallViewActions, JitsiCallViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<JitsiCallViewModel, JitsiCallViewState> {
        override fun create(initialState: JitsiCallViewState): JitsiCallViewModel
    }

    private var currentWidgetObserver: Job? = null
    private val widgetService = session.widgetService()

    private var confIsJoined = false
    private var pendingArgs: VectorJitsiActivity.Args? = null

    init {
        observeWidget(initialState.roomId, initialState.widgetId)
    }

    private fun observeWidget(roomId: String, widgetId: String) {
        confIsJoined = false
        currentWidgetObserver?.cancel()
        currentWidgetObserver = widgetService.getRoomWidgetsLive(roomId, QueryStringValue.Equals(widgetId), WidgetType.Jitsi.values())
                .asFlow()
                .distinctUntilChanged()
                .onEach {
                    val jitsiWidget = it.firstOrNull()
                    if (jitsiWidget != null) {
                        setState {
                            copy(widget = Success(jitsiWidget))
                        }
                        if (!confIsJoined) {
                            confIsJoined = true
                            joinConference(jitsiWidget)
                        }
                    } else {
                        setState {
                            copy(
                                    widget = Fail(IllegalArgumentException("Widget not found"))
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
    }

    private fun joinConference(jitsiWidget: Widget) = withState { state ->
        viewModelScope.launch {
            try {
                val joinConference = jitsiService.joinConference(state.roomId, jitsiWidget, state.enableVideo)
                _viewEvents.post(joinConference)
            } catch (throwable: Throwable) {
                _viewEvents.post(JitsiCallViewEvents.FailJoiningConference)
            }
        }
    }

    override fun handle(action: JitsiCallViewActions) {
        when (action) {
            is JitsiCallViewActions.SwitchTo      -> handleSwitchTo(action)
            JitsiCallViewActions.OnConferenceLeft -> handleOnConferenceLeft()
        }
    }

    private fun handleSwitchTo(action: JitsiCallViewActions.SwitchTo) = withState { state ->
        
        if (action.args.roomId != state.roomId ||
                action.args.widgetId != state.widgetId) {
            if (action.withConfirmation) {
                
                viewModelScope.launch {
                    delay(500)
                    _viewEvents.post(JitsiCallViewEvents.ConfirmSwitchingConference(action.args))
                }
            } else {
                
                pendingArgs = action.args
                _viewEvents.post(JitsiCallViewEvents.LeaveConference)
            }
        }
    }

    private fun handleOnConferenceLeft() {
        val safePendingArgs = pendingArgs
        pendingArgs = null

        if (safePendingArgs == null) {
            
            _viewEvents.post(JitsiCallViewEvents.Finish)
        } else {
            setState {
                copy(
                        roomId = safePendingArgs.roomId,
                        widgetId = safePendingArgs.widgetId,
                        enableVideo = safePendingArgs.enableVideo,
                        widget = Uninitialized
                )
            }
            observeWidget(safePendingArgs.roomId, safePendingArgs.widgetId)
        }
    }

    companion object : MavericksViewModelFactory<JitsiCallViewModel, JitsiCallViewState> by hiltMavericksViewModelFactory() {
        const val ENABLE_VIDEO_OPTION = "ENABLE_VIDEO_OPTION"
    }
}
