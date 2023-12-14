

package org.matrix.android.sdk.internal.session.media

import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MediaAPI {
    
    @GET(NetworkConstants.URI_API_MEDIA_PREFIX_PATH_R0 + "config")
    suspend fun getMediaConfig(): GetMediaConfigResult

    
    @GET(NetworkConstants.URI_API_MEDIA_PREFIX_PATH_R0 + "preview_url")
    suspend fun getPreviewUrlData(@Query("url") url: String, @Query("ts") ts: Long?): JsonDict
}
