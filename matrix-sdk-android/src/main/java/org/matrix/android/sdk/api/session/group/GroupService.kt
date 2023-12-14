

package org.matrix.android.sdk.api.session.group

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.group.model.GroupSummary


interface GroupService {

    
    fun getGroup(groupId: String): Group?

    
    fun getGroupSummary(groupId: String): GroupSummary?

    
    fun getGroupSummaries(groupSummaryQueryParams: GroupSummaryQueryParams): List<GroupSummary>

    
    fun getGroupSummariesLive(groupSummaryQueryParams: GroupSummaryQueryParams): LiveData<List<GroupSummary>>
}
