
package org.matrix.android.sdk.api.session.events.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = true)
data class AggregatedRelations(
        @Json(name = "m.annotation") val annotations: AggregatedAnnotation? = null,
        @Json(name = "m.reference") val references: DefaultUnsignedRelationInfo? = null,
        @Json(name = RelationType.THREAD) val latestThread: LatestThreadUnsignedRelation? = null
)
