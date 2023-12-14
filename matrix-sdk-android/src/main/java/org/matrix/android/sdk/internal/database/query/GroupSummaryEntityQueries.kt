

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.GroupSummaryEntity
import org.matrix.android.sdk.internal.database.model.GroupSummaryEntityFields

internal fun GroupSummaryEntity.Companion.where(realm: Realm, groupId: String? = null): RealmQuery<GroupSummaryEntity> {
    val query = realm.where<GroupSummaryEntity>()
    if (groupId != null) {
        query.equalTo(GroupSummaryEntityFields.GROUP_ID, groupId)
    }
    return query
}

internal fun GroupSummaryEntity.Companion.where(realm: Realm, groupIds: List<String>): RealmQuery<GroupSummaryEntity> {
    return realm.where<GroupSummaryEntity>()
            .`in`(GroupSummaryEntityFields.GROUP_ID, groupIds.toTypedArray())
}

internal fun GroupSummaryEntity.Companion.getOrCreate(realm: Realm, groupId: String): GroupSummaryEntity {
    return where(realm, groupId).findFirst() ?: realm.createObject(groupId)
}
