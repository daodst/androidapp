

package im.vector.app.features.home

import androidx.annotation.StringRes
import im.vector.app.R

enum class RoomListDisplayMode(@StringRes val titleRes: Int) {
    NOTIFICATIONS(R.string.bottom_action_notification),
    GROUP(R.string.bottom_action_rooms_group),
    PEOPLE(R.string.bottom_action_people_x),
    ROOMS(R.string.bottom_action_rooms),
    FILTERED( 0)
}
