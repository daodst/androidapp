

package org.matrix.android.sdk.internal.session.media

import com.zhuinden.monarchy.Monarchy
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntity
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject

internal interface ClearPreviewUrlCacheTask : Task<Unit, Unit>

internal class DefaultClearPreviewUrlCacheTask @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy
) : ClearPreviewUrlCacheTask {

    override suspend fun execute(params: Unit) {
        monarchy.awaitTransaction { realm ->
            realm.where<PreviewUrlCacheEntity>()
                    .findAll()
                    .deleteAllFromRealm()
        }
    }
}
