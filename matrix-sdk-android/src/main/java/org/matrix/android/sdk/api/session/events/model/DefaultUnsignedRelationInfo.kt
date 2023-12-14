
package org.matrix.android.sdk.api.session.events.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefaultUnsignedRelationInfo(
        override val limited: Boolean? = false,
        override val count: Int? = 0,
        val chunk: List<Map<String, Any>>? = null

) : UnsignedRelationInfo
