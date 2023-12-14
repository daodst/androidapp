

package im.vector.app.features.analytics.ui.consent

import im.vector.app.core.platform.VectorViewEvents

sealed interface AnalyticsOptInViewEvents : VectorViewEvents {
    object OnDataSaved : AnalyticsOptInViewEvents
}
