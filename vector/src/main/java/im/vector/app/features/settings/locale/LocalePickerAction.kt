

package im.vector.app.features.settings.locale

import im.vector.app.core.platform.VectorViewModelAction
import java.util.Locale

sealed class LocalePickerAction : VectorViewModelAction {
    data class SelectLocale(val locale: Locale) : LocalePickerAction()
}
