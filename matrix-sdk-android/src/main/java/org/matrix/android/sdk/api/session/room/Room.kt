

package org.matrix.android.sdk.api.session.room

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataService
import org.matrix.android.sdk.api.session.room.alias.AliasService
import org.matrix.android.sdk.api.session.room.call.RoomCallService
import org.matrix.android.sdk.api.session.room.crypto.RoomCryptoService
import org.matrix.android.sdk.api.session.room.members.MembershipService
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.relation.RelationService
import org.matrix.android.sdk.api.session.room.notification.RoomPushRuleService
import org.matrix.android.sdk.api.session.room.read.ReadService
import org.matrix.android.sdk.api.session.room.reporting.ReportingService
import org.matrix.android.sdk.api.session.room.send.DraftService
import org.matrix.android.sdk.api.session.room.send.SendService
import org.matrix.android.sdk.api.session.room.state.StateService
import org.matrix.android.sdk.api.session.room.tags.TagsService
import org.matrix.android.sdk.api.session.room.threads.ThreadsService
import org.matrix.android.sdk.api.session.room.threads.local.ThreadsLocalService
import org.matrix.android.sdk.api.session.room.timeline.TimelineService
import org.matrix.android.sdk.api.session.room.typing.TypingService
import org.matrix.android.sdk.api.session.room.uploads.UploadsService
import org.matrix.android.sdk.api.session.room.version.RoomVersionService
import org.matrix.android.sdk.api.session.search.SearchResult
import org.matrix.android.sdk.api.session.space.Space
import org.matrix.android.sdk.api.util.Optional


interface Room :
        TimelineService,
        ThreadsService,
        ThreadsLocalService,
        SendService,
        DraftService,
        ReadService,
        TypingService,
        AliasService,
        TagsService,
        MembershipService,
        StateService,
        UploadsService,
        ReportingService,
        RoomCallService,
        RelationService,
        RoomCryptoService,
        RoomPushRuleService,
        RoomAccountDataService,
        RoomVersionService {

    val coroutineDispatchers: MatrixCoroutineDispatchers

    
    val roomId: String

    
    fun getRoomSummaryLive(): LiveData<Optional<RoomSummary>>
    fun delAtMsg(eventId: String)
    fun clearAtMsg()

    
    fun roomSummary(): RoomSummary?

    fun setRoomSummaryGroupStatus(isGroup: Boolean)
    fun setRoomSummaryOwner(owner: String)

    fun setRoomSummaryDvmStatus(isDvmGroup: Boolean)
    fun setRoomSummaryLevel(level: Int)
    fun setRoomSummaryUpgradeAnimShow(show: Boolean)

    
    suspend fun search(searchTerm: String,
                       nextBatch: String?,
                       orderByRecent: Boolean,
                       limit: Int,
                       beforeLimit: Int,
                       afterLimit: Int,
                       includeProfile: Boolean): SearchResult

    
    fun asSpace(): Space?
}
