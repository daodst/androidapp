

package org.matrix.android.sdk.api.session.space.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SpaceChildContent(
        
        @Json(name = "via") val via: List<String>? = null,
        
        @Json(name = "order") val order: String? = null,

        
        @Json(name = "suggested") val suggested: Boolean? = false
) {
    
    fun validOrder(): String? {
        return order
                ?.takeIf { it.length <= 50 }
                ?.takeIf { ORDER_VALID_CHAR_REGEX.matches(it) }
    }

    companion object {
        private val ORDER_VALID_CHAR_REGEX = "[ -~]+".toRegex()
    }
}
