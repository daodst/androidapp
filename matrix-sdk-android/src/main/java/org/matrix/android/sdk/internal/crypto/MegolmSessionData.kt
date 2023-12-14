

package org.matrix.android.sdk.internal.crypto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class MegolmSessionData(
        
        @Json(name = "algorithm")
        val algorithm: String? = null,

        
        @Json(name = "session_id")
        val sessionId: String? = null,

        
        @Json(name = "sender_key")
        val senderKey: String? = null,

        
        @Json(name = "room_id")
        val roomId: String? = null,

        
        @Json(name = "session_key")
        val sessionKey: String? = null,

        
        @Json(name = "sender_claimed_keys")
        val senderClaimedKeys: Map<String, String>? = null,

        
        
        @Json(name = "sender_claimed_ed25519_key")
        val senderClaimedEd25519Key: String? = null,

        
        @Json(name = "forwarding_curve25519_key_chain")
        val forwardingCurve25519KeyChain: List<String>? = null
)
