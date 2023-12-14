

package org.matrix.android.sdk.internal.crypto.keysbackup.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict

@JsonClass(generateAdapter = true)
internal data class UpdateKeysBackupVersionBody(
        
        @Json(name = "algorithm")
        override val algorithm: String,

        
        @Json(name = "auth_data")
        override val authData: JsonDict,

        
        @Json(name = "version")
        val version: String? = null
) : KeysAlgorithmAndData
