

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
enum class RoomJoinRules(val value: String) {
    @Json(name = "public") PUBLIC("public"),
    @Json(name = "invite") INVITE("invite"),
    @Json(name = "knock") KNOCK("knock"),
    @Json(name = "private") PRIVATE("private"),
    @Json(name = "restricted") RESTRICTED("restricted")
}
