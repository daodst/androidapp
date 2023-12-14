

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CallCandidatesContent(
        
        @Json(name = "call_id") override val callId: String,
        
        @Json(name = "party_id") override val partyId: String? = null,
        
        @Json(name = "candidates") val candidates: List<CallCandidate> = emptyList(),
        
        @Json(name = "version") override val version: String?
) : CallSignalingContent
