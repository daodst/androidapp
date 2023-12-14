

package org.matrix.android.sdk.api.session.room.members

import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.room.model.Membership

fun roomMemberQueryParams(init: (RoomMemberQueryParams.Builder.() -> Unit) = {}): RoomMemberQueryParams {
    return RoomMemberQueryParams.Builder().apply(init).build()
}


data class RoomMemberQueryParams(
        val displayName: QueryStringValue,
        val memberships: List<Membership>,
        val userId: QueryStringValue,
        val excludeSelf: Boolean
) {

    class Builder {

        var userId: QueryStringValue = QueryStringValue.NoCondition
        var displayName: QueryStringValue = QueryStringValue.IsNotEmpty
        var memberships: List<Membership> = Membership.all()
        var excludeSelf: Boolean = false

        fun build() = RoomMemberQueryParams(
                displayName = displayName,
                memberships = memberships,
                userId = userId,
                excludeSelf = excludeSelf
        )
    }
}
