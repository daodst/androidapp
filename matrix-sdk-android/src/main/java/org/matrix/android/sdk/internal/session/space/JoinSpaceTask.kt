

package org.matrix.android.sdk.internal.session.space

import io.realm.RealmConfiguration
import kotlinx.coroutines.TimeoutCancellationException
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.space.JoinSpaceResult
import org.matrix.android.sdk.internal.database.awaitNotEmptyResult
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.room.membership.joining.JoinRoomTask
import org.matrix.android.sdk.internal.session.room.summary.RoomSummaryDataSource
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal interface JoinSpaceTask : Task<JoinSpaceTask.Params, JoinSpaceResult> {
    data class Params(
            val roomIdOrAlias: String,
            val reason: String?,
            val viaServers: List<String> = emptyList()
    )
}

internal class DefaultJoinSpaceTask @Inject constructor(
        private val joinRoomTask: JoinRoomTask,
        @SessionDatabase
        private val realmConfiguration: RealmConfiguration,
        private val roomSummaryDataSource: RoomSummaryDataSource
) : JoinSpaceTask {

    override suspend fun execute(params: JoinSpaceTask.Params): JoinSpaceResult {
        Timber.v("## Space: > Joining root space ${params.roomIdOrAlias} ...")
        try {
            joinRoomTask.execute(JoinRoomTask.Params(
                    params.roomIdOrAlias,
                    params.reason,
                    params.viaServers
            ))
        } catch (failure: Throwable) {
            return JoinSpaceResult.Fail(failure)
        }
        Timber.v("## Space: < Joining root space done for ${params.roomIdOrAlias}")
        

        Timber.v("## Space: > Wait for post joined sync ${params.roomIdOrAlias} ...")
        try {
            awaitNotEmptyResult(realmConfiguration, TimeUnit.MINUTES.toMillis(2L)) { realm ->
                realm.where(RoomSummaryEntity::class.java)
                        .apply {
                            if (params.roomIdOrAlias.startsWith("!")) {
                                equalTo(RoomSummaryEntityFields.ROOM_ID, params.roomIdOrAlias)
                            } else {
                                equalTo(RoomSummaryEntityFields.CANONICAL_ALIAS, params.roomIdOrAlias)
                            }
                        }
                        .equalTo(RoomSummaryEntityFields.MEMBERSHIP_STR, Membership.JOIN.name)
            }
        } catch (exception: TimeoutCancellationException) {
            Timber.w("## Space: > Error created with timeout")
            return JoinSpaceResult.PartialSuccess(emptyMap())
        }

        val errors = mutableMapOf<String, Throwable>()
        Timber.v("## Space: > Sync done ...")
        
        val summary = roomSummaryDataSource.getSpaceSummary(params.roomIdOrAlias)
        Timber.v("## Space: Found space summary Name:[${summary?.name}] children: ${summary?.spaceChildren?.size}")

        return if (errors.isEmpty()) {
            JoinSpaceResult.Success
        } else {
            JoinSpaceResult.PartialSuccess(errors)
        }
    }
}

