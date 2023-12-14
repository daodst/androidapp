

package org.matrix.android.sdk.internal.session.group

import org.matrix.android.sdk.api.session.group.Group
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

internal interface GroupFactory {
    fun create(groupId: String): Group
}

@SessionScope
internal class DefaultGroupFactory @Inject constructor(private val getGroupDataTask: GetGroupDataTask) :
        GroupFactory {

    override fun create(groupId: String): Group {
        return DefaultGroup(
                groupId = groupId,
                getGroupDataTask = getGroupDataTask
        )
    }
}
