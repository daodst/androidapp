

package org.matrix.android.sdk.internal.auth

import android.net.Uri
import dagger.Lazy
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.auth.AuthenticationService
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.data.LoginFlowResult
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.login.LoginWizard
import org.matrix.android.sdk.api.auth.registration.RegistrationWizard
import org.matrix.android.sdk.api.auth.wellknown.WellknownResult
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixIdFailure
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.appendParamToUrl
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.auth.data.WebClientConfig
import org.matrix.android.sdk.internal.auth.db.PendingSessionData
import org.matrix.android.sdk.internal.auth.login.DefaultLoginWizard
import org.matrix.android.sdk.internal.auth.login.DirectLoginTask
import org.matrix.android.sdk.internal.auth.registration.DefaultRegistrationWizard
import org.matrix.android.sdk.internal.auth.version.Versions
import org.matrix.android.sdk.internal.auth.version.isLoginAndRegistrationSupportedBySdk
import org.matrix.android.sdk.internal.auth.version.isSupportedBySdk
import org.matrix.android.sdk.internal.di.Unauthenticated
import org.matrix.android.sdk.internal.network.RetrofitFactory
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.network.httpclient.addSocketFactory
import org.matrix.android.sdk.internal.network.ssl.UnrecognizedCertificateException
import org.matrix.android.sdk.internal.wellknown.GetWellknownTask
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

