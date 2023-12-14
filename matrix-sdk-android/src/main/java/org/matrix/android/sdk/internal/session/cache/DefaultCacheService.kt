

package org.matrix.android.sdk.internal.session.cache

import org.matrix.android.sdk.api.session.cache.CacheService
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.task.TaskExecutor
import javax.inject.Inject

internal class DefaultCacheService @Inject constructor(
        @SessionDatabase private val clearCacheTask: ClearCacheTask,
        private val taskExecutor: TaskExecutor
) : CacheService {

    override suspend fun clearCache() {
        taskExecutor.cancelAll()
        clearCacheTask.execute(Unit)
    }
}
