
package org.matrix.android.sdk.api.pushrules.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.pushrules.Condition
import org.matrix.android.sdk.api.pushrules.ContainsDisplayNameCondition
import org.matrix.android.sdk.api.pushrules.EventMatchCondition
import org.matrix.android.sdk.api.pushrules.Kind
import org.matrix.android.sdk.api.pushrules.RoomMemberCountCondition
import org.matrix.android.sdk.api.pushrules.RuleIds
import org.matrix.android.sdk.api.pushrules.SenderNotificationPermissionCondition
import timber.log.Timber


@JsonClass(generateAdapter = true)
data class PushCondition(
        
        @Json(name = "kind")
        val kind: String,

        
        @Json(name = "key")
        val key: String? = null,

        
        @Json(name = "pattern")
        val pattern: String? = null,

        
        @Json(name = "is")
        val iz: String? = null
) {

    fun asExecutableCondition(rule: PushRule): Condition? {
        return when (Kind.fromString(kind)) {
            Kind.EventMatch                   -> {
                if (key != null && pattern != null) {
                    EventMatchCondition(key, pattern, rule.ruleId == RuleIds.RULE_ID_CONTAIN_USER_NAME)
                } else {
                    Timber.e("Malformed Event match condition")
                    null
                }
            }
            Kind.ContainsDisplayName          -> {
                ContainsDisplayNameCondition()
            }
            Kind.RoomMemberCount              -> {
                if (iz.isNullOrEmpty()) {
                    Timber.e("Malformed ROOM_MEMBER_COUNT condition")
                    null
                } else {
                    RoomMemberCountCondition(iz)
                }
            }
            Kind.SenderNotificationPermission -> {
                if (key == null) {
                    Timber.e("Malformed Sender Notification Permission condition")
                    null
                } else {
                    SenderNotificationPermissionCondition(key)
                }
            }
            Kind.Unrecognised                 -> {
                Timber.e("Unknown kind $kind")
                null
            }
        }
    }
}
