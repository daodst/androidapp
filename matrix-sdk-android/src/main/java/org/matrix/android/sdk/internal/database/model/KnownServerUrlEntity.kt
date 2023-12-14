

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class KnownServerUrlEntity(
        @PrimaryKey
        var url: String = ""
) : RealmObject() {
    companion object
}
