

package org.matrix.android.sdk.internal.session.group

import org.matrix.android.sdk.api.session.group.Group

internal class DefaultGroup(override val groupId: String,
                            private val getGroupDataTask: GetGroupDataTask) : Group {

    override suspend fun fetchGroupData() {
        val params = GetGroupDataTask.Params.FetchWithIds(listOf(groupId))
        getGroupDataTask.execute(params)
    }
}
