

package im.vector.app.features.settings.devices

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo

data class DeviceVerificationInfoBottomSheetViewState(
        val deviceId: String,
        val cryptoDeviceInfo: Async<CryptoDeviceInfo?> = Uninitialized,
        val deviceInfo: Async<DeviceInfo> = Uninitialized,
        val hasAccountCrossSigning: Boolean = false,
        val accountCrossSigningIsTrusted: Boolean = false,
        val isMine: Boolean = false,
        val hasOtherSessions: Boolean = false,
        val isRecoverySetup: Boolean = false
) : MavericksState {

    constructor(args: DeviceVerificationInfoArgs) : this(deviceId = args.deviceId)

    val canVerifySession = hasOtherSessions || isRecoverySetup
}
