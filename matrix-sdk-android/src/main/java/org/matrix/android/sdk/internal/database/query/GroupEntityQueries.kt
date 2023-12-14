

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.internal.database.model.GroupEntity
import org.matrix.android.sdk.internal.database.model.GroupEntityFields
import org.matrix.android.sdk.internal.query.process

internal fun GroupEntity.Companion.where(realm: Realm, groupId: String): RealmQuery<GroupEntity> {
    return realm.where<GroupEntity>()
            .equalTo(GroupEntityFields.GROUP_ID, groupId)
}

internal fun GroupEntity.Companion.where(realm: Realm, memberships: List<Membership>): RealmQuery<GroupEntity> {
    return realm.where<GroupEntity>().process(GroupEntityFields.MEMBERSHIP_STR, memberships)
}
