

package org.matrix.android.sdk.internal.session.identity.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class IdentityLookUpResponse(
        
        @Json(name = "mappings")
        val mappings: Map<String, String>
)
