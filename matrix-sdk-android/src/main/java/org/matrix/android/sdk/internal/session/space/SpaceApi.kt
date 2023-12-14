

package org.matrix.android.sdk.internal.session.space

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface SpaceApi {

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_V1 + "rooms/{roomId}/hierarchy")
    suspend fun getSpaceHierarchy(
            @Path("roomId") spaceId: String,
            @Query("suggested_only") suggestedOnly: Boolean?,
            @Query("limit") limit: Int?,
            @Query("max_depth") maxDepth: Int?,
            @Query("from") from: String?): SpacesResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "org.matrix.msc2946/rooms/{roomId}/hierarchy")
    suspend fun getSpaceHierarchyUnstable(
            @Path("roomId") spaceId: String,
            @Query("suggested_only") suggestedOnly: Boolean?,
            @Query("limit") limit: Int?,
            @Query("max_depth") maxDepth: Int?,
            @Query("from") from: String?): SpacesResponse
}
