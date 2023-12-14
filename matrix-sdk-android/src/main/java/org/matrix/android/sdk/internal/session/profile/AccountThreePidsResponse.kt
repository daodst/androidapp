
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class AccountThreePidsResponse(
        @Json(name = "threepids")
        val threePids: List<ThirdPartyIdentifier>? = null
)
