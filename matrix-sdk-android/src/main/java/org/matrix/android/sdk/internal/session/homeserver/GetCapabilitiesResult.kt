

package org.matrix.android.sdk.internal.session.homeserver

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
internal data class GetCapabilitiesResult(
        
        @Json(name = "capabilities")
        val capabilities: Capabilities? = null
)

@JsonClass(generateAdapter = true)
internal data class Capabilities(
        
        @Json(name = "m.change_password")
        val changePassword: BooleanCapability? = null,

        
        @Json(name = "m.set_displayname")
        val changeDisplayName: BooleanCapability? = null,

        
        @Json(name = "m.set_avatar_url")
        val changeAvatar: BooleanCapability? = null,
        
        @Json(name = "m.3pid_changes")
        val change3pid: BooleanCapability? = null,
        
        @Json(name = "m.room_versions")
        val roomVersions: RoomVersions? = null,
        
        @Json(name = "m.thread")
        val threads: BooleanCapability? = null
)

@JsonClass(generateAdapter = true)
internal data class BooleanCapability(
        
        @Json(name = "enabled")
        val enabled: Boolean?
)

@JsonClass(generateAdapter = true)
internal data class RoomVersions(
        
        @Json(name = "default")
        val default: String?,

        
        @Json(name = "available")
        val available: JsonDict? = null,

        
        @Json(name = "org.matrix.msc3244.room_capabilities")
        val roomCapabilities: JsonDict? = null
)
