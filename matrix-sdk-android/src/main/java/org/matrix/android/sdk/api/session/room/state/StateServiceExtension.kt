

package org.matrix.android.sdk.api.session.room.state

import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesContent


fun StateService.isPublic(): Boolean {
    return getStateEvent(EventType.STATE_ROOM_JOIN_RULES, QueryStringValue.NoCondition)
            ?.content
            ?.toModel<RoomJoinRulesContent>()
            ?.joinRules == RoomJoinRules.PUBLIC
}
