

package im.vector.app.core.platform

import com.airbnb.mvrx.MavericksState

data class VectorDummyViewState(
        val isDummy: Unit = Unit
) : MavericksState
