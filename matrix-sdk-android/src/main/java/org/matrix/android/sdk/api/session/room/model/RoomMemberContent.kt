

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.UnsignedData


@JsonClass(generateAdapter = true)
data class RoomMemberContent(
        @Json(name = "membership") val membership: Membership,
        @Json(name = "reason") val reason: String? = null,
        @Json(name = "displayname") val displayName: String? = null,
        @Json(name = "avatar_url") val avatarUrl: String? = null,
        @Json(name = "is_direct") val isDirect: Boolean = false,
        @Json(name = "third_party_invite") val thirdPartyInvite: Invite? = null,
        @Json(name = "unsigned") val unsignedData: UnsignedData? = null
) {
    val safeReason
        get() = reason?.takeIf { it.isNotBlank() }
}
