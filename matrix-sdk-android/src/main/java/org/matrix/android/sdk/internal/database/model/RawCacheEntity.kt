

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class RawCacheEntity(
        @PrimaryKey
        var url: String = "",
        var data: String = "",
        var lastUpdatedTimestamp: Long = 0L
) : RealmObject() {

    companion object
}
