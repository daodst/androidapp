

package im.vector.app.features.call

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.features.call.audio.CallAudioManager
import org.matrix.android.sdk.api.session.call.TurnServerResponse

sealed class VectorCallViewEvents : VectorViewEvents {

    data class ConnectionTimeout(val turn: TurnServerResponse?) : VectorCallViewEvents()
    object ConnectionSelfTimeout : VectorCallViewEvents()
    data class ShowSoundDeviceChooser(
            val available: Set<CallAudioManager.Device>,
            val current: CallAudioManager.Device
    ) : VectorCallViewEvents()

    object ShowDialPad : VectorCallViewEvents()
    object ShowCallTransferScreen : VectorCallViewEvents()
    object FailToTransfer : VectorCallViewEvents()
    object ShowScreenSharingPermissionDialog : VectorCallViewEvents()
    object StopScreenSharingService : VectorCallViewEvents()
}
