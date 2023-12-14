

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvitedGroupSync(
        
        @Json(name = "inviter") val inviter: String? = null,

        
        @Json(name = "profile") val profile: GroupSyncProfile? = null
)
