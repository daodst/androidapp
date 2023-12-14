

package org.matrix.android.sdk.internal.session.room.notification

import org.matrix.android.sdk.api.pushrules.RuleKind
import org.matrix.android.sdk.api.pushrules.rest.PushRule

internal data class RoomPushRule(
        val kind: RuleKind,
        val rule: PushRule
)
