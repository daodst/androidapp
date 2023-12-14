

package org.matrix.android.sdk.internal.session.filter

import org.matrix.android.sdk.api.session.sync.FilterService
import org.matrix.android.sdk.internal.task.TaskExecutor
import org.matrix.android.sdk.internal.task.configureWith
import javax.inject.Inject

internal class DefaultFilterService @Inject constructor(private val saveFilterTask: SaveFilterTask,
                                                        private val taskExecutor: TaskExecutor) : FilterService {

    
    override fun setFilter(filterPreset: FilterService.FilterPreset) {
        saveFilterTask
                .configureWith(SaveFilterTask.Params(filterPreset))
                .executeBy(taskExecutor)
    }
}
