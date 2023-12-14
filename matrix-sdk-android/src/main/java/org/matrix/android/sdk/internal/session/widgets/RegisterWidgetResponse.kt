

package org.matrix.android.sdk.internal.session.widgets

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RegisterWidgetResponse(
        @Json(name = "scalar_token") val scalarToken: String?
)