internal class DefaultAuthenticationService @Inject constructor(
        @Unauthenticated
        private val okHttpClient: Lazy<OkHttpClient>,
        private val retrofitFactory: RetrofitFactory,
        private val sessionParamsStore: SessionParamsStore,
        private val sessionManager: SessionManager,
        private val sessionCreator: SessionCreator,
        private val pendingSessionStore: PendingSessionStore,
        private val getWellknownTask: GetWellknownTask,
        private val directLoginTask: DirectLoginTask,
) : AuthenticationService {

    private var pendingSessionData: PendingSessionData? = pendingSessionStore.getPendingSessionData()

    private var currentLoginWizard: LoginWizard? = null
    private var currentRegistrationWizard: RegistrationWizard? = null

    override fun hasAuthenticatedSessions(): Boolean {
        return sessionParamsStore.getLast() != null
    }

    
    override fun getAccessToken(): String? {
        if (hasAuthenticatedSessions()) {
            val sessionParams = sessionParamsStore.getLast()
            return sessionParams?.let {
                val session = sessionManager.getOrCreateSession(it)
                sessionParamsStore.get(session.sessionId)?.credentials?.accessToken
            }
        }
        return "";
    }

    override fun getLastAuthenticatedSession(): Session? {
        val sessionParams = sessionParamsStore.getLast()
        return sessionParams?.let {
            sessionManager.getOrCreateSession(it)
        }
    }

    override fun isLogin(address: String): Boolean {
        return sessionParamsStore.getAll().find {
            it.userId.contains(address);
        } != null
    }

    override suspend fun getLoginFlowOfSession(sessionId: String): LoginFlowResult {
        val homeServerConnectionConfig = sessionParamsStore.get(sessionId)?.homeServerConnectionConfig
                ?: throw IllegalStateException("Session not found")

        return getLoginFlow(homeServerConnectionConfig)
    }

    override fun getSsoUrl(redirectUrl: String, deviceId: String?, providerId: String?): String? {
        val homeServerUrlBase = getHomeServerUrlBase() ?: return null

        return buildString {
            append(homeServerUrlBase)
            append(SSO_REDIRECT_PATH)
            if (providerId != null) {
                append("/$providerId")
            }
            
            appendParamToUrl(SSO_REDIRECT_URL_PARAM, redirectUrl)
            deviceId?.takeIf { it.isNotBlank() }?.let {
                
                appendParamToUrl("device_id", it)
            }
        }
    }

    override fun getFallbackUrl(forSignIn: Boolean, deviceId: String?): String? {
        val homeServerUrlBase = getHomeServerUrlBase() ?: return null

        return buildString {
            append(homeServerUrlBase)
            if (forSignIn) {
                append(LOGIN_FALLBACK_PATH)
                deviceId?.takeIf { it.isNotBlank() }?.let {
                    
                    appendParamToUrl("device_id", it)
                }
            } else {
                
                append(REGISTER_FALLBACK_PATH)
            }
        }
    }

    private fun getHomeServerUrlBase(): String? {
        return pendingSessionData
                ?.homeServerConnectionConfig
                ?.homeServerUriBase
                ?.toString()
                ?.trim { it == '/' }
    }

    
    override suspend fun getLoginFlow(homeServerConnectionConfig: HomeServerConnectionConfig): LoginFlowResult {
        pendingSessionData = null

        pendingSessionStore.delete()

        val result = runCatching {
            getLoginFlowInternal(homeServerConnectionConfig)
        }
        return result.fold(
                {
                    
                    
                    val alteredHomeServerConnectionConfig = homeServerConnectionConfig.copy(
                            homeServerUriBase = Uri.parse(it.homeServerUrl)
                    )

                    pendingSessionData = PendingSessionData(alteredHomeServerConnectionConfig)
                            .also { data -> pendingSessionStore.savePendingSessionData(data) }
                    it
                },
                {
                    if (it is UnrecognizedCertificateException) {
                        throw Failure.UnrecognizedCertificateFailure(homeServerConnectionConfig.homeServerUriBase.toString(), it.fingerprint)
                    } else {
                        throw it
                    }
                }
        )
    }

    private suspend fun getLoginFlowInternal(homeServerConnectionConfig: HomeServerConnectionConfig): LoginFlowResult {
        val authAPI = buildAuthAPI(homeServerConnectionConfig)

        
        return try {
            getWellknownLoginFlowInternal(homeServerConnectionConfig)
        } catch (failure: Throwable) {
            if (failure is Failure.OtherServerError &&
                    failure.httpCode == HttpsURLConnection.HTTP_NOT_FOUND ) {
                
                
                return runCatching {
                    executeRequest(null) {
                        authAPI.versions()
                    }
                }
                        .map { versions ->
                            
                            getLoginFlowResult(authAPI, versions, homeServerConnectionConfig.homeServerUriBase.toString())
                        }
                        .fold(
                                {
                                    it
                                },
                                {
                                    if (it is Failure.OtherServerError &&
                                            it.httpCode == HttpsURLConnection.HTTP_NOT_FOUND ) {
                                        
                                        getWebClientDomainLoginFlowInternal(homeServerConnectionConfig)
                                    } else {
                                        throw it
                                    }
                                }
                        )
            } else {
                throw failure
            }
        }
    }

    private suspend fun getWebClientDomainLoginFlowInternal(homeServerConnectionConfig: HomeServerConnectionConfig): LoginFlowResult {
        val authAPI = buildAuthAPI(homeServerConnectionConfig)

        val domain = homeServerConnectionConfig.homeServerUri.host
                ?: return getWebClientLoginFlowInternal(homeServerConnectionConfig)

        
        return runCatching {
            executeRequest(null) {
                authAPI.getWebClientConfigDomain(domain)
            }
        }
                .map { webClientConfig ->
                    onWebClientConfigRetrieved(homeServerConnectionConfig, webClientConfig)
                }
                .fold(
                        {
                            it
                        },
                        {
                            if (it is Failure.OtherServerError &&
                                    it.httpCode == HttpsURLConnection.HTTP_NOT_FOUND ) {
                                
                                getWebClientLoginFlowInternal(homeServerConnectionConfig)
                            } else {
                                throw it
                            }
                        }
                )
    }

    private suspend fun getWebClientLoginFlowInternal(homeServerConnectionConfig: HomeServerConnectionConfig): LoginFlowResult {
        val authAPI = buildAuthAPI(homeServerConnectionConfig)

        
        return executeRequest(null) {
            authAPI.getWebClientConfig()
        }
                .let { webClientConfig ->
                    onWebClientConfigRetrieved(homeServerConnectionConfig, webClientConfig)
                }
    }

    private suspend fun onWebClientConfigRetrieved(homeServerConnectionConfig: HomeServerConnectionConfig, webClientConfig: WebClientConfig): LoginFlowResult {
        val defaultHomeServerUrl = webClientConfig.getPreferredHomeServerUrl()
        if (defaultHomeServerUrl?.isNotEmpty() == true) {
            
            val newHomeServerConnectionConfig = homeServerConnectionConfig.copy(
                    homeServerUriBase = Uri.parse(defaultHomeServerUrl)
            )

            val newAuthAPI = buildAuthAPI(newHomeServerConnectionConfig)

            val versions = executeRequest(null) {
                newAuthAPI.versions()
            }

            return getLoginFlowResult(newAuthAPI, versions, defaultHomeServerUrl)
        } else {
            
            throw Failure.OtherServerError("", HttpsURLConnection.HTTP_NOT_FOUND )
        }
    }

    private suspend fun getWellknownLoginFlowInternal(homeServerConnectionConfig: HomeServerConnectionConfig): LoginFlowResult {
        val domain = homeServerConnectionConfig.homeServerUri.host
                ?: throw Failure.OtherServerError("", HttpsURLConnection.HTTP_NOT_FOUND )

        val wellknownResult = getWellknownTask.execute(GetWellknownTask.Params(domain, homeServerConnectionConfig))

        return when (wellknownResult) {
            is WellknownResult.Prompt -> {
                val newHomeServerConnectionConfig = homeServerConnectionConfig.copy(
                        homeServerUriBase = Uri.parse(wellknownResult.homeServerUrl),
                        identityServerUri = wellknownResult.identityServerUrl?.let { Uri.parse(it) } ?: homeServerConnectionConfig.identityServerUri
                )

                val newAuthAPI = buildAuthAPI(newHomeServerConnectionConfig)

                val versions = executeRequest(null) {
                    newAuthAPI.versions()
                }

                getLoginFlowResult(newAuthAPI, versions, wellknownResult.homeServerUrl)
            }
            else                      -> throw Failure.OtherServerError("", HttpsURLConnection.HTTP_NOT_FOUND )
        }
    }

    private suspend fun getLoginFlowResult(authAPI: AuthAPI, versions: Versions, homeServerUrl: String): LoginFlowResult {
        
        val loginFlowResponse = executeRequest(null) {
            authAPI.getLoginFlows()
        }
        return LoginFlowResult(
                supportedLoginTypes = loginFlowResponse.flows.orEmpty().mapNotNull { it.type },
                ssoIdentityProviders = loginFlowResponse.flows.orEmpty().firstOrNull { it.type == LoginFlowTypes.SSO }?.ssoIdentityProvider,
                isLoginAndRegistrationSupported = versions.isLoginAndRegistrationSupportedBySdk(),
                homeServerUrl = homeServerUrl,
                isOutdatedHomeserver = !versions.isSupportedBySdk()
        )
    }

    override fun getRegistrationWizard(): RegistrationWizard {
        return currentRegistrationWizard
                ?: let {
                    pendingSessionData?.homeServerConnectionConfig?.let {
                        DefaultRegistrationWizard(
                                buildAuthAPI(it),
                                sessionCreator,
                                pendingSessionStore
                        ).also {
                            currentRegistrationWizard = it
                        }
                    } ?: error("Please call getLoginFlow() with success first")
                }
    }

    override val isRegistrationStarted: Boolean
        get() = currentRegistrationWizard?.isRegistrationStarted == true

    override fun getLoginWizard(): LoginWizard {
        return currentLoginWizard
                ?: let {
                    pendingSessionData?.homeServerConnectionConfig?.let {
                        DefaultLoginWizard(
                                buildAuthAPI(it),
                                sessionCreator,
                                pendingSessionStore
                        ).also {
                            currentLoginWizard = it
                        }
                    } ?: error("Please call getLoginFlow() with success first")
                }
    }

    override suspend fun cancelPendingLoginOrRegistration() {
        currentLoginWizard = null
        currentRegistrationWizard = null

        
        
        pendingSessionData = pendingSessionData?.homeServerConnectionConfig
                ?.let { PendingSessionData(it) }
                .also {
                    if (it == null) {
                        
                        pendingSessionStore.delete()
                    } else {
                        pendingSessionStore.savePendingSessionData(it)
                    }
                }
    }

    override suspend fun reset() {
        currentLoginWizard = null
        currentRegistrationWizard = null

        pendingSessionData = null

        pendingSessionStore.delete()
    }

    override suspend fun createSessionFromSso(homeServerConnectionConfig: HomeServerConnectionConfig,
                                              credentials: Credentials): Session {
        return sessionCreator.createSession(credentials, homeServerConnectionConfig)
    }

    override suspend fun getWellKnownData(matrixId: String,
                                          homeServerConnectionConfig: HomeServerConnectionConfig?): WellknownResult {
        if (!MatrixPatterns.isUserId(matrixId)) {
            throw MatrixIdFailure.InvalidMatrixId
        }

        return getWellknownTask.execute(
                GetWellknownTask.Params(
                        domain = matrixId.getDomain(),
                        homeServerConnectionConfig = homeServerConnectionConfig
                )
        )
    }

    override suspend fun directAuthentication(homeServerConnectionConfig: HomeServerConnectionConfig,
                                              matrixId: String,
                                              password: String,
                                              initialDeviceName: String,
                                              deviceId: String?): Session {
        return directLoginTask.execute(
                DirectLoginTask.Params(
                        homeServerConnectionConfig = homeServerConnectionConfig,
                        userId = matrixId,
                        password = password,
                        deviceName = initialDeviceName,
                        deviceId = deviceId
                )
        )
    }

    private fun buildAuthAPI(homeServerConnectionConfig: HomeServerConnectionConfig): AuthAPI {
        val retrofit = retrofitFactory.create(buildClient(homeServerConnectionConfig), homeServerConnectionConfig.homeServerUriBase.toString())
        return retrofit.create(AuthAPI::class.java)
    }

    private fun buildClient(homeServerConnectionConfig: HomeServerConnectionConfig): OkHttpClient {
        return okHttpClient.get()
                .newBuilder()
                .addSocketFactory(homeServerConnectionConfig)
                .build()
    }
}
