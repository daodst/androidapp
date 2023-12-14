

package im.vector.app.features.roomdirectory.createroom

import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import org.matrix.android.sdk.api.session.room.name.RoomNameError
import javax.inject.Inject

class RoomNameErrorFormatter @Inject constructor(
        private val stringProvider: StringProvider
) {

    fun format(roomAliasError: RoomNameError?): String? {
        return when (roomAliasError) {
            is RoomNameError.NameNotNull -> R.string.create_room_name_empty
            else                         -> null
        }
                ?.let { stringProvider.getString(it) }
    }


}
