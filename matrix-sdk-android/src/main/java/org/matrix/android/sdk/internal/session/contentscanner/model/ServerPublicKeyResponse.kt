

package org.matrix.android.sdk.internal.session.contentscanner.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ServerPublicKeyResponse(
        @Json(name = "public_key")
        val publicKey: String?
)
