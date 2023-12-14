

package org.matrix.android.sdk.internal.session

import io.realm.Realm
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.database.model.EventInsertType

internal interface EventInsertLiveProcessor {

    fun shouldProcess(eventId: String, eventType: String, insertType: EventInsertType): Boolean

    suspend fun process(realm: Realm, event: Event)

    
    suspend fun onPostProcess() {
        
    }
}
