

package org.matrix.android.sdk.internal.session.identity.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class IdentityLookUpParams(
        
        @Json(name = "addresses")
        val hashedAddresses: List<String>,

        
        @Json(name = "algorithm")
        val algorithm: String,

        
        @Json(name = "pepper")
        val pepper: String
)
