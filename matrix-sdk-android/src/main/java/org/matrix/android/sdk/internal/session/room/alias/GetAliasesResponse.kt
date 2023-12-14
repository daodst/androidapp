

package org.matrix.android.sdk.internal.session.room.alias

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetAliasesResponse(
        
        @Json(name = "aliases") val aliases: List<String> = emptyList()
)
