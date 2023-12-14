

package org.matrix.android.sdk.internal.session.terms

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class AcceptTermsBody(
        @Json(name = "user_accepts")
        val acceptedTermUrls: List<String>
)
