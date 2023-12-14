

package org.matrix.android.sdk.internal.session.room.aggregation.livelocation

import io.realm.Realm
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.message.MessageLiveLocationContent

internal interface LiveLocationAggregationProcessor {
    fun handleLiveLocation(realm: Realm,
                           event: Event,
                           content: MessageLiveLocationContent,
                           roomId: String,
                           isLocalEcho: Boolean)
}
