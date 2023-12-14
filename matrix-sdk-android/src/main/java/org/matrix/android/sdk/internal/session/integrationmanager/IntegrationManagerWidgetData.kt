

package org.matrix.android.sdk.internal.session.integrationmanager

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class IntegrationManagerWidgetData(
        @Json(name = "api_url") val apiUrl: String? = null
)
