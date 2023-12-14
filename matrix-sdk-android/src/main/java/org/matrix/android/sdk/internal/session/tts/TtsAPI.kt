

package org.matrix.android.sdk.internal.session.tts

import org.matrix.android.sdk.internal.network.TimeOutInterceptor
import org.matrix.android.sdk.internal.session.tts.model.TranslateResponse
import org.matrix.android.sdk.internal.session.tts.model.TranslateStateResponse
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

internal interface TtsAPI {

    @POST
    @FormUrlEncoded
    suspend fun translate(@Url path: String,
                          @FieldMap param: Map<String, String>,
                          @Header("token") token: String,
                          @Header(TimeOutInterceptor.CONNECT_TIMEOUT) connectTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT,
                          @Header(TimeOutInterceptor.READ_TIMEOUT) readTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT,
                          @Header(TimeOutInterceptor.WRITE_TIMEOUT) writeTimeOut: Long = TimeOutInterceptor.DEFAULT_LONG_TIMEOUT
    ): TranslateResponse



    @GET
    suspend fun translateState(@Url path: String): TranslateStateResponse
}
