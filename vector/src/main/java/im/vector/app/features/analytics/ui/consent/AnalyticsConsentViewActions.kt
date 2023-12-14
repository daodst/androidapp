

package im.vector.app.features.analytics.ui.consent

import im.vector.app.core.platform.VectorViewModelAction

sealed class AnalyticsConsentViewActions : VectorViewModelAction {
    data class SetUserConsent(val userConsent: Boolean) : AnalyticsConsentViewActions()
}
