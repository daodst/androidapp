

package org.matrix.android.sdk.internal.session.integrationmanager

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AllowedWidgetsContent(
        
        @Json(name = "widgets") val widgets: Map<String, Boolean> = emptyMap(),

        
        @Json(name = "native_widgets") val native: Map<String, Map<String, Boolean>> = emptyMap()
)
