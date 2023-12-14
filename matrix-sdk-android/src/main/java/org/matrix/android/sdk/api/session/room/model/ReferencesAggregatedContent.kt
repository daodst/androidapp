
package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.verification.VerificationState


@JsonClass(generateAdapter = true)
data class ReferencesAggregatedContent(
        
        @Json(name = "verif_sum") val verificationState: VerificationState
        
)
