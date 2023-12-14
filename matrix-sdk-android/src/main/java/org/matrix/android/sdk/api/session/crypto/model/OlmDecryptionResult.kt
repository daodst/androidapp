

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
data class OlmDecryptionResult(
        
        @Json(name = "payload") val payload: JsonDict? = null,

        
        @Json(name = "keysClaimed") val keysClaimed: Map<String, String>? = null,

        
        @Json(name = "senderKey") val senderKey: String? = null,

        
        @Json(name = "forwardingCurve25519KeyChain") val forwardingCurve25519KeyChain: List<String>? = null
)
