

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject


internal open class EventInsertEntity(var eventId: String = "",
                                      var eventType: String = "",
                                      
                                      var canBeProcessed: Boolean = true
) : RealmObject() {

    private var insertTypeStr: String = EventInsertType.INCREMENTAL_SYNC.name
    var insertType: EventInsertType
        get() {
            return EventInsertType.valueOf(insertTypeStr)
        }
        set(value) {
            insertTypeStr = value.name
        }
}
