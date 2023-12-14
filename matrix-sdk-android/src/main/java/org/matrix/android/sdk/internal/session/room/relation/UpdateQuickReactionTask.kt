
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

internal interface UpdateQuickReactionTask : Task<UpdateQuickReactionTask.Params, UpdateQuickReactionTask.Result> {

    data class Params(
            val roomId: String,
            val eventId: String,
            val reaction: String,
            val oppositeReaction: String
    )

    data class Result(
            val reactionToAdd: String?,
            val reactionToRedact: List<String>
    )
}

internal class DefaultUpdateQuickReactionTask @Inject constructor(@SessionDatabase private val monarchy: Monarchy,
                                                                  @UserId private val userId: String) : UpdateQuickReactionTask {

    override suspend fun execute(params: UpdateQuickReactionTask.Params): UpdateQuickReactionTask.Result {
        var res: Pair<String?, List<String>?>? = null
        monarchy.doWithRealm { realm ->
            res = updateQuickReaction(realm, params)
        }
        return UpdateQuickReactionTask.Result(res?.first, res?.second.orEmpty())
    }

    private fun updateQuickReaction(realm: Realm, params: UpdateQuickReactionTask.Params): Pair<String?, List<String>?> {
        
        val existingSummary = EventAnnotationsSummaryEntity.where(realm, params.roomId, params.eventId).findFirst()
                ?: return Pair(params.reaction, null)

        
        val aggregationForReaction = existingSummary.reactionsSummary.where()
                .equalTo(ReactionAggregatedSummaryEntityFields.KEY, params.reaction)
                .findFirst()
        val aggregationForOppositeReaction = existingSummary.reactionsSummary.where()
                .equalTo(ReactionAggregatedSummaryEntityFields.KEY, params.oppositeReaction)
                .findFirst()

        if (aggregationForReaction == null || !aggregationForReaction.addedByMe) {
            
            val toRedact = aggregationForOppositeReaction?.sourceEvents?.mapNotNull {
                
                val entity = EventEntity.where(realm, it).findFirst()
                if (entity?.sender == userId) entity.eventId else null
            }
            return Pair(params.reaction, toRedact)
        } else {
            
            
            val toRedact = aggregationForReaction.sourceEvents.mapNotNull {
                
                val entity = EventEntity.where(realm, it).findFirst()
                if (entity?.sender == userId) entity.eventId else null
            }
            return Pair(null, toRedact)
        }
    }
}
