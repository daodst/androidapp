

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomServerAclContent(
        
        @Json(name = "allow_ip_literals")
        val allowIpLiterals: Boolean = true,

        
        @Json(name = "allow")
        val allowList: List<String> = emptyList(),

        
        @Json(name = "deny")
        val denyList: List<String> = emptyList()

) {
    companion object {
        const val ALL = "*"
    }
}
