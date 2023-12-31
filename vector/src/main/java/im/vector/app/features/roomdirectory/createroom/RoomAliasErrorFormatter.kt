

package im.vector.app.features.roomdirectory.createroom

import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import org.matrix.android.sdk.api.session.room.alias.RoomAliasError
import javax.inject.Inject

class RoomAliasErrorFormatter @Inject constructor(
        private val stringProvider: StringProvider
) {
    fun format(roomAliasError: RoomAliasError?): String? {
        return when (roomAliasError) {
            is RoomAliasError.AliasIsBlank      -> R.string.create_room_alias_empty
            is RoomAliasError.AliasNotAvailable -> R.string.create_room_alias_already_in_use
            is RoomAliasError.AliasInvalid      -> R.string.create_room_alias_invalid
            else                                -> null
        }
                ?.let { stringProvider.getString(it) }
    }
}
