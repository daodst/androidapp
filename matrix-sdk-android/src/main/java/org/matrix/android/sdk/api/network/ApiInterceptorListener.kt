

package org.matrix.android.sdk.api.network

interface ApiInterceptorListener {
    fun onApiResponse(path: ApiPath, response: String)
}
