

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.RoomEntityFields
import org.matrix.android.sdk.internal.database.model.RoomMembersLoadStatusType
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo007(realm: DynamicRealm) : RealmMigrator(realm, 7) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("RoomEntity")
                ?.addField(RoomEntityFields.MEMBERS_LOAD_STATUS_STR, String::class.java)
                ?.transform { obj ->
                    if (obj.getBoolean("areAllMembersLoaded")) {
                        obj.setString("membersLoadStatusStr", RoomMembersLoadStatusType.LOADED.name)
                    } else {
                        obj.setString("membersLoadStatusStr", RoomMembersLoadStatusType.NONE.name)
                    }
                }
                ?.removeField("areAllMembersLoaded")
    }
}
