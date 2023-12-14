

package im.vector.app.features.settings.locale

import im.vector.app.core.platform.VectorViewEvents

sealed class LocalePickerViewEvents : VectorViewEvents {
    object RestartActivity : LocalePickerViewEvents()
}
