

package org.matrix.android.sdk.api.session.accountdata

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content


@JsonClass(generateAdapter = true)
data class UserAccountDataEvent(
        @Json(name = "type") val type: String,
        @Json(name = "content") val content: Content
)
