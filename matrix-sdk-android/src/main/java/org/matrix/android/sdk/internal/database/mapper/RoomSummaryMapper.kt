

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.api.session.room.model.RoomEncryptionAlgorithm
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.session.room.model.SpaceParentInfo
import org.matrix.android.sdk.api.session.room.model.tag.RoomTag
import org.matrix.android.sdk.api.session.typing.TypingUsersTracker
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.presence.toUserPresence
import javax.inject.Inject

internal class RoomSummaryMapper @Inject constructor(private val timelineEventMapper: TimelineEventMapper,
                                                     private val typingUsersTracker: TypingUsersTracker) {

    fun map(roomSummaryEntity: RoomSummaryEntity): RoomSummary {
        val tags = roomSummaryEntity.tags().map {
            RoomTag(it.tagName, it.tagOrder)
        }
        val atMsg = roomSummaryEntity.atMsg.map {
            timelineEventMapper.map(it)
        }

        val latestEvent = roomSummaryEntity.latestPreviewableEvent?.let {
            timelineEventMapper.map(it, buildReadReceipts = false)
        }
        
        val typingUsers = typingUsersTracker.getTypingUsers(roomSummaryEntity.roomId)

        
        val owner = roomSummaryEntity.owner ?: ""
        
        return RoomSummary(
                atMsg = atMsg,
                
                roomId = roomSummaryEntity.roomId,
                level = roomSummaryEntity.level,
                displayName = roomSummaryEntity.displayName() ?: "",
                name = roomSummaryEntity.name ?: "",
                topic = roomSummaryEntity.topic ?: "",
                avatarUrl = roomSummaryEntity.avatarUrl ?: "",
                joinRules = roomSummaryEntity.joinRules,
                isDirect = roomSummaryEntity.isDirect,
                owner = owner,
                isGroup = roomSummaryEntity.isGroup,
                izUpgrade = roomSummaryEntity.izUpgrade,
                isDvmGroup = roomSummaryEntity.isDvmGroup,
                directUserId = roomSummaryEntity.directUserId,
                directUserPresence = roomSummaryEntity.directUserPresence?.toUserPresence(),
                latestPreviewableEvent = latestEvent,
                joinedMembersCount = roomSummaryEntity.joinedMembersCount,
                invitedMembersCount = roomSummaryEntity.invitedMembersCount,
                otherMemberIds = roomSummaryEntity.otherMemberIds.toList(),
                highlightCount = roomSummaryEntity.highlightCount,
                notificationCount = roomSummaryEntity.notificationCount,
                hasUnreadMessages = roomSummaryEntity.hasUnreadMessages,
                tags = tags,
                typingUsers = typingUsers,
                membership = roomSummaryEntity.membership,
                versioningState = roomSummaryEntity.versioningState,
                readMarkerId = roomSummaryEntity.readMarkerId,
                userDrafts = roomSummaryEntity.userDrafts?.userDrafts?.map { DraftMapper.map(it) }.orEmpty(),
                canonicalAlias = roomSummaryEntity.canonicalAlias,
                aliases = roomSummaryEntity.aliases.toList(),
                isEncrypted = roomSummaryEntity.isEncrypted,
                encryptionEventTs = roomSummaryEntity.encryptionEventTs,
                breadcrumbsIndex = roomSummaryEntity.breadcrumbsIndex,
                roomEncryptionTrustLevel = if (roomSummaryEntity.isEncrypted && roomSummaryEntity.e2eAlgorithm != MXCRYPTO_ALGORITHM_MEGOLM) {
                    RoomEncryptionTrustLevel.E2EWithUnsupportedAlgorithm
                } else roomSummaryEntity.roomEncryptionTrustLevel,
                inviterId = roomSummaryEntity.inviterId,
                hasFailedSending = roomSummaryEntity.hasFailedSending,
                roomType = roomSummaryEntity.roomType,
                spaceParents = roomSummaryEntity.parents.map { relationInfoEntity ->
                    SpaceParentInfo(
                            parentId = relationInfoEntity.parentRoomId,
                            roomSummary = relationInfoEntity.parentSummaryEntity?.let { map(it) },
                            canonical = relationInfoEntity.canonical ?: false,
                            viaServers = relationInfoEntity.viaServers.toList()
                    )
                },
                spaceChildren = roomSummaryEntity.children.map {
                    SpaceChildInfo(
                            childRoomId = it.childRoomId ?: "",
                            isKnown = it.childSummaryEntity != null,
                            roomType = it.childSummaryEntity?.roomType,
                            name = it.childSummaryEntity?.name,
                            topic = it.childSummaryEntity?.topic,
                            avatarUrl = it.childSummaryEntity?.avatarUrl,
                            activeMemberCount = it.childSummaryEntity?.joinedMembersCount,
                            order = it.order,
                            viaServers = it.viaServers.toList(),
                            parentRoomId = roomSummaryEntity.roomId,
                            suggested = it.suggested,
                            canonicalAlias = it.childSummaryEntity?.canonicalAlias,
                            aliases = it.childSummaryEntity?.aliases?.toList(),
                            worldReadable = it.childSummaryEntity?.joinRules == RoomJoinRules.PUBLIC
                    )
                },
                flattenParentIds = roomSummaryEntity.flattenParentIds?.split("|") ?: emptyList(),
                roomEncryptionAlgorithm = when (val alg = roomSummaryEntity.e2eAlgorithm) {
                    
                    
                    MXCRYPTO_ALGORITHM_MEGOLM -> RoomEncryptionAlgorithm.Megolm
                    else                      -> RoomEncryptionAlgorithm.UnsupportedAlgorithm(alg)
                }
        )
    }
}
