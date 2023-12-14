
package org.matrix.android.sdk.api.session.events.model

import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = true)
data class AggregatedAnnotation(
        override val limited: Boolean? = false,
        override val count: Int? = 0,
        val chunk: List<RelationChunkInfo>? = null

) : UnsignedRelationInfo
