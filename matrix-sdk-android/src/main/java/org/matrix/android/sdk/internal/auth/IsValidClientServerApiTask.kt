

package org.matrix.android.sdk.internal.auth

import dagger.Lazy
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.internal.di.Unauthenticated
import org.matrix.android.sdk.internal.network.RetrofitFactory
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.network.httpclient.addSocketFactory
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

internal interface IsValidClientServerApiTask : Task<IsValidClientServerApiTask.Params, Boolean> {
    data class Params(
            val homeServerConnectionConfig: HomeServerConnectionConfig
    )
}

internal class DefaultIsValidClientServerApiTask @Inject constructor(
        @Unauthenticated
        private val okHttpClient: Lazy<OkHttpClient>,
        private val retrofitFactory: RetrofitFactory
) : IsValidClientServerApiTask {

    override suspend fun execute(params: IsValidClientServerApiTask.Params): Boolean {
        val client = buildClient(params.homeServerConnectionConfig)
        val homeServerUrl = params.homeServerConnectionConfig.homeServerUriBase.toString()

        val authAPI = retrofitFactory.create(client, homeServerUrl)
                .create(AuthAPI::class.java)

        return try {
            executeRequest(null) {
                authAPI.getLoginFlows()
            }
            
            true
        } catch (failure: Throwable) {
            if (failure is Failure.OtherServerError &&
                    failure.httpCode == HttpsURLConnection.HTTP_NOT_FOUND ) {
                
                false
            } else {
                
                throw failure
            }
        }
    }

    private fun buildClient(homeServerConnectionConfig: HomeServerConnectionConfig): OkHttpClient {
        return okHttpClient.get()
                .newBuilder()
                .addSocketFactory(homeServerConnectionConfig)
                .build()
    }
}
