

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class SdpType {
    @Json(name = "offer")
    OFFER,

    @Json(name = "answer")
    ANSWER;
}
