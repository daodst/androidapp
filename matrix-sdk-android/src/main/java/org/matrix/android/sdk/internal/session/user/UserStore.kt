

package org.matrix.android.sdk.internal.session.user

import android.text.TextUtils
import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.query.contains
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.util.awaitTransaction
import timber.log.Timber
import javax.inject.Inject

internal interface UserStore {
    suspend fun createOrUpdate(userId: String, displayName: String? = null, avatarUrl: String? = null)
    suspend fun updateAvatar(userId: String, avatarUrl: String? = null)
    suspend fun updateDisplayName(userId: String, displayName: String? = null)
}

fun getAddressByUid(userId: String): String {
    if (TextUtils.isEmpty(userId)) {
        return userId
    }
    var address = userId
    if (address.startsWith("@")) {
        address = address.substring(1)
    }
    if (address.contains(":")) {
        val strs = address.split(":").toTypedArray()
        address = strs[0]
    }
    return address
}

internal class RealmUserStore @Inject constructor(@SessionDatabase private val monarchy: Monarchy) : UserStore {

    override suspend fun createOrUpdate(userId: String, displayName: String?, avatarUrl: String?) {

        monarchy.awaitTransaction { realm ->
            val userEntity = UserEntity(userId, displayName ?: "", avatarUrl ?: "")
            val address = getAddressByUid(userId)
            val findFirst = UserEntity.contains(realm, address).findFirst()
            Timber.i("-----createOrUpdate------$findFirst-----------------")
            if (null != findFirst && !userId.equals(findFirst.userId)) {
                
                findFirst.deleteFromRealm()
            }
            realm.insertOrUpdate(userEntity)
        }
    }

    override suspend fun updateAvatar(userId: String, avatarUrl: String?) {
        monarchy.awaitTransaction { realm ->
            UserEntity.where(realm, userId).findFirst()?.let {
                it.avatarUrl = avatarUrl ?: ""
            }
        }
    }

    override suspend fun updateDisplayName(userId: String, displayName: String?) {
        monarchy.awaitTransaction { realm ->
            Timber.i("-----createOrUpdate-----updateDisplayName---------")
            UserEntity.where(realm, userId).findFirst()?.let {
                it.displayName = displayName ?: ""
            }
        }
    }
}
