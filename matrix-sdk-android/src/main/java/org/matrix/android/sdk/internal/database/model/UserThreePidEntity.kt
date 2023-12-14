
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject

internal open class UserThreePidEntity(
        var medium: String = "",
        var address: String = "",
        var validatedAt: Long = 0,
        var addedAt: Long = 0
) : RealmObject()
