

package im.vector.app.features.debug.analytics

import com.airbnb.mvrx.MavericksState

data class DebugAnalyticsViewState(
        val analyticsId: String? = null,
        val userConsent: Boolean = false,
        val didAskUserConsent: Boolean = false
) : MavericksState
