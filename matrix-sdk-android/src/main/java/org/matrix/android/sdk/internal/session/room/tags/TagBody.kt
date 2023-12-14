

package org.matrix.android.sdk.internal.session.room.tags

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TagBody(
        
        @Json(name = "order")
        val order: Double?
)
