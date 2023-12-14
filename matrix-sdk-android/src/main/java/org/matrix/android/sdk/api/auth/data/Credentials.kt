

package org.matrix.android.sdk.api.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.md5


@JsonClass(generateAdapter = true)
data class Credentials(
        
        @Json(name = "user_id") val userId: String,
        
        @Json(name = "access_token") val accessToken: String,
        
        @Json(name = "refresh_token") val refreshToken: String?,
        
        @Json(name = "home_server") val homeServer: String?,
        
        @Json(name = "device_id") val deviceId: String?,
        
        @Json(name = "well_known") val discoveryInformation: DiscoveryInformation? = null
)

internal fun Credentials.sessionId(): String {
    return (if (deviceId.isNullOrBlank()) userId else "$userId|$deviceId").md5()
}
