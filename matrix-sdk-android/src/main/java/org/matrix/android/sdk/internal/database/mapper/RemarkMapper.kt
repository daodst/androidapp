

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.model.RemarkEntity
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.session.remark.Remark
import javax.inject.Inject

internal class RemarkMapper @Inject constructor(
        private val realmSessionProvider: RealmSessionProvider) {
    fun map(remarkEntity: RemarkEntity): Remark {
        var user :UserEntity? = realmSessionProvider.withRealm {
            UserEntity.where(it, remarkEntity.userId).findFirst()
        }
        return Remark(
                userId = remarkEntity.userId,
                remark = remarkEntity.remark,
                isSync = remarkEntity.isSync,
                avatarUrl = user?.avatarUrl,
        )
    }
}
