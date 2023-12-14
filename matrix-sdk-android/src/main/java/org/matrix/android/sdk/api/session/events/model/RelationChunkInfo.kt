
package org.matrix.android.sdk.api.session.events.model

import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = true)
data class RelationChunkInfo(
        val type: String,
        val key: String,
        val count: Int
)
