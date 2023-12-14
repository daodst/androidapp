

package im.vector.app.features.roomprofile.daily

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.app.R
import im.vector.app.core.epoxy.dividerItem
import im.vector.app.core.epoxy.profiles.buildProfileSection
import im.vector.app.core.epoxy.profiles.profileMatrixItem
import im.vector.app.core.epoxy.profiles.profileMatrixItemWithPowerLevelWithPresence
import im.vector.app.core.extensions.join
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.home.AvatarRenderer
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomThirdPartyInviteContent
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class RoomMemberDailyListController @Inject constructor(
        private val avatarRenderer: AvatarRenderer,
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val roomMemberSummaryFilter: RoomMemberDailySummaryFilter
) : TypedEpoxyController<RoomMemberListViewState>() {

    interface Callback {
        fun onRoomMemberClicked(roomMember: RoomMemberSummary)
        fun onThreePidInviteClicked(event: Event)
    }

    var callback: Callback? = null

    override fun buildModels(data: RoomMemberListViewState?) {
        data ?: return
        val host = this

        roomMemberSummaryFilter.filter = data.filter

        val roomMembersByPowerLevel = data.roomMemberSummaries.invoke() ?: return

        val filteredThreePidInvites = data.threePidInvites()
                ?.filter { event ->
                    event.content.toModel<RoomThirdPartyInviteContent>()
                            ?.takeIf {
                                data.filter.isEmpty() || it.displayName?.contains(data.filter, ignoreCase = true) == true
                            } != null
                }
                .orEmpty()
        var threePidInvitesDone = filteredThreePidInvites.isEmpty()

        for ((powerLevelCategory, roomMemberList) in roomMembersByPowerLevel) {
            val filteredRoomMemberList = roomMemberList.filter { roomMemberSummaryFilter.test(it) }
            if (filteredRoomMemberList.isEmpty()) {
                continue
            }

            if (powerLevelCategory == RoomMemberListCategories.USER && !threePidInvitesDone) {
                
                buildProfileSection(
                        stringProvider.getString(RoomMemberListCategories.INVITE.titleRes)
                )

                buildThreePidInvites(filteredThreePidInvites, data.actionsPermissions.canRevokeThreePidInvite)
                threePidInvitesDone = true
            }

            buildProfileSection(
                    stringProvider.getString(powerLevelCategory.titleRes)
            )

            filteredRoomMemberList.join(
                    each = { _, roomMember ->
                        buildRoomMember(roomMember, powerLevelCategory, host, data)
                    },
                    between = { _, roomMemberBefore ->
                        dividerItem {
                            id("divider_${roomMemberBefore.userId}")
                        }
                    }
            )
            if (powerLevelCategory == RoomMemberListCategories.INVITE && !threePidInvitesDone) {
                
                dividerItem {
                    id("divider_threepidinvites")
                }

                buildThreePidInvites(filteredThreePidInvites, data.actionsPermissions.canRevokeThreePidInvite)
                threePidInvitesDone = true
            }
        }

        if (!threePidInvitesDone) {
            
            buildProfileSection(
                    stringProvider.getString(RoomMemberListCategories.INVITE.titleRes)
            )

            buildThreePidInvites(filteredThreePidInvites, data.actionsPermissions.canRevokeThreePidInvite)
        }
    }

    private fun buildRoomMember(roomMember: RoomMemberSummary,
                                powerLevelCategory: RoomMemberListCategories,
                                host: RoomMemberDailyListController,
                                data: RoomMemberListViewState) {
        val powerLabel = stringProvider.getString(powerLevelCategory.titleRes)

        profileMatrixItemWithPowerLevelWithPresence {
            id(roomMember.userId)
            matrixItem(roomMember.toMatrixItem())
            avatarRenderer(host.avatarRenderer)
            userEncryptionTrustLevel(data.trustLevelMap.invoke()?.get(roomMember.userId))
            clickListener {
                host.callback?.onRoomMemberClicked(roomMember)
            }
            showPresence(true)
            userPresence(roomMember.userPresence)
            ignoredUser(roomMember.userId in data.ignoredUserIds)
            powerLevelLabel(
                    span {
                        span(powerLabel) {
                            textColor = host.colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
                        }
                    }
            )
        }
    }

    private fun buildThreePidInvites(filteredThreePidInvites: List<Event>, canRevokeThreePidInvite: Boolean) {
        val host = this
        filteredThreePidInvites
                .join(
                        each = { idx, event ->
                            event.content.toModel<RoomThirdPartyInviteContent>()
                                    ?.let { content ->
                                        profileMatrixItem {
                                            id("3pid_$idx")
                                            matrixItem(MatrixItem.UserItem("@", displayName = content.displayName))
                                            avatarRenderer(host.avatarRenderer)
                                            editable(canRevokeThreePidInvite)
                                            clickListener {
                                                host.callback?.onThreePidInviteClicked(event)
                                            }
                                        }
                                    }
                        },
                        between = { idx, _ ->
                            dividerItem {
                                id("divider3_$idx")
                            }
                        }
                )
    }
}
