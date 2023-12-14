

package im.vector.app.features.me

import com.airbnb.mvrx.MavericksState
import org.matrix.android.sdk.api.session.user.model.User

data class VectorMeViewState(
        val user: User? = null,
        val level: Int? = null,
) : MavericksState {

}


