

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PollAnswer(
        @Json(name = "id") val id: String? = null,
        @Json(name = "org.matrix.msc1767.text") val unstableAnswer: String? = null,
        @Json(name = "m.text") val answer: String? = null
) {

    fun getBestAnswer() = answer ?: unstableAnswer
}
