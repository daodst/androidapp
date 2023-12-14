

package im.vector.app.features.debug.analytics

import im.vector.app.core.platform.VectorViewModelAction

sealed interface DebugAnalyticsViewActions : VectorViewModelAction {
    object ResetAnalyticsOptInDisplayed : DebugAnalyticsViewActions
}
