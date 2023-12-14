

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.query.process
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo015(realm: DynamicRealm) : RealmMigrator(realm, 15) {

    override fun doMigrate(realm: DynamicRealm) {
        
        
        realm.where("RoomSummaryEntity")
                .process(RoomSummaryEntityFields.MEMBERSHIP_STR, Membership.activeMemberships())
                .equalTo(RoomSummaryEntityFields.IS_DIRECT, true)
                .findAll()
                .onEach {
                    it.setString(RoomSummaryEntityFields.FLATTEN_PARENT_IDS, null)
                }
    }
}
