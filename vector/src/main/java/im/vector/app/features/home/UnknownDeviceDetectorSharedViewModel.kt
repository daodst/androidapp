

package im.vector.app.features.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.settings.VectorPreferences
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.flow.flow
import timber.log.Timber

data class UnknownDevicesState(
        val myMatrixItem: MatrixItem.UserItem? = null,
        val unknownSessions: Async<List<DeviceDetectionInfo>> = Uninitialized
) : MavericksState

data class DeviceDetectionInfo(
        val deviceInfo: DeviceInfo,
        val isNew: Boolean,
        val currentSessionTrust: Boolean
)

class UnknownDeviceDetectorSharedViewModel @AssistedInject constructor(@Assisted initialState: UnknownDevicesState,
                                                                       session: Session,
                                                                       private val vectorPreferences: VectorPreferences) :
        VectorViewModel<UnknownDevicesState, UnknownDeviceDetectorSharedViewModel.Action, EmptyViewEvents>(initialState) {

    sealed class Action : VectorViewModelAction {
        data class IgnoreDevice(val deviceIds: List<String>) : Action()
    }

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<UnknownDeviceDetectorSharedViewModel, UnknownDevicesState> {
        override fun create(initialState: UnknownDevicesState): UnknownDeviceDetectorSharedViewModel
    }

    companion object : MavericksViewModelFactory<UnknownDeviceDetectorSharedViewModel, UnknownDevicesState> by hiltMavericksViewModelFactory()

    private val ignoredDeviceList = ArrayList<String>()

    init {

        val currentSessionTs = session.cryptoService().getCryptoDeviceInfo(session.myUserId)
                .firstOrNull { it.deviceId == session.sessionParams.deviceId }
                ?.firstTimeSeenLocalTs
                ?: System.currentTimeMillis()
        Timber.v("## Detector - Current Session first time seen $currentSessionTs")

        ignoredDeviceList.addAll(
                vectorPreferences.getUnknownDeviceDismissedList().also {
                    Timber.v("## Detector - Remembered ignored list $it")
                }
        )

        combine(
                session.flow().liveUserCryptoDevices(session.myUserId),
                session.flow().liveMyDevicesInfo(),
                session.flow().liveCrossSigningPrivateKeys()
        ) { cryptoList, infoList, pInfo ->
            
            infoList
                    .filter { info ->
                        
                        cryptoList.firstOrNull { info.deviceId == it.deviceId }?.isVerified?.not().orFalse()
                    }
                    
                    .filter { !ignoredDeviceList.contains(it.deviceId) }
                    .sortedByDescending { it.lastSeenTs }
                    .map { deviceInfo ->
                        val deviceKnownSince = cryptoList.firstOrNull { it.deviceId == deviceInfo.deviceId }?.firstTimeSeenLocalTs ?: 0
                        DeviceDetectionInfo(
                                deviceInfo,
                                deviceKnownSince > currentSessionTs + 60_000, 
                                pInfo.getOrNull()?.selfSigned != null 
                        )
                    }
        }
                .distinctUntilChanged()
                .execute { async ->
                    
                    copy(
                            myMatrixItem = session.getUser(session.myUserId)?.toMatrixItem(),
                            unknownSessions = async
                    )
                }

        session.flow().liveUserCryptoDevices(session.myUserId)
                .distinctUntilChanged()
                .sample(5_000)
                .onEach {
                    
                    session.cryptoService().fetchDevicesList(NoOpMatrixCallback())
                }
                .launchIn(viewModelScope)

        
        session.cryptoService().fetchDevicesList(NoOpMatrixCallback())
    }

    override fun handle(action: Action) {
        when (action) {
            is Action.IgnoreDevice -> {
                ignoredDeviceList.addAll(action.deviceIds)
                
                withState { state ->
                    state.unknownSessions.invoke()?.let { detectedSessions ->
                        val updated = detectedSessions.filter { !action.deviceIds.contains(it.deviceInfo.deviceId) }
                        setState {
                            copy(unknownSessions = Success(updated))
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        vectorPreferences.storeUnknownDeviceDismissedList(ignoredDeviceList)
        super.onCleared()
    }
}
