

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject

internal open class RoomTagEntity(
        var tagName: String = "",
        var tagOrder: Double? = null
) : RealmObject() {

    companion object
}
