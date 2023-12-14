

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupsSyncResponse(
        
        @Json(name = "join") val join: Map<String, Any> = emptyMap(),

        
        @Json(name = "invite") val invite: Map<String, InvitedGroupSync> = emptyMap(),

        
        @Json(name = "leave") val leave: Map<String, Any> = emptyMap()
)
