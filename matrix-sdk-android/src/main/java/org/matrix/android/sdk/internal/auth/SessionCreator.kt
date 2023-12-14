

package org.matrix.android.sdk.internal.auth

import android.net.Uri
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.internal.SessionManager
import timber.log.Timber
import javax.inject.Inject

internal interface SessionCreator {
    suspend fun createSession(credentials: Credentials, homeServerConnectionConfig: HomeServerConnectionConfig): Session
}

internal class DefaultSessionCreator @Inject constructor(
        private val sessionParamsStore: SessionParamsStore,
        private val sessionManager: SessionManager,
        private val pendingSessionStore: PendingSessionStore,
        private val isValidClientServerApiTask: IsValidClientServerApiTask
) : SessionCreator {

    
    override suspend fun createSession(credentials: Credentials, homeServerConnectionConfig: HomeServerConnectionConfig): Session {
        
        pendingSessionStore.delete()

        val overriddenUrl = credentials.discoveryInformation?.homeServer?.baseURL
                
                ?.trim { it == '/' }
                ?.takeIf { it.isNotBlank() }
                
                ?.takeIf { it != homeServerConnectionConfig.homeServerUriBase.toString() }
                ?.also { Timber.d("Overriding homeserver url to $it (will check if valid)") }
                ?.let { Uri.parse(it) }
                ?.takeIf {
                    
                    tryOrNull {
                        isValidClientServerApiTask.execute(
                                IsValidClientServerApiTask.Params(
                                        homeServerConnectionConfig.copy(homeServerUriBase = it)
                                )
                        )
                                .also { Timber.d("Overriding homeserver url: $it") }
                    } ?: true 
                }

        val sessionParams = SessionParams(
                credentials = credentials,
                homeServerConnectionConfig = homeServerConnectionConfig.copy(
                        homeServerUriBase = overriddenUrl ?: homeServerConnectionConfig.homeServerUriBase,
                        identityServerUri = credentials.discoveryInformation?.identityServer?.baseURL
                                
                                ?.trim { it == '/' }
                                ?.takeIf { it.isNotBlank() }
                                ?.also { Timber.d("Overriding identity server url to $it") }
                                ?.let { Uri.parse(it) }
                                ?: homeServerConnectionConfig.identityServerUri
                ),
                isTokenValid = true)

        sessionParamsStore.save(sessionParams)
        return sessionManager.getOrCreateSession(sessionParams)
    }
}
