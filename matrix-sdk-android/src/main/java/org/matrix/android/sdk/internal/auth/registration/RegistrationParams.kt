
package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class RegistrationParams(
        
        @Json(name = "auth")
        val auth: AuthParams? = null,

        
        @Json(name = "username")
        val username: String? = null,

        
        @Json(name = "password")
        val password: String? = null,

        
        @Json(name = "initial_device_display_name")
        val initialDeviceDisplayName: String? = null,

        
        
        @Json(name = "x_show_msisdn")
        val xShowMsisdn: Boolean? = null
)
