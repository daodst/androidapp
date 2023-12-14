

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ForwardedRoomKeyContent(
        
        @Json(name = "algorithm")
        val algorithm: String? = null,

        
        @Json(name = "room_id")
        val roomId: String? = null,

        
        @Json(name = "sender_key")
        val senderKey: String? = null,

        
        @Json(name = "session_id")
        val sessionId: String? = null,

        
        @Json(name = "session_key")
        val sessionKey: String? = null,

        
        @Json(name = "forwarding_curve25519_key_chain")
        val forwardingCurve25519KeyChain: List<String>? = null,

        
        @Json(name = "sender_claimed_ed25519_key")
        val senderClaimedEd25519Key: String? = null
)
