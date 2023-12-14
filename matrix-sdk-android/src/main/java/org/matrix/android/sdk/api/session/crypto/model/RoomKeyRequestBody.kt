

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.MoshiProvider


@JsonClass(generateAdapter = true)
data class RoomKeyRequestBody(
        @Json(name = "algorithm")
        val algorithm: String? = null,

        @Json(name = "room_id")
        val roomId: String? = null,

        @Json(name = "sender_key")
        val senderKey: String? = null,

        @Json(name = "session_id")
        val sessionId: String? = null
) {
    fun toJson(): String {
        return MoshiProvider.providesMoshi().adapter(RoomKeyRequestBody::class.java).toJson(this)
    }

    companion object {
        fun fromJson(json: String?): RoomKeyRequestBody? {
            return json?.let { MoshiProvider.providesMoshi().adapter(RoomKeyRequestBody::class.java).fromJson(it) }
        }
    }
}
