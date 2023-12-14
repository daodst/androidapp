

package org.matrix.android.sdk.internal.session.sync.model.accountdata

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BreadcrumbsContent(
        @Json(name = "recent_rooms") val recentRoomIds: List<String> = emptyList()
)
