

package org.matrix.android.sdk.api.session.widgets.model

import android.annotation.SuppressLint
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.util.safeCapitalize


@JsonClass(generateAdapter = true)
data class WidgetContent(
        @Json(name = "creatorUserId") val creatorUserId: String? = null,
        @Json(name = "id") val id: String? = null,
        @Json(name = "type") val type: String? = null,
        @Json(name = "url") val url: String? = null,
        @Json(name = "name") val name: String? = null,
        @Json(name = "data") val data: JsonDict = emptyMap(),
        @Json(name = "waitForIframeLoad") val waitForIframeLoad: Boolean = false
) {

    fun isActive() = type != null && url != null

    @SuppressLint("DefaultLocale")
    fun getHumanName(): String {
        return (name ?: type ?: "").safeCapitalize()
    }
}
