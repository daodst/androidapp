

package im.vector.app.features.roommemberprofile.devices

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo

sealed class DeviceListAction : VectorViewModelAction {
    data class SelectDevice(val device: CryptoDeviceInfo) : DeviceListAction()
    object DeselectDevice : DeviceListAction()

    data class ManuallyVerify(val deviceId: String) : DeviceListAction()
}
