

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.VersioningState
import org.matrix.android.sdk.api.session.room.model.tag.RoomTag
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntity
import org.matrix.android.sdk.internal.session.room.membership.RoomName

internal open class RoomSummaryEntity(
        @PrimaryKey var roomId: String = "",
        var roomType: String? = null,
        var parents: RealmList<SpaceParentSummaryEntity> = RealmList(),
        var children: RealmList<SpaceChildSummaryEntity> = RealmList()
) : RealmObject() {

    private var displayName: String? = ""

    fun displayName() = displayName

    fun setDisplayName(roomName: RoomName) {
        if (roomName.name != displayName) {
            displayName = roomName.name
            normalizedDisplayName = roomName.normalizedName
        }
    }

    
    private var normalizedDisplayName: String? = ""

    var avatarUrl: String? = ""
        set(value) {
            if (value != field) field = value
        }
    var name: String? = ""
        set(value) {
            if (value != field) field = value
        }
    var topic: String? = ""
        set(value) {
            if (value != field) field = value
        }

    var latestPreviewableEvent: TimelineEventEntity? = null
        set(value) {
            if (value != field) field = value
        }

    @Index
    var lastActivityTime: Long? = null
        set(value) {
            if (value != field) field = value
        }

    var heroes: RealmList<String> = RealmList()

    var joinedMembersCount: Int? = 0
        set(value) {
            if (value != field) field = value
        }

    
    var atMsg: RealmList<TimelineEventEntity> = RealmList()
        set(value) {
            if (value != field) field = value
        }

    var invitedMembersCount: Int? = 0
        set(value) {
            if (value != field) field = value
        }

    @Index
    var isDirect: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    var izUpgrade: Boolean = false
        set(value) {
            if (value != field) field = value
        }
    
    var isGroup: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    
    var isDvmGroup: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    
    var owner: String? = null
        set(value) {
            if (value != field) field = value
        }
    var directUserId: String? = null
        set(value) {
            if (value != field) field = value
        }

    var otherMemberIds: RealmList<String> = RealmList()

    var notificationCount: Int = 0
        set(value) {
            if (value != field) field = value
        }

    var highlightCount: Int = 0
        set(value) {
            if (value != field) field = value
        }

    var readMarkerId: String? = null
        set(value) {
            if (value != field) field = value
        }

    var hasUnreadMessages: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    private var tags: RealmList<RoomTagEntity> = RealmList()

    fun tags(): List<RoomTagEntity> = tags

    fun updateTags(newTags: List<Pair<String, Double?>>) {
        val toDelete = mutableListOf<RoomTagEntity>()
        tags.forEach { existingTag ->
            val updatedTag = newTags.firstOrNull { it.first == existingTag.tagName }
            if (updatedTag == null) {
                toDelete.add(existingTag)
            } else {
                existingTag.tagOrder = updatedTag.second
            }
        }
        toDelete.forEach { it.deleteFromRealm() }
        newTags.forEach { newTag ->
            if (tags.all { it.tagName != newTag.first }) {
                
                tags.add(
                        RoomTagEntity(newTag.first, newTag.second)
                )
            }
        }

        isFavourite = newTags.any { it.first == RoomTag.ROOM_TAG_FAVOURITE }
        isLowPriority = newTags.any { it.first == RoomTag.ROOM_TAG_LOW_PRIORITY }
        isServerNotice = newTags.any { it.first == RoomTag.ROOM_TAG_SERVER_NOTICE }
    }

    @Index
    var isFavourite: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    @Index
    var isLowPriority: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    @Index
    var isServerNotice: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    var userDrafts: UserDraftsEntity? = null
        set(value) {
            if (value != field) field = value
        }

    var breadcrumbsIndex: Int = RoomSummary.NOT_IN_BREADCRUMBS
        set(value) {
            if (value != field) field = value
        }

    var canonicalAlias: String? = null
        set(value) {
            if (value != field) field = value
        }

    var aliases: RealmList<String> = RealmList()

    fun updateAliases(newAliases: List<String>) {
        
        if (newAliases.distinct().sorted() != aliases.distinct().sorted()) {
            aliases.clear()
            aliases.addAll(newAliases)
            flatAliases = newAliases.joinToString(separator = "|", prefix = "|")
        }
    }

    
    var flatAliases: String = ""

    var isEncrypted: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    var e2eAlgorithm: String? = null
        set(value) {
            if (value != field) field = value
        }

    var encryptionEventTs: Long? = 0
        set(value) {
            if (value != field) field = value
        }

    var roomEncryptionTrustLevelStr: String? = null
        set(value) {
            if (value != field) field = value
        }

    var inviterId: String? = null
        set(value) {
            if (value != field) field = value
        }
    var level: Int? = null
        set(value) {
            if (value != field) field = value
        }
    var directUserPresence: UserPresenceEntity? = null
        set(value) {
            if (value != field) field = value
        }

    var hasFailedSending: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    var flattenParentIds: String? = null
        set(value) {
            if (value != field) field = value
        }

    var groupIds: String? = null
        set(value) {
            if (value != field) field = value
        }

    @Index
    private var membershipStr: String = Membership.NONE.name

    var membership: Membership
        get() {
            return Membership.valueOf(membershipStr)
        }
        set(value) {
            if (value.name != membershipStr) {
                membershipStr = value.name
            }
        }

    @Index
    var isHiddenFromUser: Boolean = false
        set(value) {
            if (value != field) field = value
        }

    @Index
    private var versioningStateStr: String = VersioningState.NONE.name
    var versioningState: VersioningState
        get() {
            return VersioningState.valueOf(versioningStateStr)
        }
        set(value) {
            if (value.name != versioningStateStr) {
                versioningStateStr = value.name
            }
        }

    private var joinRulesStr: String? = null
    var joinRules: RoomJoinRules?
        get() {
            return joinRulesStr?.let {
                tryOrNull { RoomJoinRules.valueOf(it) }
            }
        }
        set(value) {
            if (value?.name != joinRulesStr) {
                joinRulesStr = value?.name
            }
        }

    var roomEncryptionTrustLevel: RoomEncryptionTrustLevel?
        get() {
            return roomEncryptionTrustLevelStr?.let {
                try {
                    RoomEncryptionTrustLevel.valueOf(it)
                } catch (failure: Throwable) {
                    null
                }
            }
        }
        set(value) {
            if (value?.name != roomEncryptionTrustLevelStr) {
                roomEncryptionTrustLevelStr = value?.name
            }
        }

    companion object
}
