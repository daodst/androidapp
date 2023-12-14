

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PollQuestion(
        @Json(name = "org.matrix.msc1767.text") val unstableQuestion: String? = null,
        @Json(name = "m.text") val question: String? = null
) {

    fun getBestQuestion() = question ?: unstableQuestion
}
