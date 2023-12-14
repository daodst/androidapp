

package im.vector.app.features.rageshake

import com.airbnb.mvrx.MavericksState

data class BugReportState(
        val serverVersion: String = ""
) : MavericksState
