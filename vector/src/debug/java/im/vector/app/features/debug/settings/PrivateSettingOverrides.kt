

package im.vector.app.features.debug.settings

sealed interface BooleanHomeserverCapabilitiesOverride : OverrideOption {

    companion object {
        fun from(value: Boolean?) = when (value) {
            null  -> null
            true  -> ForceEnabled
            false -> ForceDisabled
        }
    }

    object ForceEnabled : BooleanHomeserverCapabilitiesOverride {
        override val label = "Force enabled"
    }

    object ForceDisabled : BooleanHomeserverCapabilitiesOverride {
        override val label = "Force disabled"
    }
}

fun BooleanHomeserverCapabilitiesOverride?.toBoolean() = when (this) {
    null                                                -> null
    BooleanHomeserverCapabilitiesOverride.ForceDisabled -> false
    BooleanHomeserverCapabilitiesOverride.ForceEnabled  -> true
}
