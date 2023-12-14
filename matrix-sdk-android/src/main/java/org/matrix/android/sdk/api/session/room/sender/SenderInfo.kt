

package org.matrix.android.sdk.api.session.room.sender

import org.matrix.android.sdk.internal.util.replaceSpaceChars

data class SenderInfo(
        val userId: String,
        
        val displayName: String?,
        val isUniqueDisplayName: Boolean,
        val avatarUrl: String?,
        val remark: String? = null,
) {
    val disambiguatedDisplayName: String
        get() = when {
            remark != null                            -> remark
            displayName == null                       -> userId
            displayName.replaceSpaceChars().isBlank() -> "$displayName ($userId)"
            isUniqueDisplayName                       -> displayName
            else                                      -> "$displayName ($userId)"
        }
}
