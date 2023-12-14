

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects


internal open class UserDraftsEntity(var userDrafts: RealmList<DraftEntity> = RealmList()
) : RealmObject() {

    
    @LinkingObjects("userDrafts")
    val roomSummaryEntity: RealmResults<RoomSummaryEntity>? = null

    companion object
}
