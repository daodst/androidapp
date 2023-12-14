

package im.vector.app.features.analytics.ui.consent

import com.airbnb.mvrx.MavericksState

data class AnalyticsConsentViewState(
        val userConsent: Boolean = false,
        val didAskUserConsent: Boolean = false
) : MavericksState
