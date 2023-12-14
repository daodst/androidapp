
package org.matrix.android.sdk.internal.session.widgets

import org.matrix.android.sdk.api.session.openid.OpenIdToken
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface WidgetsAPI {

    
    @POST("register")
    suspend fun register(@Body body: OpenIdToken,
                         @Query("v") version: String?): RegisterWidgetResponse

    @GET("account")
    suspend fun validateToken(@Query("scalar_token") scalarToken: String?,
                              @Query("v") version: String?)
}
