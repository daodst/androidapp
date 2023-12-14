

package org.matrix.android.sdk.api.session.user.model

import org.matrix.android.sdk.api.session.profile.ProfileService
import org.matrix.android.sdk.api.util.JsonDict


data class User(
        val userId: String,
        
        val displayName: String? = null,
        val avatarUrl: String? = null,
        val tel_numbers: List<String>? = null,

        var shouldSendFlowers: Boolean = false,
        val chat_addr: String? = null,
) {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromJson(userId: String, json: JsonDict): User {
            return User(
                    userId = userId,
                    displayName = json[ProfileService.DISPLAY_NAME_KEY] as? String,
                    avatarUrl = json[ProfileService.AVATAR_URL_KEY] as? String,
                    tel_numbers = json[ProfileService.TEL_NUMBERS_KEY] as? List<String>
            )
        }
    }
}
