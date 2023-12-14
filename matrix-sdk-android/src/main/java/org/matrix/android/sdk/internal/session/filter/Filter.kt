
package org.matrix.android.sdk.internal.session.filter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.MoshiProvider


@JsonClass(generateAdapter = true)
internal data class Filter(
        
        @Json(name = "event_fields") val eventFields: List<String>? = null,
        
        @Json(name = "event_format") val eventFormat: String? = null,
        
        @Json(name = "presence") val presence: EventFilter? = null,
        
        @Json(name = "account_data") val accountData: EventFilter? = null,
        
        @Json(name = "room") val room: RoomFilter? = null
) {

    fun toJSONString(): String {
        return MoshiProvider.providesMoshi().adapter(Filter::class.java).toJson(this)
    }
}
