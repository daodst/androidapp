

package org.matrix.android.sdk.internal.session.user

import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.internal.database.model.UserEntity

internal object UserEntityFactory {

    fun create(userId: String, roomMember: RoomMemberContent): UserEntity {
        return UserEntity(
                userId = userId,
                displayName = roomMember.displayName.orEmpty(),
                avatarUrl = roomMember.avatarUrl.orEmpty()
        )
    }

    fun create(user: User): UserEntity {
        val userEntity = UserEntity(
                userId = user.userId,
                displayName = user.displayName.orEmpty(),
                avatarUrl = user.avatarUrl.orEmpty(),
        )
        userEntity.telNumbers.clear()
        user.tel_numbers?.mapTo(userEntity.telNumbers){it}
        return userEntity
    }
}
