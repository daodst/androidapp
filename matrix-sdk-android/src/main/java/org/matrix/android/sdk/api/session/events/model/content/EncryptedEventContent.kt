
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent


@JsonClass(generateAdapter = true)
data class EncryptedEventContent(

        
        @Json(name = "algorithm")
        val algorithm: String? = null,

        
        @Json(name = "ciphertext")
        val ciphertext: String? = null,

        
        @Json(name = "device_id")
        val deviceId: String? = null,

        
        @Json(name = "sender_key")
        val senderKey: String? = null,

        
        @Json(name = "session_id")
        val sessionId: String? = null,

        
        @Json(name = "m.relates_to") val relatesTo: RelationDefaultContent? = null
)
