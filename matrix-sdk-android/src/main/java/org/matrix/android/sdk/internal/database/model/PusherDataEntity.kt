
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject

internal open class PusherDataEntity(
        var url: String? = null,
        var format: String? = null
) : RealmObject() {
    companion object
}
