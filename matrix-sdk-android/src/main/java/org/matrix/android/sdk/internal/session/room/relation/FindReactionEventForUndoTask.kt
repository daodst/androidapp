
package org.matrix.android.sdk.internal.session.room.relation

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import org.matrix.android.sdk.internal.database.model.EventAnnotationsSummaryEntity
import org.matrix.android.sdk.internal.database.model.EventEntity
import org.matrix.android.sdk.internal.database.model.ReactionAggregatedSummaryEntityFields
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface FindReactionEventForUndoTask : Task<FindReactionEventForUndoTask.Params, FindReactionEventForUndoTask.Result> {

    data class Params(
            val roomId: String,
            val eventId: String,
            val reaction: String
    )

    data class Result(
            val redactEventId: String?
    )
}

internal class DefaultFindReactionEventForUndoTask @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy,
        @UserId private val userId: String) : FindReactionEventForUndoTask {

    override suspend fun execute(params: FindReactionEventForUndoTask.Params): FindReactionEventForUndoTask.Result {
        val eventId = Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            getReactionToRedact(realm, params)?.eventId
        }
        return FindReactionEventForUndoTask.Result(eventId)
    }

    private fun getReactionToRedact(realm: Realm, params: FindReactionEventForUndoTask.Params): EventEntity? {
        val summary = EventAnnotationsSummaryEntity.where(realm, params.roomId, params.eventId).findFirst() ?: return null

        val rase = summary.reactionsSummary.where()
                .equalTo(ReactionAggregatedSummaryEntityFields.KEY, params.reaction)
                .findFirst() ?: return null

        
        return rase.sourceEvents
                .asSequence()
                .mapNotNull {
                    
                    EventEntity.where(realm, it).findFirst()
                }
                .firstOrNull { eventEntity ->
                    
                    eventEntity.sender == userId
                }
    }
}
