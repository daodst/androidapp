

package org.matrix.android.sdk.internal.session.sync.model.accountdata

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.emptyJsonDict

@JsonClass(generateAdapter = true)
internal data class IgnoredUsersContent(
        
        @Json(name = "ignored_users") val ignoredUsers: Map<String, Any>
) {

    companion object {
        fun createWithUserIds(userIds: List<String>): IgnoredUsersContent {
            return IgnoredUsersContent(
                    ignoredUsers = userIds.associateWith { emptyJsonDict }
            )
        }
    }
}
