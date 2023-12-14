

package org.matrix.android.sdk.internal.wellknown

import android.util.MalformedJsonException
import dagger.Lazy
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.data.WellKnown
import org.matrix.android.sdk.api.auth.wellknown.WellknownResult
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.internal.di.Unauthenticated
import org.matrix.android.sdk.internal.network.RetrofitFactory
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.network.httpclient.addSocketFactory
import org.matrix.android.sdk.internal.network.ssl.UnrecognizedCertificateException
import org.matrix.android.sdk.internal.session.homeserver.CapabilitiesAPI
import org.matrix.android.sdk.internal.session.identity.IdentityAuthAPI
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.isValidUrl
import timber.log.Timber
import java.io.EOFException
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

internal interface GetWellknownTask : Task<GetWellknownTask.Params, WellknownResult> {
    data class Params(
            
            val domain: String,
            val homeServerConnectionConfig: HomeServerConnectionConfig?
    )
}


internal class DefaultGetWellknownTask @Inject constructor(
        @Unauthenticated
        private val okHttpClient: Lazy<OkHttpClient>,
        private val retrofitFactory: RetrofitFactory
) : GetWellknownTask {

    override suspend fun execute(params: GetWellknownTask.Params): WellknownResult {
        val serverUrl = params.homeServerConnectionConfig?.homeServerUri.toString();
        if (true) {
            return WellknownResult.FailPrompt(null, null)
        }
        Timber.i("serverUrl = $serverUrl")
        val client = buildClient(params.homeServerConnectionConfig)
        return findClientConfig(serverUrl, client)
    }

    private fun buildClient(homeServerConnectionConfig: HomeServerConnectionConfig?): OkHttpClient {
        return if (homeServerConnectionConfig != null) {
            okHttpClient.get()
                    .newBuilder()
                    .addSocketFactory(homeServerConnectionConfig)
                    .build()
        } else {
            okHttpClient.get()
        }
    }

    
    private suspend fun findClientConfig(domain: String, client: OkHttpClient): WellknownResult {
        val wellKnownAPI = retrofitFactory.create(client, "https://dummy.org")
                .create(WellKnownAPI::class.java)

        return try {
            val wellKnown = executeRequest(null) {
                wellKnownAPI.getWellKnown("$domain.well-known/matrix/client")
            }

            
            val homeServerBaseUrl = wellKnown.homeServer?.baseURL
            if (homeServerBaseUrl.isNullOrBlank()) {
                WellknownResult.FailPrompt(null, null)
            } else {
                if (homeServerBaseUrl.isValidUrl()) {
                    
                    validateHomeServer(homeServerBaseUrl, wellKnown, client)
                } else {
                    WellknownResult.FailError
                }
            }
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            when (throwable) {
                is UnrecognizedCertificateException        -> {
                    throw Failure.UnrecognizedCertificateFailure(
                            "https://$domain",
                            throwable.fingerprint
                    )
                }
                is Failure.NetworkConnection               -> {
                    WellknownResult.Ignore
                }
                is Failure.OtherServerError                -> {
                    when (throwable.httpCode) {
                        HttpsURLConnection.HTTP_NOT_FOUND -> WellknownResult.Ignore
                        else                              -> WellknownResult.FailPrompt(null, null)
                    }
                }
                is MalformedJsonException, is EOFException -> {
                    WellknownResult.FailPrompt(null, null)
                }
                else                                       -> {
                    throw throwable
                }
            }
        }
    }

    
    private suspend fun validateHomeServer(homeServerBaseUrl: String, wellKnown: WellKnown, client: OkHttpClient): WellknownResult {
        val capabilitiesAPI = retrofitFactory.create(client, homeServerBaseUrl)
                .create(CapabilitiesAPI::class.java)

        try {
            executeRequest(null) {
                capabilitiesAPI.ping()
            }
        } catch (throwable: Throwable) {
            return WellknownResult.FailError
        }

        return if (wellKnown.identityServer == null) {
            
            WellknownResult.Prompt(homeServerBaseUrl, null, wellKnown)
        } else {
            
            val identityServerBaseUrl = wellKnown.identityServer.baseURL
            if (identityServerBaseUrl.isNullOrBlank()) {
                WellknownResult.FailError
            } else {
                if (identityServerBaseUrl.isValidUrl()) {
                    if (validateIdentityServer(identityServerBaseUrl, client)) {
                        
                        WellknownResult.Prompt(homeServerBaseUrl, identityServerBaseUrl, wellKnown)
                    } else {
                        WellknownResult.FailPrompt(homeServerBaseUrl, wellKnown)
                    }
                } else {
                    WellknownResult.FailError
                }
            }
        }
    }

    
    private suspend fun validateIdentityServer(identityServerBaseUrl: String, client: OkHttpClient): Boolean {
        val identityPingApi = retrofitFactory.create(client, identityServerBaseUrl)
                .create(IdentityAuthAPI::class.java)

        return try {
            executeRequest(null) {
                identityPingApi.ping()
            }

            true
        } catch (throwable: Throwable) {
            false
        }
    }

    
    
}
