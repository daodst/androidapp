

package org.matrix.android.sdk.internal.session.room.membership.threepid

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ThreePidInviteBody(
        
        @Json(name = "id_server")
        val idServer: String,
        
        @Json(name = "id_access_token")
        val idAccessToken: String,
        
        @Json(name = "medium")
        val medium: String,
        
        @Json(name = "address")
        val address: String
)
