

package org.matrix.android.sdk.internal.session.search.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
internal data class SearchResponseItem(
        
        @Json(name = "rank")
        val rank: Double? = null,

        
        @Json(name = "result")
        val event: Event,

        
        @Json(name = "context")
        val context: SearchResponseEventContext? = null
)
