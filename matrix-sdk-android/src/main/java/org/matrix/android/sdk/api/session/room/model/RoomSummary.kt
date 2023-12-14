

package org.matrix.android.sdk.api.session.room.model

import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.api.session.presence.model.UserPresence
import org.matrix.android.sdk.api.session.room.model.tag.RoomTag
import org.matrix.android.sdk.api.session.room.send.UserDraft
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent


data class RoomSummary(
        
        val atMsg: List<TimelineEvent> = emptyList(),
        
        val roomId: String,
        val owner: String = "",
        val level: Int? = null,
        
        val displayName: String = "",
        val name: String = "",
        val topic: String = "",
        val avatarUrl: String = "",
        val canonicalAlias: String? = null,
        val aliases: List<String> = emptyList(),
        val joinRules: RoomJoinRules? = null,
        val isDirect: Boolean = false,
        val izUpgrade: Boolean = false,
        val isGroup: Boolean = false,
        val isDvmGroup: Boolean = false,
        val directUserId: String? = null,
        val directUserPresence: UserPresence? = null,
        val joinedMembersCount: Int? = 0,
        val invitedMembersCount: Int? = 0,
        val latestPreviewableEvent: TimelineEvent? = null,
        val otherMemberIds: List<String> = emptyList(),
        val notificationCount: Int = 0,
        val highlightCount: Int = 0,
        val hasUnreadMessages: Boolean = false,
        val tags: List<RoomTag> = emptyList(),
        val membership: Membership = Membership.NONE,
        val versioningState: VersioningState = VersioningState.NONE,
        val readMarkerId: String? = null,
        val userDrafts: List<UserDraft> = emptyList(),
        val isEncrypted: Boolean,
        val encryptionEventTs: Long?,
        val typingUsers: List<SenderInfo>,
        val inviterId: String? = null,
        val breadcrumbsIndex: Int = NOT_IN_BREADCRUMBS,
        val roomEncryptionTrustLevel: RoomEncryptionTrustLevel? = null,
        val hasFailedSending: Boolean = false,
        val roomType: String? = null,
        val spaceParents: List<SpaceParentInfo>? = null,
        val spaceChildren: List<SpaceChildInfo>? = null,
        val flattenParentIds: List<String> = emptyList(),
        val roomEncryptionAlgorithm: RoomEncryptionAlgorithm? = null
) {

    val isVersioned: Boolean
        get() = versioningState != VersioningState.NONE

    val hasNewMessages: Boolean
        get() = notificationCount != 0

    val isLowPriority: Boolean
        get() = hasTag(RoomTag.ROOM_TAG_LOW_PRIORITY)

    val isFavorite: Boolean
        get() = hasTag(RoomTag.ROOM_TAG_FAVOURITE)

    val isPublic: Boolean
        get() = joinRules == RoomJoinRules.PUBLIC

    fun hasTag(tag: String) = tags.any { it.name == tag }

    val canStartCall: Boolean
        get() = joinedMembersCount == 2

    companion object {
        const val NOT_IN_BREADCRUMBS = -1
    }
}
