

package im.vector.app.features.home.room.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.flow.Flow
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.summary.RoomAggregateNotificationCount

data class RoomsSection(
        val sectionName: String,
        
        val livePages: LiveData<PagedList<RoomSummary>>? = null,
        val liveList: LiveData<List<RoomSummary>>? = null,
        val liveSuggested: LiveData<SuggestedRoomInfo>? = null,
        val isExpanded: MutableLiveData<Boolean> = MutableLiveData(true),
        val itemCount: Flow<Int>,
        val notificationCount: MutableLiveData<RoomAggregateNotificationCount> = MutableLiveData(RoomAggregateNotificationCount(0, 0)),
        val notifyOfLocalEcho: Boolean = false
)
