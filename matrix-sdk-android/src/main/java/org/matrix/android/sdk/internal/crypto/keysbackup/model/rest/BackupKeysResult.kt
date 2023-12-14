

package org.matrix.android.sdk.internal.crypto.keysbackup.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BackupKeysResult(
        
        @Json(name = "etag")
        val hash: String,

        
        @Json(name = "count")
        val count: Int
)
