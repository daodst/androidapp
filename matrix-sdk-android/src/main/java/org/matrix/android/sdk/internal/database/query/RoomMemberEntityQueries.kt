

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntityFields
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntity

internal fun RoomMemberSummaryEntity.Companion.where(realm: Realm, roomId: String, userId: String? = null): RealmQuery<RoomMemberSummaryEntity> {
    val query = realm
            .where<RoomMemberSummaryEntity>()
            .equalTo(RoomMemberSummaryEntityFields.ROOM_ID, roomId)

    if (userId != null) {
        query.equalTo(RoomMemberSummaryEntityFields.USER_ID, userId)
    }
    return query
}

internal fun RoomMemberSummaryEntity.Companion.likeWhere(realm: Realm, roomId: String, userId: String? = null): RealmQuery<RoomMemberSummaryEntity> {
    val query = realm
            .where<RoomMemberSummaryEntity>()
            .equalTo(RoomMemberSummaryEntityFields.ROOM_ID, roomId)

    if (userId != null) {
        query.contains(RoomMemberSummaryEntityFields.USER_ID, userId)
    }
    return query
}

internal fun RoomMemberSummaryEntity.Companion.updateUserPresence(realm: Realm, userId: String, userPresenceEntity: UserPresenceEntity) {
    realm.where<RoomMemberSummaryEntity>()
            .equalTo(RoomMemberSummaryEntityFields.USER_ID, userId)
            .isNull(RoomMemberSummaryEntityFields.USER_PRESENCE_ENTITY.`$`)
            .findAll()
            .map {
                it.userPresenceEntity = userPresenceEntity
            }
}
