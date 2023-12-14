

package im.vector.app.features.debug.settings

import com.airbnb.mvrx.MavericksState
import im.vector.app.features.debug.settings.OverrideDropdownView.OverrideDropdown

data class DebugPrivateSettingsViewState(
        val dialPadVisible: Boolean = false,
        val forceLoginFallback: Boolean = false,
        val homeserverCapabilityOverrides: HomeserverCapabilityOverrides = HomeserverCapabilityOverrides()
) : MavericksState

data class HomeserverCapabilityOverrides(
        val displayName: OverrideDropdown<BooleanHomeserverCapabilitiesOverride> = OverrideDropdown(
                label = "Override display name capability",
                activeOption = null,
                options = listOf(BooleanHomeserverCapabilitiesOverride.ForceEnabled, BooleanHomeserverCapabilitiesOverride.ForceDisabled)
        ),
        val avatar: OverrideDropdown<BooleanHomeserverCapabilitiesOverride> = OverrideDropdown(
                label = "Override avatar capability",
                activeOption = null,
                options = listOf(BooleanHomeserverCapabilitiesOverride.ForceEnabled, BooleanHomeserverCapabilitiesOverride.ForceDisabled)
        )
)
