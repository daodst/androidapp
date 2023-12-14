

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ReferencesAggregatedSummaryEntity
import org.matrix.android.sdk.internal.database.model.ReferencesAggregatedSummaryEntityFields

internal fun ReferencesAggregatedSummaryEntity.Companion.where(realm: Realm, eventId: String): RealmQuery<ReferencesAggregatedSummaryEntity> {
    val query = realm.where<ReferencesAggregatedSummaryEntity>()
    query.equalTo(ReferencesAggregatedSummaryEntityFields.EVENT_ID, eventId)
    return query
}

internal fun ReferencesAggregatedSummaryEntity.Companion.create(realm: Realm, txID: String): ReferencesAggregatedSummaryEntity {
    return realm.createObject(ReferencesAggregatedSummaryEntity::class.java).apply {
        this.eventId = txID
    }
}
