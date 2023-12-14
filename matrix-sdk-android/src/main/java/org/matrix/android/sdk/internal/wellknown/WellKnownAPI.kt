
package org.matrix.android.sdk.internal.wellknown

import org.matrix.android.sdk.api.auth.data.WellKnown
import retrofit2.http.GET
import retrofit2.http.Url

internal interface WellKnownAPI {
    @GET()
    suspend fun getWellKnown(@Url domain: String): WellKnown
}
