
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomKeyContent(

        @Json(name = "algorithm")
        val algorithm: String? = null,

        @Json(name = "room_id")
        val roomId: String? = null,

        @Json(name = "session_id")
        val sessionId: String? = null,

        @Json(name = "session_key")
        val sessionKey: String? = null,

        
        @Json(name = "chain_index")
        val chainIndex: Any? = null
)
