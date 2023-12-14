

package org.matrix.android.sdk.internal.raw

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

internal interface RawAPI {
    @GET
    suspend fun getUrl(@Url url: String): ResponseBody
}
