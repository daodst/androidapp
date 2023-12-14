

package org.matrix.android.sdk.internal.session.sync

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import org.matrix.android.sdk.internal.database.model.SyncEntity
import org.matrix.android.sdk.internal.di.SessionDatabase
import javax.inject.Inject

internal class SyncTokenStore @Inject constructor(@SessionDatabase private val monarchy: Monarchy) {

    fun getLastToken(): String? {
        val token = Realm.getInstance(monarchy.realmConfiguration).use {
            
            it.refresh()
            it.where(SyncEntity::class.java).findFirst()?.nextBatch
        }
        return token
    }

    fun saveToken(realm: Realm, token: String?) {
        val sync = SyncEntity(token)
        realm.insertOrUpdate(sync)
    }
}
