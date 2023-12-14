

package org.matrix.android.sdk.api.session.crypto.keysbackup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysAlgorithmAndData

@JsonClass(generateAdapter = true)
data class KeysVersionResult(
        
        @Json(name = "algorithm")
        override val algorithm: String,

        
        @Json(name = "auth_data")
        override val authData: JsonDict,

        
        @Json(name = "version")
        val version: String,

        
        @Json(name = "etag")
        val hash: String,

        
        @Json(name = "count")
        val count: Int
) : KeysAlgorithmAndData
