

package im.vector.app.features.me

import android.content.Context
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.Session


class VectorMeViewModel @AssistedInject constructor(
        @Assisted private val initialState: VectorMeViewState,
        private val session: Session,
        private val context: Context
) : VectorViewModel<VectorMeViewState, VectorMeAction, VectorMeEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<VectorMeViewModel, VectorMeViewState> {
        override fun create(initialState: VectorMeViewState): VectorMeViewModel
    }

    companion object : MavericksViewModelFactory<VectorMeViewModel, VectorMeViewState> by hiltMavericksViewModelFactory()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _viewEvents.post(VectorMeEvents.Loading())
            val user = runCatching {
                session.resolveUserOnline(session.myUserId)
            }.fold({
                it
            }, {
                if (it is Failure.ServerError) {
                    _viewEvents.post(VectorMeEvents.DisLoading)
                    withContext(Dispatchers.Main) {
                        context.toast(it.error.message)
                    }
                    return@launch
                }
                _viewEvents.post(VectorMeEvents.DisLoading)
                it.message?.let {
                    withContext(Dispatchers.Main) {
                        context.toast(it)
                    }
                }
                return@launch
            })
            val level = runCatching {
                session.getLevel(session.myOriginUId)
            }.fold({
                it
            }, {
                return@launch
            })

            setState {
                copy(
                        user = user,
                        level = level
                )
            }
            _viewEvents.post(VectorMeEvents.DisLoading)
        }
    }

    override fun handle(action: VectorMeAction) {

        when (action) {
            is VectorMeAction.RetryFetchingInfo -> handleRetryFetchProfileInfo()
            is VectorMeAction.RefershName       -> refershName(action.message)
        }
    }

    private fun refershName(message: String) = withState {
        setState {
            copy(
                    user = user?.copy(displayName = message),
            )
        }

    }

    private fun handleRetryFetchProfileInfo() {

        viewModelScope.launch(Dispatchers.IO) {
            _viewEvents.post(VectorMeEvents.Loading())
            val user = runCatching {
                session.resolveUserOnline(session.myUserId)
            }.fold({
                it
            }, {
                if (it is Failure.ServerError) {
                    _viewEvents.post(VectorMeEvents.DisLoading)
                    withContext(Dispatchers.Main) {
                        context.toast(it.error.message)
                    }
                    return@launch
                }
                _viewEvents.post(VectorMeEvents.DisLoading)
                it.message?.let {
                    withContext(Dispatchers.Main) {
                        context.toast(it)
                    }
                }
                return@launch
            })
            setState {
                copy(
                        user = user,
                )
            }
            _viewEvents.post(VectorMeEvents.DisLoading)
        }
    }
}
