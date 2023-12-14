

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject

internal open class DraftEntity(var content: String = "",
                                var draftMode: String = MODE_REGULAR,
                                var linkedEventId: String = ""
) : RealmObject() {

    companion object {
        const val MODE_REGULAR = "REGULAR"
        const val MODE_EDIT = "EDIT"
        const val MODE_REPLY = "REPLY"
        const val MODE_QUOTE = "QUOTE"
        const val MODE_VOICE = "VOICE"
    }
}
