

package org.matrix.android.sdk.internal.session.room.typing

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TypingEventContent(
        @Json(name = "user_ids")
        val typingUserIds: List<String> = emptyList()
)
