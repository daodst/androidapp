

package im.vector.app.features.roomprofile.settings.joinrule.advanced

import im.vector.app.core.platform.VectorViewEvents

sealed class RoomJoinRuleChooseRestrictedEvents : VectorViewEvents {
    object NavigateToChooseRestricted : RoomJoinRuleChooseRestrictedEvents()
    data class NavigateToUpgradeRoom(val roomId: String, val toVersion: String, val description: CharSequence) : RoomJoinRuleChooseRestrictedEvents()
}
