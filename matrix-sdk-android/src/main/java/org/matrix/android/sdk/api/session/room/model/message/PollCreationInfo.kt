

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PollCreationInfo(
        @Json(name = "question") val question: PollQuestion? = null,
        @Json(name = "kind") val kind: PollType? = PollType.DISCLOSED_UNSTABLE,
        @Json(name = "max_selections") val maxSelections: Int = 1,
        @Json(name = "answers") val answers: List<PollAnswer>? = null
)
