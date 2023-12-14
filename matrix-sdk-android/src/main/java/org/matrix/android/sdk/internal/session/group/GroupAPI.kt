

package org.matrix.android.sdk.internal.session.group

import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.group.model.GroupRooms
import org.matrix.android.sdk.internal.session.group.model.GroupSummaryResponse
import org.matrix.android.sdk.internal.session.group.model.GroupUsers
import retrofit2.http.GET
import retrofit2.http.Path

internal interface GroupAPI {

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "groups/{groupId}/summary")
    suspend fun getSummary(@Path("groupId") groupId: String): GroupSummaryResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "groups/{groupId}/rooms")
    suspend fun getRooms(@Path("groupId") groupId: String): GroupRooms

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "groups/{groupId}/users")
    suspend fun getUsers(@Path("groupId") groupId: String): GroupUsers
}
