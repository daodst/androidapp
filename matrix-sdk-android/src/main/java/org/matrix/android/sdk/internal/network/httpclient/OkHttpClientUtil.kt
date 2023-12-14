

package org.matrix.android.sdk.internal.network.httpclient

import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.internal.network.AccessTokenInterceptor
import org.matrix.android.sdk.internal.network.interceptors.CurlLoggingInterceptor
import org.matrix.android.sdk.internal.network.ssl.CertUtil
import org.matrix.android.sdk.internal.network.token.AccessTokenProvider
import timber.log.Timber

internal fun OkHttpClient.Builder.addAccessTokenInterceptor(accessTokenProvider: AccessTokenProvider): OkHttpClient.Builder {
    
    val existingCurlInterceptors = interceptors().filterIsInstance<CurlLoggingInterceptor>()
    interceptors().removeAll(existingCurlInterceptors)

    addInterceptor(AccessTokenInterceptor(accessTokenProvider))

    
    existingCurlInterceptors.forEach {
        addInterceptor(it)
    }

    return this
}

internal fun OkHttpClient.Builder.addSocketFactory(homeServerConnectionConfig: HomeServerConnectionConfig): OkHttpClient.Builder {
    try {
        val pair = CertUtil.newPinnedSSLSocketFactory(homeServerConnectionConfig)
        sslSocketFactory(pair.sslSocketFactory, pair.x509TrustManager)
        hostnameVerifier(CertUtil.newHostnameVerifier(homeServerConnectionConfig))
        connectionSpecs(CertUtil.newConnectionSpecs(homeServerConnectionConfig))
    } catch (e: Exception) {
        Timber.e(e, "addSocketFactory failed")
    }

    return this
}
