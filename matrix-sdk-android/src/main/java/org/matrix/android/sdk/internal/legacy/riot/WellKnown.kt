
package org.matrix.android.sdk.internal.legacy.riot

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class WellKnown {

    @JvmField
    @Json(name = "m.homeserver")
    var homeServer: WellKnownBaseConfig? = null

    @JvmField
    @Json(name = "m.identity_server")
    var identityServer: WellKnownBaseConfig? = null

    @JvmField
    @Json(name = "m.integrations")
    var integrations: Map<String, *>? = null

    
    fun getIntegrationManagers(): List<WellKnownManagerConfig> {
        val managers = ArrayList<WellKnownManagerConfig>()
        integrations?.get("managers")?.let {
            (it as? ArrayList<*>)?.let { configs ->
                configs.forEach { config ->
                    (config as? Map<*, *>)?.let { map ->
                        val apiUrl = map["api_url"] as? String
                        val uiUrl = map["ui_url"] as? String ?: apiUrl
                        if (apiUrl != null &&
                                apiUrl.startsWith("https://") &&
                                uiUrl!!.startsWith("https://")) {
                            managers.add(WellKnownManagerConfig(
                                    apiUrl = apiUrl,
                                    uiUrl = uiUrl
                            ))
                        }
                    }
                }
            }
        }
        return managers
    }

    @JvmField
    @Json(name = "im.vector.riot.jitsi")
    var jitsiServer: WellKnownPreferredConfig? = null
}
