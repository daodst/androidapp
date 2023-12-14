

package org.matrix.android.sdk.internal.session.room.aggregation.livelocation

import io.realm.Realm
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.livelocation.LiveLocationBeaconContent
import org.matrix.android.sdk.api.session.room.model.message.MessageLiveLocationContent
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.database.model.CurrentStateEventEntity
import org.matrix.android.sdk.internal.database.query.getOrNull
import timber.log.Timber
import javax.inject.Inject

internal class DefaultLiveLocationAggregationProcessor @Inject constructor() : LiveLocationAggregationProcessor {

    override fun handleLiveLocation(realm: Realm, event: Event, content: MessageLiveLocationContent, roomId: String, isLocalEcho: Boolean) {
        val locationSenderId = event.senderId ?: return

        
        if (isLocalEcho) {
            return
        }

        
        
        var beaconInfoEntity: CurrentStateEventEntity? = null
        val eventTypesIterator = EventType.STATE_ROOM_BEACON_INFO.iterator()
        while (beaconInfoEntity == null && eventTypesIterator.hasNext()) {
            beaconInfoEntity = CurrentStateEventEntity.getOrNull(realm, roomId, locationSenderId, eventTypesIterator.next())
        }

        if (beaconInfoEntity == null) {
            Timber.v("## LIVE LOCATION. There is not any beacon info which should be emitted before sending location updates")
            return
        }
        val beaconInfoContent = ContentMapper.map(beaconInfoEntity.root?.content)?.toModel<LiveLocationBeaconContent>(catchError = true)
        if (beaconInfoContent == null) {
            Timber.v("## LIVE LOCATION. Beacon info content is invalid")
            return
        }

        
        if (!beaconInfoContent.getBestBeaconInfo()?.isLive.orFalse()) {
            Timber.v("## LIVE LOCATION. Beacon info is not live anymore")
            return
        }

        
        if (isBeaconInfoOutdated(beaconInfoContent, content)) {
            Timber.v("## LIVE LOCATION. Beacon info has timeout")
            beaconInfoContent.hasTimedOut = true
        } else {
            beaconInfoContent.lastLocationContent = content
        }

        beaconInfoEntity.root?.content = ContentMapper.map(beaconInfoContent.toContent())
    }

    private fun isBeaconInfoOutdated(beaconInfoContent: LiveLocationBeaconContent,
                                     liveLocationContent: MessageLiveLocationContent): Boolean {
        val beaconInfoStartTime = beaconInfoContent.getBestTimestampAsMilliseconds() ?: 0
        val liveLocationEventTime = liveLocationContent.getBestTimestampAsMilliseconds() ?: 0
        val timeout = beaconInfoContent.getBestBeaconInfo()?.timeout ?: 0
        return liveLocationEventTime - beaconInfoStartTime > timeout
    }
}
