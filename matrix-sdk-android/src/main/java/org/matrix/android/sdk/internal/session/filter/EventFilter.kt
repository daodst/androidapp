
package org.matrix.android.sdk.internal.session.filter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class EventFilter(
        
        @Json(name = "limit") val limit: Int? = null,
        
        @Json(name = "senders") val senders: List<String>? = null,
        
        @Json(name = "not_senders") val notSenders: List<String>? = null,
        
        @Json(name = "types") val types: List<String>? = null,
        
        @Json(name = "not_types") val notTypes: List<String>? = null
) {
    fun hasData(): Boolean {
        return limit != null ||
                senders != null ||
                notSenders != null ||
                types != null ||
                notTypes != null
    }
}
