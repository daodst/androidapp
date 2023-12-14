

package im.vector.app.features.debug.settings

import im.vector.app.core.platform.VectorViewModelAction

sealed interface DebugPrivateSettingsViewActions : VectorViewModelAction {
    data class SetDialPadVisibility(val force: Boolean) : DebugPrivateSettingsViewActions
    data class SetForceLoginFallbackEnabled(val force: Boolean) : DebugPrivateSettingsViewActions
    data class SetDisplayNameCapabilityOverride(val option: BooleanHomeserverCapabilitiesOverride?) : DebugPrivateSettingsViewActions
    data class SetAvatarCapabilityOverride(val option: BooleanHomeserverCapabilitiesOverride?) : DebugPrivateSettingsViewActions
}
