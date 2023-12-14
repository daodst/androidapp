

package org.matrix.android.sdk.api.util

import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.group.model.GroupSummary
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoom
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.user.model.User
import java.util.Locale

sealed class MatrixItem(
        open val id: String,
        open val displayName: String?,
        open val avatarUrl: String?
) {
    data class UserItem(
            override val id: String,
            override val displayName: String? = null,
            override val avatarUrl: String? = null,
            var shouldSendFlowers: Boolean = false,
            var izGroup: Boolean = false,
    ) :
            MatrixItem(id, displayName?.removeSuffix(IRC_PATTERN), avatarUrl) {

        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    data class EveryoneInRoomItem(override val id: String,
                                  override val displayName: String = NOTIFY_EVERYONE,
                                  override val avatarUrl: String? = null,
                                  val roomDisplayName: String? = null) :
            MatrixItem(id, displayName, avatarUrl) {
        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    data class EventItem(override val id: String,
                         override val displayName: String? = null,
                         override val avatarUrl: String? = null) :
            MatrixItem(id, displayName, avatarUrl) {
        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    data class RoomItem(override val id: String,
                        override val displayName: String? = null,
                        override val avatarUrl: String? = null) :
            MatrixItem(id, displayName, avatarUrl) {
        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    data class SpaceItem(override val id: String,
                         override val displayName: String? = null,
                         override val avatarUrl: String? = null) :
            MatrixItem(id, displayName, avatarUrl) {
        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    data class RoomAliasItem(override val id: String,
                             override val displayName: String? = null,
                             override val avatarUrl: String? = null) :
            MatrixItem(id, displayName, avatarUrl) {
        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    data class GroupItem(override val id: String,
                         override val displayName: String? = null,
                         override val avatarUrl: String? = null) :
            MatrixItem(id, displayName, avatarUrl) {
        init {
            if (BuildConfig.DEBUG) checkId()
        }

        override fun updateAvatar(newAvatar: String?) = copy(avatarUrl = newAvatar)
    }

    protected fun checkId() {
        if (!id.startsWith(getIdPrefix())) {
            error("Wrong usage of MatrixItem: check the id $id should start with ${getIdPrefix()}")
        }
    }

    abstract fun updateAvatar(newAvatar: String?): MatrixItem

    
    private fun getIdPrefix() = when (this) {
        is UserItem           -> '@'
        is EventItem          -> '$'
        is SpaceItem,
        is RoomItem,
        is EveryoneInRoomItem -> '!'
        is RoomAliasItem      -> '#'
        is GroupItem          -> '+'
    }

    fun firstLetterOfDisplayName(): String {
        val displayName = when (this) {
            
            is EveryoneInRoomItem -> roomDisplayName
            else                  -> displayName
        }
        return (displayName?.takeIf { it.isNotBlank() } ?: id)
                .let { dn ->
                    var startIndex = 0
                    val initial = dn[startIndex]

                    if (initial in listOf('@', '#', '+') && dn.length > 1) {
                        startIndex++
                    }

                    var length = 1
                    var first = dn[startIndex]

                    
                    if (dn.length >= 2 && 0x200e == first.code) {
                        startIndex++
                        first = dn[startIndex]
                    }

                    
                    if (first.code in 0xD800..0xDBFF && dn.length > startIndex + 1) {
                        val second = dn[startIndex + 1]
                        if (second.code in 0xDC00..0xDFFF) {
                            length++
                        }
                    }

                    dn.substring(startIndex, startIndex + length)
                }
                .uppercase(Locale.ROOT)
    }

    companion object {
        private const val IRC_PATTERN = " (IRC)"
        const val NOTIFY_EVERYONE = "@room"
    }
}



fun User.toMatrixItem(isGroup: Boolean = false) = MatrixItem.UserItem(userId, displayName, avatarUrl, izGroup = isGroup)

fun GroupSummary.toMatrixItem() = MatrixItem.GroupItem(groupId, displayName, avatarUrl)

fun RoomSummary.toMatrixItem() = if (roomType == RoomType.SPACE) {
    MatrixItem.SpaceItem(roomId, displayName, avatarUrl)
} else {
    MatrixItem.RoomItem(roomId, displayName, avatarUrl)
}

fun RoomSummary.toRoomAliasMatrixItem() = MatrixItem.RoomAliasItem(canonicalAlias ?: roomId, displayName, avatarUrl)

fun RoomSummary.toEveryoneInRoomMatrixItem() = MatrixItem.EveryoneInRoomItem(id = roomId, avatarUrl = avatarUrl, roomDisplayName = displayName)

fun PublicRoom.toMatrixItem() = MatrixItem.RoomItem(roomId, name ?: getPrimaryAlias() ?: "", avatarUrl)

fun RoomMemberSummary.toMatrixItem(isGroup: Boolean = false) = MatrixItem.UserItem(userId, displayName, avatarUrl, izGroup = isGroup)

fun SenderInfo.toMatrixItem() = MatrixItem.UserItem(userId, disambiguatedDisplayName, avatarUrl)

fun SenderInfo.toMatrixItemOrNull() = tryOrNull { MatrixItem.UserItem(userId, disambiguatedDisplayName, avatarUrl) }

fun SpaceChildInfo.toMatrixItem() = if (roomType == RoomType.SPACE) {
    MatrixItem.SpaceItem(childRoomId, name ?: canonicalAlias, avatarUrl)
} else {
    MatrixItem.RoomItem(childRoomId, name ?: canonicalAlias, avatarUrl)
}
