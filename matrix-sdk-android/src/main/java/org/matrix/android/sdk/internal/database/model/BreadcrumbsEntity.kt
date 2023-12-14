

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject

internal open class BreadcrumbsEntity(
        var recentRoomIds: RealmList<String> = RealmList()
) : RealmObject() {

    companion object
}
