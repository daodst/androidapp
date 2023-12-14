

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
internal data class KeysUploadBody(
        
        @Json(name = "device_keys")
        val deviceKeys: DeviceKeys? = null,

        
        @Json(name = "one_time_keys")
        val oneTimeKeys: JsonDict? = null,

        
        @Json(name = "org.matrix.msc2732.fallback_keys")
        val fallbackKeys: JsonDict? = null
)
