

package org.matrix.android.sdk.internal.session.integrationmanager

import org.matrix.android.sdk.api.auth.data.WellKnown
import org.matrix.android.sdk.internal.database.model.WellknownIntegrationManagerConfigEntity
import javax.inject.Inject

internal class IntegrationManagerConfigExtractor @Inject constructor() {

    fun extract(wellKnown: WellKnown): WellknownIntegrationManagerConfigEntity? {
        wellKnown.integrations?.get("managers")?.let {
            (it as? List<*>)?.let { configs ->
                configs.forEach { config ->
                    (config as? Map<*, *>)?.let { map ->
                        val apiUrl = map["api_url"] as? String
                        val uiUrl = map["ui_url"] as? String ?: apiUrl
                        if (apiUrl != null &&
                                apiUrl.startsWith("https://") &&
                                uiUrl!!.startsWith("https://")) {
                            return WellknownIntegrationManagerConfigEntity(
                                    apiUrl = apiUrl,
                                    uiUrl = uiUrl
                            )
                        }
                    }
                }
            }
        }
        return null
    }
}
