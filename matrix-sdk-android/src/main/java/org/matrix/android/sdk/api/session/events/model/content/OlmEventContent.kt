
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class OlmEventContent(
        
        @Json(name = "ciphertext")
        val ciphertext: Map<String, Any>? = null,

        
        @Json(name = "sender_key")
        val senderKey: String? = null
)
