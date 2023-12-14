
package org.matrix.android.sdk.internal.session.presence.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.presence.model.PresenceEnum

@JsonClass(generateAdapter = true)
internal data class SetPresenceBody(
        @Json(name = "presence")
        val presence: PresenceEnum,
        @Json(name = "status_msg")
        val statusMsg: String?
)
