

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CallReplacesContent(
        
        @Json(name = "call_id") override val callId: String,
        
        @Json(name = "party_id") override val partyId: String? = null,
        
        @Json(name = "replacement_id") val replacementId: String? = null,
        
        @Json(name = "target_room") val targetRoomId: String? = null,
        
        @Json(name = "target_user") val targetUser: TargetUser? = null,
        
        @Json(name = "create_call") val createCall: String? = null,
        
        @Json(name = "await_call") val awaitCall: String? = null,
        
        @Json(name = "version") override val version: String?
) : CallSignalingContent {

    @JsonClass(generateAdapter = true)
    data class TargetUser(
            
            @Json(name = "id") val id: String,
            
            @Json(name = "display_name") val displayName: String?,
            
            @Json(name = "avatar_url") val avatarUrl: String?
    )
}
