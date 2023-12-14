

package org.matrix.android.sdk.internal.session.room.typing

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TypingBody(
        
        @Json(name = "typing")
        val typing: Boolean,
        
        @Json(name = "timeout")
        val timeout: Int?
)
