

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
internal open class RoomAccountDataEntity(
        @Index var type: String? = null,
        var contentStr: String? = null
) : RealmObject()
