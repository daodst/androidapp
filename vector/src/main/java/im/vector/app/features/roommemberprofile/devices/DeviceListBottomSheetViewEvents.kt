

package im.vector.app.features.roommemberprofile.devices

import im.vector.app.core.platform.VectorViewEvents


sealed class DeviceListBottomSheetViewEvents : VectorViewEvents {
    data class Verify(val userId: String, val txID: String) : DeviceListBottomSheetViewEvents()
}
