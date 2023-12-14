

package im.vector.app.features.roomprofile.settings

import im.vector.app.core.platform.VectorViewEvents


sealed class RoomSettingsViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : RoomSettingsViewEvents()
    object Success : RoomSettingsViewEvents()
    object GoBack : RoomSettingsViewEvents()
}
