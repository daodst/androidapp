

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import timber.log.Timber


@JsonClass(generateAdapter = true)
data class RoomJoinRulesContent(
        @Json(name = "join_rule") val _joinRules: String? = null,
        
        @Json(name = "allow") val allowList: List<RoomJoinRulesAllowEntry>? = null
) {
    val joinRules: RoomJoinRules? = when (_joinRules) {
        "public"     -> RoomJoinRules.PUBLIC
        "invite"     -> RoomJoinRules.INVITE
        "knock"      -> RoomJoinRules.KNOCK
        "private"    -> RoomJoinRules.PRIVATE
        "restricted" -> RoomJoinRules.RESTRICTED
        else         -> {
            Timber.w("Invalid value for RoomJoinRules: `$_joinRules`")
            null
        }
    }
}
