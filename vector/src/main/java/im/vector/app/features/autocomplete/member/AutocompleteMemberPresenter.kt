

package im.vector.app.features.autocomplete.member

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.R
import im.vector.app.features.autocomplete.AutocompleteClickListener
import im.vector.app.features.autocomplete.RecyclerViewPresenter
import org.matrix.android.sdk.api.pushrules.SenderNotificationPermissionCondition
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.members.RoomMemberQueryParams
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.util.MatrixItem

class AutocompleteMemberPresenter @AssistedInject constructor(context: Context,
                                                              @Assisted val roomId: String,
                                                              private val session: Session,
                                                              private val controller: AutocompleteMemberController
) : RecyclerViewPresenter<AutocompleteMemberItem>(context), AutocompleteClickListener<AutocompleteMemberItem> {

    

    private val room by lazy { session.getRoom(roomId)!! }

    

    init {
        controller.listener = this
    }

    

    fun clear() {
        controller.listener = null
    }

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): AutocompleteMemberPresenter
    }

    

    override fun instantiateAdapter(): RecyclerView.Adapter<*> {
        return controller.adapter
    }

    override fun onItemClick(t: AutocompleteMemberItem) {
        dispatchClick(t)
    }

    override fun onQuery(query: CharSequence?) {
        val queryParams = createQueryParams(query)
        val membersHeader = createMembersHeader()
        val members = createMemberItems(queryParams)
        val everyone = createEveryoneItem(query)
        
        val canAddHeaders = canNotifyEveryone()

        val items = mutableListOf<AutocompleteMemberItem>().apply {
            if (members.isNotEmpty()) {
                if (canAddHeaders) {
                    add(membersHeader)
                }
                addAll(members)
            }
            everyone?.let {
                val everyoneHeader = createEveryoneHeader()
                add(everyoneHeader)
                add(it)
            }
        }

        controller.setData(items)
    }

    

    private fun createQueryParams(query: CharSequence?) = roomMemberQueryParams {
        displayName = if (query.isNullOrBlank()) {
            QueryStringValue.IsNotEmpty
        } else {
            QueryStringValue.Contains(query.toString(), QueryStringValue.Case.INSENSITIVE)
        }
        memberships = listOf(Membership.JOIN)
        excludeSelf = true
    }

    private fun createMembersHeader() =
            AutocompleteMemberItem.Header(
                    ID_HEADER_MEMBERS,
                    context.getString(R.string.room_message_autocomplete_users)
            )

    private fun createMemberItems(queryParams: RoomMemberQueryParams) =
            room.getRoomMembers(queryParams)
                    .asSequence()
                    .sortedBy { it.displayName }
                    .disambiguate()
                    .map { AutocompleteMemberItem.RoomMember(it) }
                    .toList()

    private fun createEveryoneHeader() =
            AutocompleteMemberItem.Header(
                    ID_HEADER_EVERYONE,
                    context.getString(R.string.room_message_autocomplete_notification)
            )

    private fun createEveryoneItem(query: CharSequence?) =
            room.roomSummary()
                    ?.takeIf { canNotifyEveryone() }
                    ?.takeIf { query.isNullOrBlank() || MatrixItem.NOTIFY_EVERYONE.startsWith("@$query") }
                    ?.let {
                        AutocompleteMemberItem.Everyone(it)
                    }

    private fun canNotifyEveryone() = session.resolveSenderNotificationPermissionCondition(
            Event(
                    senderId = session.myUserId,
                    roomId = roomId
            ),
            SenderNotificationPermissionCondition(PowerLevelsContent.NOTIFICATIONS_ROOM_KEY)
    )

    

    companion object {
        private const val ID_HEADER_MEMBERS = "ID_HEADER_MEMBERS"
        private const val ID_HEADER_EVERYONE = "ID_HEADER_EVERYONE"
    }
}

private fun Sequence<RoomMemberSummary>.disambiguate(): Sequence<RoomMemberSummary> {
    val displayNames = hashMapOf<String, Int>().also { map ->
        for (item in this) {
            item.displayName?.lowercase()?.also { displayName ->
                map[displayName] = map.getOrPut(displayName, { 0 }) + 1
            }
        }
    }

    return map { roomMemberSummary ->
        if (displayNames[roomMemberSummary.displayName?.lowercase()] ?: 0 > 1) {
            roomMemberSummary.copy(displayName = roomMemberSummary.displayName + " " + roomMemberSummary.userId)
        } else roomMemberSummary
    }
}
