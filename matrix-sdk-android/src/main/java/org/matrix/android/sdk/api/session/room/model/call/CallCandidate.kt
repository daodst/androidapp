

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CallCandidate(
        
        @Json(name = "sdpMid") val sdpMid: String? = null,
        
        @Json(name = "sdpMLineIndex") val sdpMLineIndex: Int = 0,
        
        @Json(name = "candidate") val candidate: String? = null
)
