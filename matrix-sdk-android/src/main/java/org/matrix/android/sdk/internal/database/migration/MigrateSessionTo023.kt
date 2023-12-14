

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import org.matrix.android.sdk.api.session.threads.ThreadNotificationState
import org.matrix.android.sdk.internal.database.model.EventEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo023(realm: DynamicRealm) : RealmMigrator(realm, 23) {

    override fun doMigrate(realm: DynamicRealm) {
        val eventEntity = realm.schema.get("TimelineEventEntity") ?: return

        realm.schema.get("EventEntity")
                ?.addField(EventEntityFields.IS_ROOT_THREAD, Boolean::class.java, FieldAttribute.INDEXED)
                ?.addField(EventEntityFields.ROOT_THREAD_EVENT_ID, String::class.java, FieldAttribute.INDEXED)
                ?.addField(EventEntityFields.NUMBER_OF_THREADS, Int::class.java)
                ?.addField(EventEntityFields.THREAD_NOTIFICATION_STATE_STR, String::class.java)
                ?.transform {
                    it.setString(EventEntityFields.THREAD_NOTIFICATION_STATE_STR, ThreadNotificationState.NO_NEW_MESSAGE.name)
                }
                ?.addRealmObjectField(EventEntityFields.THREAD_SUMMARY_LATEST_MESSAGE.`$`, eventEntity)
    }
}
