

package im.vector.app.features.settings.ignored

import com.airbnb.mvrx.MavericksState
import org.matrix.android.sdk.api.session.user.model.User

data class IgnoredUsersViewState(
        val ignoredUsers: List<User> = emptyList(),
        val isLoading: Boolean = false
) : MavericksState
