
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DeleteThreePidBody(
        
        @Json(name = "medium") val medium: String,
        
        @Json(name = "address") val address: String
)
