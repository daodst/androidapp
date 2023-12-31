

package im.vector.app.features.home.room.typing

import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import javax.inject.Inject

class TypingHelper @Inject constructor(private val stringProvider: StringProvider) {

    
    fun getTypingMessage(typingUsers: List<SenderInfo>): String {
        return when {
            typingUsers.isEmpty() ->
                ""
            typingUsers.size == 1 ->
                stringProvider.getString(R.string.room_one_user_is_typing, typingUsers[0].disambiguatedDisplayName)
            typingUsers.size == 2 ->
                stringProvider.getString(R.string.room_two_users_are_typing,
                        typingUsers[0].disambiguatedDisplayName,
                        typingUsers[1].disambiguatedDisplayName)
            else                  ->
                stringProvider.getString(R.string.room_many_users_are_typing,
                        typingUsers[0].disambiguatedDisplayName,
                        typingUsers[1].disambiguatedDisplayName)
        }
    }

    fun getNotificationTypingMessage(typingUsers: List<SenderInfo>): String {
        return when {
            typingUsers.isEmpty() -> ""
            typingUsers.size == 1 -> typingUsers[0].disambiguatedDisplayName
            typingUsers.size == 2 -> stringProvider.getString(R.string.room_notification_two_users_are_typing,
                    typingUsers[0].disambiguatedDisplayName, typingUsers[1].disambiguatedDisplayName)
            else                  -> stringProvider.getString(R.string.room_notification_more_than_two_users_are_typing,
                    typingUsers[0].disambiguatedDisplayName, typingUsers[1].disambiguatedDisplayName)
        }
    }
}
