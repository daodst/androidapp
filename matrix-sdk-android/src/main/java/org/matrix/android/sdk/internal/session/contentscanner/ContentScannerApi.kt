

package org.matrix.android.sdk.internal.session.contentscanner

import okhttp3.ResponseBody
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.contentscanner.model.DownloadBody
import org.matrix.android.sdk.internal.session.contentscanner.model.ScanResponse
import org.matrix.android.sdk.internal.session.contentscanner.model.ServerPublicKeyResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


internal interface ContentScannerApi {

    @POST(NetworkConstants.URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE + "download_encrypted")
    suspend fun downloadEncrypted(@Body info: DownloadBody): ResponseBody

    @POST(NetworkConstants.URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE + "scan_encrypted")
    suspend fun scanFile(@Body info: DownloadBody): ScanResponse

    @GET(NetworkConstants.URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE + "public_key")
    suspend fun getServerPublicKey(): ServerPublicKeyResponse

    @GET(NetworkConstants.URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE + "scan/{domain}/{mediaId}")
    suspend fun scanMedia(@Path(value = "domain") domain: String, @Path(value = "mediaId") mediaId: String): ScanResponse
}
