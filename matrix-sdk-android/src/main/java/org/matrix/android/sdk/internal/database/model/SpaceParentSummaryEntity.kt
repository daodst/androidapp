

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject


internal open class SpaceParentSummaryEntity(
        
        var canonical: Boolean? = null,

        var parentRoomId: String? = null,
        
        var parentSummaryEntity: RoomSummaryEntity? = null,

        var viaServers: RealmList<String> = RealmList()

) : RealmObject() {

    companion object
}
