

package org.matrix.android.sdk.api.session.group

import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.room.model.Membership

fun groupSummaryQueryParams(init: (GroupSummaryQueryParams.Builder.() -> Unit) = {}): GroupSummaryQueryParams {
    return GroupSummaryQueryParams.Builder().apply(init).build()
}


data class GroupSummaryQueryParams(
        val displayName: QueryStringValue,
        val memberships: List<Membership>
) {

    class Builder {

        var displayName: QueryStringValue = QueryStringValue.IsNotEmpty
        var memberships: List<Membership> = Membership.all()

        fun build() = GroupSummaryQueryParams(
                displayName = displayName,
                memberships = memberships
        )
    }
}
