

package org.matrix.android.sdk.internal.session.room.accountdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.RealmQuery
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataEvent
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.toOptional
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.mapper.AccountDataMapper
import org.matrix.android.sdk.internal.database.model.RoomAccountDataEntityFields
import org.matrix.android.sdk.internal.database.model.RoomEntity
import org.matrix.android.sdk.internal.database.model.RoomEntityFields
import org.matrix.android.sdk.internal.di.SessionDatabase
import javax.inject.Inject

internal class RoomAccountDataDataSource @Inject constructor(@SessionDatabase private val monarchy: Monarchy,
                                                             private val realmSessionProvider: RealmSessionProvider,
                                                             private val accountDataMapper: AccountDataMapper) {

    fun getAccountDataEvent(roomId: String, type: String): RoomAccountDataEvent? {
        return getAccountDataEvents(roomId, setOf(type)).firstOrNull()
    }

    fun getLiveAccountDataEvent(roomId: String, type: String): LiveData<Optional<RoomAccountDataEvent>> {
        return Transformations.map(getLiveAccountDataEvents(roomId, setOf(type))) {
            it.firstOrNull().toOptional()
        }
    }

    
    fun getAccountDataEvents(roomId: String?, types: Set<String>): List<RoomAccountDataEvent> {
        return realmSessionProvider.withRealm { realm ->
            buildRoomQuery(realm, roomId, types).findAll().flatMap { it.accountDataEvents(types) }
        }
    }

    
    fun getLiveAccountDataEvents(roomId: String?, types: Set<String>): LiveData<List<RoomAccountDataEvent>> {
        val liveRoomEntity = monarchy.findAllManagedWithChanges {
            buildRoomQuery(it, roomId, types)
        }
        val resultLiveData = MediatorLiveData<List<RoomAccountDataEvent>>()
        resultLiveData.addSource(liveRoomEntity) { changeSet ->
            val mappedResult = changeSet.realmResults.flatMap { it.accountDataEvents(types) }
            resultLiveData.postValue(mappedResult)
        }
        return resultLiveData
    }

    private fun buildRoomQuery(realm: Realm, roomId: String?, types: Set<String>): RealmQuery<RoomEntity> {
        val query = realm.where(RoomEntity::class.java)
        if (roomId != null) {
            query.equalTo(RoomEntityFields.ROOM_ID, roomId)
        }
        query.isNotEmpty(RoomEntityFields.ACCOUNT_DATA.`$`)
        if (types.isNotEmpty()) {
            query.`in`(RoomEntityFields.ACCOUNT_DATA.TYPE, types.toTypedArray())
        }
        return query
    }

    private fun RoomEntity.accountDataEvents(types: Set<String>): List<RoomAccountDataEvent> {
        val query = accountData.where()
        if (types.isNotEmpty()) {
            query.`in`(RoomAccountDataEntityFields.TYPE, types.toTypedArray())
        }
        return query.findAll().map { accountDataMapper.map(roomId, it) }
    }
}
