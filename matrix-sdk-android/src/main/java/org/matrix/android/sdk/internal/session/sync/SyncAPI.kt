

package org.matrix.android.sdk.internal.session.sync

import okhttp3.ResponseBody
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.network.TimeOutInterceptor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap
import retrofit2.http.Streaming

internal interface SyncAPI {
    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "sync")
    suspend fun sync(@QueryMap params: Map<String, String>,
                     @Header(TimeOutInterceptor.CONNECT_TIMEOUT) connectTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT,
                     @Header(TimeOutInterceptor.READ_TIMEOUT) readTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT,
                     @Header(TimeOutInterceptor.WRITE_TIMEOUT) writeTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT
    ): SyncResponse

    
    @Streaming
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "sync")
    fun syncStream(@QueryMap params: Map<String, String>,
                   @Header(TimeOutInterceptor.CONNECT_TIMEOUT) connectTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT,
                   @Header(TimeOutInterceptor.READ_TIMEOUT) readTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT,
                   @Header(TimeOutInterceptor.WRITE_TIMEOUT) writeTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT
    ): Call<ResponseBody>
}
