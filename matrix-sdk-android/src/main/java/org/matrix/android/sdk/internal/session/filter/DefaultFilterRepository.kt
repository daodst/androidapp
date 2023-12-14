

package org.matrix.android.sdk.internal.session.filter

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.FilterEntity
import org.matrix.android.sdk.internal.database.model.FilterEntityFields
import org.matrix.android.sdk.internal.database.query.get
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject

internal class DefaultFilterRepository @Inject constructor(@SessionDatabase private val monarchy: Monarchy) : FilterRepository {

    override suspend fun storeFilter(filter: Filter, roomEventFilter: RoomEventFilter): Boolean {
        return Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            val filterEntity = FilterEntity.get(realm)
            
            filterEntity == null ||
                    filterEntity.filterBodyJson != filter.toJSONString() ||
                    filterEntity.filterId.isBlank()
        }.also { hasChanged ->
            if (hasChanged) {
                
                
                monarchy.awaitTransaction { realm ->
                    
                    val filterJson = filter.toJSONString()
                    val roomEventFilterJson = roomEventFilter.toJSONString()

                    val filterEntity = FilterEntity.getOrCreate(realm)

                    filterEntity.filterBodyJson = filterJson
                    filterEntity.roomEventFilterJson = roomEventFilterJson
                    
                    filterEntity.filterId = ""
                }
            }
        }
    }

    override suspend fun storeFilterId(filter: Filter, filterId: String) {
        monarchy.awaitTransaction {
            
            val filterJson = filter.toJSONString()

            
            it.where<FilterEntity>()
                    .equalTo(FilterEntityFields.FILTER_BODY_JSON, filterJson)
                    ?.findFirst()
                    ?.filterId = filterId
        }
    }

    override suspend fun getFilter(): String {
        return monarchy.awaitTransaction {
            val filter = FilterEntity.getOrCreate(it)
            if (filter.filterId.isBlank()) {
                
                filter.filterBodyJson
            } else {
                
                filter.filterId
            }
        }
    }

    override suspend fun getRoomFilter(): String {
        return monarchy.awaitTransaction {
            FilterEntity.getOrCreate(it).roomEventFilterJson
        }
    }
}
