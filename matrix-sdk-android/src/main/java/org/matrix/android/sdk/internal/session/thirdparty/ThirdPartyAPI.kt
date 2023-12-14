

package org.matrix.android.sdk.internal.session.thirdparty

import org.matrix.android.sdk.api.session.room.model.thirdparty.ThirdPartyProtocol
import org.matrix.android.sdk.api.session.thirdparty.model.ThirdPartyUser
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

internal interface ThirdPartyAPI {

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "thirdparty/protocols")
    suspend fun thirdPartyProtocols(): Map<String, ThirdPartyProtocol>

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "thirdparty/user/{protocol}")
    suspend fun getThirdPartyUser(@Path("protocol") protocol: String,
                                  @QueryMap params: Map<String, String>?): List<ThirdPartyUser>
}
