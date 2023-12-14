

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject


internal open class SpaceChildSummaryEntity(

        var order: String? = null,

        var autoJoin: Boolean? = null,

        var suggested: Boolean? = null,

        var childRoomId: String? = null,
        
        var childSummaryEntity: RoomSummaryEntity? = null,

        var viaServers: RealmList<String> = RealmList()


) : RealmObject() {

    companion object
}
