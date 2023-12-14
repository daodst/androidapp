

package org.matrix.android.sdk.internal.session.sync.model.accountdata

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AcceptedTermsContent(
        @Json(name = "accepted") val acceptedTerms: List<String> = emptyList()
)
