

package org.matrix.android.sdk.internal.session.room

import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomStrippedState
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsParams
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsResponse
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.room.alias.GetAliasesResponse
import org.matrix.android.sdk.internal.session.room.create.CreateRoomBody
import org.matrix.android.sdk.internal.session.room.create.CreateRoomResponse
import org.matrix.android.sdk.internal.session.room.create.JoinRoomResponse
import org.matrix.android.sdk.internal.session.room.membership.RoomJoinedMembersResponse
import org.matrix.android.sdk.internal.session.room.membership.RoomMembersResponse
import org.matrix.android.sdk.internal.session.room.membership.admin.UserIdAndReason
import org.matrix.android.sdk.internal.session.room.membership.joining.InviteBody
import org.matrix.android.sdk.internal.session.room.membership.threepid.ThreePidInviteBody
import org.matrix.android.sdk.internal.session.room.relation.RelationsResponse
import org.matrix.android.sdk.internal.session.room.reporting.ReportContentBody
import org.matrix.android.sdk.internal.session.room.send.SendResponse
import org.matrix.android.sdk.internal.session.room.tags.TagBody
import org.matrix.android.sdk.internal.session.room.timeline.EventContextResponse
import org.matrix.android.sdk.internal.session.room.timeline.PaginationResponse
import org.matrix.android.sdk.internal.session.room.typing.TypingBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RoomAPI {

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "publicRooms")
    suspend fun publicRooms(@Query("server") server: String?,
                            @Body publicRoomsParams: PublicRoomsParams
    ): PublicRoomsResponse

    
    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "createRoom")
    suspend fun createRoom(@Body param: CreateRoomBody): CreateRoomResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/messages")
    suspend fun getRoomMessagesFrom(@Path("roomId") roomId: String,
                                    @Query("from") from: String,
                                    @Query("dir") dir: String,
                                    @Query("limit") limit: Int?,
                                    @Query("filter") filter: String?
    ): PaginationResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/members")
    suspend fun getMembers(@Path("roomId") roomId: String,
                           @Query("at") syncToken: String?,
                           @Query("membership") membership: Membership?,
                           @Query("not_membership") notMembership: Membership?
    ): RoomMembersResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/joined_members")
    suspend fun getJoinedMembers(
            @Path("roomId") roomId: String,
            @Query("at") syncToken: String?,
    ): RoomJoinedMembersResponse

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/send/{eventType}/{txId}")
    suspend fun send(@Path("txId") txId: String,
                     @Path("roomId") roomId: String,
                     @Path("eventType") eventType: String,
                     @Body content: Content?
    ): SendResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/context/{eventId}")
    suspend fun getContextOfEvent(@Path("roomId") roomId: String,
                                  @Path("eventId") eventId: String,
                                  @Query("limit") limit: Int,
                                  @Query("filter") filter: String? = null): EventContextResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/event/{eventId}")
    suspend fun getEvent(@Path("roomId") roomId: String,
                         @Path("eventId") eventId: String): Event

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/read_markers")
    suspend fun sendReadMarker(@Path("roomId") roomId: String,
                               @Body markers: Map<String, String>)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/receipt/{receiptType}/{eventId}")
    suspend fun sendReceipt(@Path("roomId") roomId: String,
                            @Path("receiptType") receiptType: String,
                            @Path("eventId") eventId: String,
                            @Body body: JsonDict = emptyMap())

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/invite")
    suspend fun invite(@Path("roomId") roomId: String,
                       @Body body: InviteBody)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/invite")
    suspend fun invite3pid(@Path("roomId") roomId: String,
                           @Body body: ThreePidInviteBody)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/state/{state_event_type}")
    suspend fun sendStateEvent(@Path("roomId") roomId: String,
                               @Path("state_event_type") stateEventType: String,
                               @Body params: JsonDict)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/state/{state_event_type}/{state_key}")
    suspend fun sendStateEvent(@Path("roomId") roomId: String,
                               @Path("state_event_type") stateEventType: String,
                               @Path("state_key") stateKey: String,
                               @Body params: JsonDict)

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/state")
    suspend fun getRoomState(@Path("roomId") roomId: String): List<Event>

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "rooms/{roomId}/relations/{eventId}/{relationType}/{eventType}")
    suspend fun getRelations(@Path("roomId") roomId: String,
                             @Path("eventId") eventId: String,
                             @Path("relationType") relationType: String,
                             @Path("eventType") eventType: String,
                             @Query("from") from: String? = null,
                             @Query("to") to: String? = null,
                             @Query("limit") limit: Int? = null
    ): RelationsResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "rooms/{roomId}/relations/{eventId}/{relationType}")
    suspend fun getThreadsRelations(@Path("roomId") roomId: String,
                                    @Path("eventId") eventId: String,
                                    @Path("relationType") relationType: String = RelationType.THREAD,
                                    @Query("from") from: String? = null,
                                    @Query("to") to: String? = null,
                                    @Query("limit") limit: Int? = null
    ): RelationsResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "join/{roomIdOrAlias}")
    suspend fun join(@Path("roomIdOrAlias") roomIdOrAlias: String,
                     @Query("server_name") viaServers: List<String>,
                     @Body params: JsonDict): JoinRoomResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/leave")
    suspend fun leave(@Path("roomId") roomId: String,
                      @Body params: Map<String, String?>)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/ban")
    suspend fun ban(@Path("roomId") roomId: String,
                    @Body userIdAndReason: UserIdAndReason)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/unban")
    suspend fun unban(@Path("roomId") roomId: String,
                      @Body userIdAndReason: UserIdAndReason)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/kick")
    suspend fun kick(@Path("roomId") roomId: String,
                     @Body userIdAndReason: UserIdAndReason)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/redact/{eventId}/{txnId}")
    suspend fun redactEvent(
            @Path("txnId") txId: String,
            @Path("roomId") roomId: String,
            @Path("eventId") eventId: String,
            @Body reason: Map<String, String>
    ): SendResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/report/{eventId}")
    suspend fun reportContent(@Path("roomId") roomId: String,
                              @Path("eventId") eventId: String,
                              @Body body: ReportContentBody)

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "org.matrix.msc2432/rooms/{roomId}/aliases")
    suspend fun getAliases(@Path("roomId") roomId: String): GetAliasesResponse

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/typing/{userId}")
    suspend fun sendTypingState(@Path("roomId") roomId: String,
                                @Path("userId") userId: String,
                                @Body body: TypingBody)

    

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/rooms/{roomId}/tags/{tag}")
    suspend fun putTag(@Path("userId") userId: String,
                       @Path("roomId") roomId: String,
                       @Path("tag") tag: String,
                       @Body body: TagBody)

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/rooms/{roomId}/tags/{tag}")
    suspend fun deleteTag(@Path("userId") userId: String,
                          @Path("roomId") roomId: String,
                          @Path("tag") tag: String)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/rooms/{roomId}/account_data/{type}")
    suspend fun setRoomAccountData(@Path("userId") userId: String,
                                   @Path("roomId") roomId: String,
                                   @Path("type") type: String,
                                   @Body content: JsonDict)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "rooms/{roomId}/upgrade")
    suspend fun upgradeRoom(@Path("roomId") roomId: String,
                            @Body body: RoomUpgradeBody): RoomUpgradeResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "im.nheko.summary/rooms/{roomIdOrAlias}/summary")
    suspend fun getRoomSummary(@Path("roomIdOrAlias") roomidOrAlias: String,
                               @Query("via") viaServers: List<String>?): RoomStrippedState
}
