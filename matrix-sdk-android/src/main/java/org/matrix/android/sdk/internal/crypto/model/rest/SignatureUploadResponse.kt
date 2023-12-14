

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class SignatureUploadResponse(
        
        val failures: Map<String, Map<String, UploadResponseFailure>>? = null
)

@JsonClass(generateAdapter = true)
internal data class UploadResponseFailure(
        @Json(name = "status")
        val status: Int,

        @Json(name = "errcode")
        val errCode: String,

        @Json(name = "message")
        val message: String
)
