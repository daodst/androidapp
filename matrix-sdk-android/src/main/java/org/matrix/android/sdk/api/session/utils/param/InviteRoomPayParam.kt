package org.matrix.android.sdk.api.session.utils.param

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class InviteRoomPayParam(



        
        @Json(name = "invitee")
        val invitee: String,
        
        @Json(name = "pub_key")
        val pub_key: String,
        
        @Json(name = "query_sign")
        val query_sign: String,
        
        @Json(name = "timestamp")
        val timestamp: String,
        
        @Json(name = "localpart")
        val localpart: String,
)
