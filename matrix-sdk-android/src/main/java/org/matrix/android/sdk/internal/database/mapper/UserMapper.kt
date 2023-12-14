

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.internal.database.model.UserEntity

internal object UserMapper {

    fun map(userEntity: UserEntity): User {
        return User(
                userEntity.userId,
                userEntity.displayName,
                userEntity.avatarUrl,
                userEntity.telNumbers.toList()
        )
    }
}

internal fun UserEntity.asDomain(): User {
    return UserMapper.map(this)
}
