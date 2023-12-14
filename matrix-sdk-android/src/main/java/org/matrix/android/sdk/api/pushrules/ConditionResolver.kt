
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.session.events.model.Event


interface ConditionResolver {
    fun resolveEventMatchCondition(event: Event,
                                   condition: EventMatchCondition): Boolean

    fun resolveRoomMemberCountCondition(event: Event,
                                        condition: RoomMemberCountCondition): Boolean

    fun resolveSenderNotificationPermissionCondition(event: Event,
                                                     condition: SenderNotificationPermissionCondition): Boolean

    fun resolveContainsDisplayNameCondition(event: Event,
                                            condition: ContainsDisplayNameCondition): Boolean
}
