
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.MoshiProvider


@JsonClass(generateAdapter = true)
data class OlmPayloadContent(
        
        @Json(name = "room_id")
        val roomId: String? = null,

        
        @Json(name = "sender")
        val sender: String? = null,

        
        @Json(name = "recipient")
        val recipient: String? = null,

        
        @Json(name = "recipient_keys")
        val recipientKeys: Map<String, String>? = null,

        
        @Json(name = "keys")
        val keys: Map<String, String>? = null
) {
    fun toJsonString(): String {
        return MoshiProvider.providesMoshi().adapter(OlmPayloadContent::class.java).toJson(this)
    }

    companion object {
        fun fromJsonString(str: String): OlmPayloadContent? {
            return MoshiProvider.providesMoshi().adapter(OlmPayloadContent::class.java).fromJson(str)
        }
    }
}
