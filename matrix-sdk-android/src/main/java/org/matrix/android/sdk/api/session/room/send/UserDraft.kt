

package org.matrix.android.sdk.api.session.room.send


sealed interface UserDraft {
    data class Regular(val content: String) : UserDraft
    data class Quote(val linkedEventId: String, val content: String) : UserDraft
    data class Edit(val linkedEventId: String, val content: String) : UserDraft
    data class Reply(val linkedEventId: String, val content: String) : UserDraft
    data class Voice(val content: String) : UserDraft

    fun isValid(): Boolean {
        return when (this) {
            is Regular -> content.isNotBlank()
            else       -> true
        }
    }
}
