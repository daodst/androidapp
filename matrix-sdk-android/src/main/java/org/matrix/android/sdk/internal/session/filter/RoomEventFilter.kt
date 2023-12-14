
package org.matrix.android.sdk.internal.session.filter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.MoshiProvider


@JsonClass(generateAdapter = true)
internal data class RoomEventFilter(
        
        @Json(name = "limit") val limit: Int? = null,
        
        @Json(name = "not_senders") val notSenders: List<String>? = null,
        
        @Json(name = "not_types") val notTypes: List<String>? = null,
        
        @Json(name = "senders") val senders: List<String>? = null,
        
        @Json(name = "types") val types: List<String>? = null,
        
        @Json(name = "related_by_rel_types") val relationTypes: List<String>? = null,
        
        @Json(name = "related_by_senders") val relationSenders: List<String>? = null,

        
        @Json(name = "rooms") val rooms: List<String>? = null,
        
        @Json(name = "not_rooms") val notRooms: List<String>? = null,
        
        @Json(name = "contains_url") val containsUrl: Boolean? = null,
        
        @Json(name = "lazy_load_members") val lazyLoadMembers: Boolean? = null
) {

    fun toJSONString(): String {
        return MoshiProvider.providesMoshi().adapter(RoomEventFilter::class.java).toJson(this)
    }

    fun hasData(): Boolean {
        return (limit != null ||
                notSenders != null ||
                notTypes != null ||
                senders != null ||
                types != null ||
                rooms != null ||
                notRooms != null ||
                containsUrl != null ||
                lazyLoadMembers != null)
    }
}
