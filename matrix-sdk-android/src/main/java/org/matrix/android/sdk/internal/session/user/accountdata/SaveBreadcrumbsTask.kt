
package org.matrix.android.sdk.internal.session.user.accountdata

import com.zhuinden.monarchy.Monarchy
import io.realm.RealmList
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.internal.database.model.BreadcrumbsEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject


internal interface SaveBreadcrumbsTask : Task<SaveBreadcrumbsTask.Params, Unit> {
    data class Params(
            val recentRoomIds: List<String>
    )
}

internal class DefaultSaveBreadcrumbsTask @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy
) : SaveBreadcrumbsTask {

    override suspend fun execute(params: SaveBreadcrumbsTask.Params) {
        monarchy.awaitTransaction { realm ->
            
            val entity = BreadcrumbsEntity.getOrCreate(realm)

            
            entity.recentRoomIds = RealmList<String>().apply { addAll(params.recentRoomIds) }

            
            
            RoomSummaryEntity.where(realm)
                    .greaterThan(RoomSummaryEntityFields.BREADCRUMBS_INDEX, RoomSummary.NOT_IN_BREADCRUMBS)
                    .findAll()
                    .forEach {
                        it.breadcrumbsIndex = RoomSummary.NOT_IN_BREADCRUMBS
                    }

            
            params.recentRoomIds.forEachIndexed { index, roomId ->
                RoomSummaryEntity.where(realm, roomId)
                        .findFirst()
                        ?.breadcrumbsIndex = index
            }
        }
    }
}
