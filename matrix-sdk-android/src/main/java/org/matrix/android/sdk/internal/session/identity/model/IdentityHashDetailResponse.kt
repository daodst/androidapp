

package org.matrix.android.sdk.internal.session.identity.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class IdentityHashDetailResponse(
        
        @Json(name = "lookup_pepper")
        val pepper: String,

        
        @Json(name = "algorithms")
        val algorithms: List<String>
) {
    companion object {
        const val ALGORITHM_SHA256 = "sha256"
        const val ALGORITHM_NONE = "none"
    }
}
