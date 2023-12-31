
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UploadSigningKeysBody(
        @Json(name = "master_key")
        val masterKey: RestKeyInfo? = null,

        @Json(name = "self_signing_key")
        val selfSigningKey: RestKeyInfo? = null,

        @Json(name = "user_signing_key")
        val userSigningKey: RestKeyInfo? = null,

        @Json(name = "auth")
        val auth: Map<String, *>? = null
)
