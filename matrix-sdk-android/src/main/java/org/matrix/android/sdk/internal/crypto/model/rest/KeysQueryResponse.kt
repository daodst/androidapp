

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class KeysQueryResponse(
        
        @Json(name = "device_keys")
        val deviceKeys: Map<String, Map<String, DeviceKeysWithUnsigned>>? = null,

        
        val failures: Map<String, Map<String, Any>>? = null,

        @Json(name = "master_keys")
        val masterKeys: Map<String, RestKeyInfo?>? = null,

        @Json(name = "self_signing_keys")
        val selfSigningKeys: Map<String, RestKeyInfo?>? = null,

        @Json(name = "user_signing_keys")
        val userSigningKeys: Map<String, RestKeyInfo?>? = null
)
