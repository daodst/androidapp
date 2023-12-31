

package org.matrix.android.sdk.api.session.room.model.create

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesAllowEntry
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesContent

interface RoomFeaturePreset {

    fun updateRoomParams(params: CreateRoomParams)

    fun setupInitialStates(): List<Event>?
}

class RestrictedRoomPreset(val homeServerCapabilities: HomeServerCapabilities, val restrictedList: List<RoomJoinRulesAllowEntry>) : RoomFeaturePreset {

    override fun updateRoomParams(params: CreateRoomParams) {
        params.historyVisibility = params.historyVisibility ?: RoomHistoryVisibility.SHARED
        params.guestAccess = params.guestAccess ?: GuestAccess.Forbidden
        params.roomVersion = homeServerCapabilities.versionOverrideForFeature(HomeServerCapabilities.ROOM_CAP_RESTRICTED)
    }

    override fun setupInitialStates(): List<Event>? {
        return listOf(
                Event(
                        type = EventType.STATE_ROOM_JOIN_RULES,
                        stateKey = "",
                        content = RoomJoinRulesContent(
                                _joinRules = RoomJoinRules.RESTRICTED.value,
                                allowList = restrictedList
                        ).toContent()
                )
        )
    }
}
