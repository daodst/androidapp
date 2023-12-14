

package im.vector.app.features.settings.legals

import im.vector.app.core.platform.VectorViewModelAction

sealed interface LegalsAction : VectorViewModelAction {
    object Refresh : LegalsAction
}
