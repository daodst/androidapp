

package org.matrix.android.sdk.api.session.room

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import org.matrix.android.sdk.api.session.room.model.RoomSummary

interface UpdatableLivePageResult {
    val livePagedList: LiveData<PagedList<RoomSummary>>
    val liveBoundaries: LiveData<ResultBoundaries>
    var queryParams: RoomSummaryQueryParams
}

data class ResultBoundaries(
        val frontLoaded: Boolean = false,
        val endLoaded: Boolean = false,
        val zeroItemLoaded: Boolean = false
)
