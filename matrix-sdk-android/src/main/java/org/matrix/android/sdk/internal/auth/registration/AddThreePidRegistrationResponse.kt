

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddThreePidRegistrationResponse(
        
        @Json(name = "sid")
        val sid: String,

        
        @Json(name = "submit_url")
        val submitUrl: String? = null,

        

        @Json(name = "msisdn")
        val msisdn: String? = null,

        @Json(name = "intl_fmt")
        val formattedMsisdn: String? = null,

        @Json(name = "success")
        val success: Boolean? = null
)
