

package org.matrix.android.sdk.internal.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class WebClientConfig(
        
        @Json(name = "default_hs_url")
        val defaultHomeServerUrl: String?,

        @Json(name = "default_server_config")
        val defaultServerConfig: WebClientConfigDefaultServerConfig?
) {
    fun getPreferredHomeServerUrl(): String? {
        return defaultHomeServerUrl
                ?.takeIf { it.isNotEmpty() }
                ?: defaultServerConfig?.homeServer?.baseURL
    }
}

@JsonClass(generateAdapter = true)
internal data class WebClientConfigDefaultServerConfig(
        @Json(name = "m.homeserver")
        val homeServer: WebClientConfigBaseConfig? = null
)

@JsonClass(generateAdapter = true)
internal data class WebClientConfigBaseConfig(
        @Json(name = "base_url")
        val baseURL: String? = null
)
