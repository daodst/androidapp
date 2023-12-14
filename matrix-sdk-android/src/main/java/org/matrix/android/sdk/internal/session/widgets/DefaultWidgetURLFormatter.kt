

package org.matrix.android.sdk.internal.session.widgets

import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.SessionLifecycleObserver
import org.matrix.android.sdk.api.session.integrationmanager.IntegrationManagerConfig
import org.matrix.android.sdk.api.session.integrationmanager.IntegrationManagerService
import org.matrix.android.sdk.api.session.widgets.WidgetURLFormatter
import org.matrix.android.sdk.api.util.appendParamToUrl
import org.matrix.android.sdk.api.util.appendParamsToUrl
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.integrationmanager.IntegrationManager
import org.matrix.android.sdk.internal.session.widgets.token.GetScalarTokenTask
import javax.inject.Inject

@SessionScope
internal class DefaultWidgetURLFormatter @Inject constructor(private val integrationManager: IntegrationManager,
                                                             private val getScalarTokenTask: GetScalarTokenTask,
                                                             private val matrixConfiguration: MatrixConfiguration
) : IntegrationManagerService.Listener, WidgetURLFormatter, SessionLifecycleObserver {

    private lateinit var currentConfig: IntegrationManagerConfig
    private var whiteListedUrls: List<String> = emptyList()

    override fun onSessionStarted(session: Session) {
        setupWithConfiguration()
        integrationManager.addListener(this)
    }

    override fun onSessionStopped(session: Session) {
        integrationManager.removeListener(this)
    }

    override fun onConfigurationChanged(configs: List<IntegrationManagerConfig>) {
        setupWithConfiguration()
    }

    private fun setupWithConfiguration() {
        val preferredConfig = integrationManager.getPreferredConfig()
        if (!this::currentConfig.isInitialized || preferredConfig != currentConfig) {
            currentConfig = preferredConfig
            whiteListedUrls = if (matrixConfiguration.integrationWidgetUrls.isEmpty()) {
                listOf(preferredConfig.restUrl)
            } else {
                matrixConfiguration.integrationWidgetUrls
            }
        }
    }

    
    override suspend fun format(baseUrl: String, params: Map<String, String>, forceFetchScalarToken: Boolean, bypassWhitelist: Boolean): String {
        return if (bypassWhitelist || isWhiteListed(baseUrl)) {
            val taskParams = GetScalarTokenTask.Params(currentConfig.restUrl, forceFetchScalarToken)
            val scalarToken = getScalarTokenTask.execute(taskParams)
            buildString {
                append(baseUrl)
                appendParamToUrl("scalar_token", scalarToken)
                appendParamsToUrl(params)
            }
        } else {
            buildString {
                append(baseUrl)
                appendParamsToUrl(params)
            }
        }
    }

    private fun isWhiteListed(url: String): Boolean {
        val allowed: List<String> = whiteListedUrls
        for (allowedUrl in allowed) {
            if (url.startsWith(allowedUrl)) {
                return true
            }
        }
        return false
    }
}
