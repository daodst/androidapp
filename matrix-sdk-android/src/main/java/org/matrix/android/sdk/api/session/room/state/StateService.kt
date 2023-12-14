

package org.matrix.android.sdk.api.session.room.state

import android.net.Uri
import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesAllowEntry
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.api.util.Optional

interface StateService {

    
    suspend fun updateTopic(topic: String)

    
    suspend fun updateName(name: String)

    
    suspend fun updateCanonicalAlias(alias: String?, altAliases: List<String>)

    
    suspend fun updateHistoryReadability(readability: RoomHistoryVisibility)

    
    suspend fun updateJoinRule(joinRules: RoomJoinRules?, guestAccess: GuestAccess?, allowList: List<RoomJoinRulesAllowEntry>? = null)

    
    suspend fun updateAvatar(avatarUri: Uri, fileName: String)

    
    suspend fun deleteAvatar()

    
    suspend fun stopLiveLocation(userId: String)

    
    suspend fun getLiveLocationBeaconInfo(userId: String, filterOnlyLive: Boolean): Event?

    
    suspend fun sendStateEvent(eventType: String, stateKey: String, body: JsonDict)

    
    fun getStateEvent(eventType: String, stateKey: QueryStringValue = QueryStringValue.NoCondition): Event?

    
    fun getStateEventLive(eventType: String, stateKey: QueryStringValue = QueryStringValue.NoCondition): LiveData<Optional<Event>>

    
    fun getStateEvents(eventTypes: Set<String>, stateKey: QueryStringValue = QueryStringValue.NoCondition): List<Event>

    
    fun getStateEventsLive(eventTypes: Set<String>, stateKey: QueryStringValue = QueryStringValue.NoCondition): LiveData<List<Event>>

    suspend fun setJoinRulePublic()
    suspend fun setJoinRuleInviteOnly()
    suspend fun setJoinRuleRestricted(allowList: List<String>)
}
