

package im.vector.app.features.call

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.call.audio.CallAudioManager
import im.vector.app.features.call.transfer.CallTransferResult

sealed class VectorCallViewActions : VectorViewModelAction {
    object EndCall : VectorCallViewActions()
    object AcceptCall : VectorCallViewActions()
    object DeclineCall : VectorCallViewActions()
    object ToggleMute : VectorCallViewActions()
    object ToggleVideo : VectorCallViewActions()
    object ToggleHoldResume : VectorCallViewActions()
    data class ChangeAudioDevice(val device: CallAudioManager.Device) : VectorCallViewActions()
    object OpenDialPad : VectorCallViewActions()
    data class SendDtmfDigit(val digit: String) : VectorCallViewActions()
    data class SwitchCall(val callArgs: CallArgs) : VectorCallViewActions()

    object SwitchSoundDevice : VectorCallViewActions()
    object HeadSetButtonPressed : VectorCallViewActions()
    object ToggleCamera : VectorCallViewActions()
    object ToggleHDSD : VectorCallViewActions()
    object InitiateCallTransfer : VectorCallViewActions()
    object CallTransferSelectionCancelled : VectorCallViewActions()
    data class CallTransferSelectionResult(val callTransferResult: CallTransferResult) : VectorCallViewActions()
    object TransferCall : VectorCallViewActions()
    object ToggleScreenSharing : VectorCallViewActions()
    object StartScreenSharing : VectorCallViewActions()
}
