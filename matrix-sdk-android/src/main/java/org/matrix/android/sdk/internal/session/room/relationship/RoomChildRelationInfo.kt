

package org.matrix.android.sdk.internal.session.room.relationship

import io.realm.Realm
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.space.model.SpaceChildContent
import org.matrix.android.sdk.api.session.space.model.SpaceParentContent
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.database.model.CurrentStateEventEntity
import org.matrix.android.sdk.internal.database.query.whereType


internal class RoomChildRelationInfo(
        private val realm: Realm,
        private val roomId: String
) {

    data class SpaceChildInfo(
            val roomId: String,
            val order: String?,
            val viaServers: List<String>
    )

    data class SpaceParentInfo(
            val roomId: String,
            val canonical: Boolean,
            val viaServers: List<String>,
            val stateEventSender: String
    )

    
    fun getDirectChildrenDescriptions(): List<SpaceChildInfo> {
        return CurrentStateEventEntity.whereType(realm, roomId, EventType.STATE_SPACE_CHILD)
                .findAll()
                .mapNotNull {
                    ContentMapper.map(it.root?.content).toModel<SpaceChildContent>()?.let { scc ->
                        
                        scc.via?.let { via ->
                            SpaceChildInfo(
                                    roomId = it.stateKey,
                                    order = scc.validOrder(),
                                    viaServers = via
                            )
                        }
                    }
                }
                .sortedBy { it.order }
    }

    fun getParentDescriptions(): List<SpaceParentInfo> {
        return CurrentStateEventEntity.whereType(realm, roomId, EventType.STATE_SPACE_PARENT)
                .findAll()
                .mapNotNull {
                    ContentMapper.map(it.root?.content).toModel<SpaceParentContent>()?.let { scc ->
                        
                        scc.via?.let { via ->
                            SpaceParentInfo(
                                    roomId = it.stateKey,
                                    canonical = scc.canonical ?: false,
                                    viaServers = via,
                                    stateEventSender = it.root?.sender ?: ""
                            )
                        }
                    }
                }
    }
}
