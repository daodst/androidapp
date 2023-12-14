

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
data class ToDeviceSyncResponse(

        
        val events: List<Event>? = null
)
