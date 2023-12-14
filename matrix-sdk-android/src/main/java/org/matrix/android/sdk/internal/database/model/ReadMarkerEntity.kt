

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class ReadMarkerEntity(
        @PrimaryKey
        var roomId: String = "",
        var eventId: String = ""
) : RealmObject() {

    companion object
}
