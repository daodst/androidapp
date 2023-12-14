

package org.matrix.android.sdk.internal.session.user.accountdata

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.internal.database.model.BreadcrumbsEntity
import org.matrix.android.sdk.internal.database.query.get
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.sync.model.accountdata.BreadcrumbsContent
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.fetchCopied
import javax.inject.Inject

private const val MAX_BREADCRUMBS_ROOMS_NUMBER = 20

internal interface UpdateBreadcrumbsTask : Task<UpdateBreadcrumbsTask.Params, Unit> {
    data class Params(
            val newTopRoomId: String
    )
}

internal class DefaultUpdateBreadcrumbsTask @Inject constructor(
        private val saveBreadcrumbsTask: SaveBreadcrumbsTask,
        private val updateUserAccountDataTask: UpdateUserAccountDataTask,
        @SessionDatabase private val monarchy: Monarchy
) : UpdateBreadcrumbsTask {

    override suspend fun execute(params: UpdateBreadcrumbsTask.Params) {
        val newBreadcrumbs =
                
                monarchy.fetchCopied { BreadcrumbsEntity.get(it) }
                        ?.recentRoomIds
                        ?.apply {
                            
                            
                            remove(params.newTopRoomId)
                            
                            add(0, params.newTopRoomId)
                        }
                        ?.take(MAX_BREADCRUMBS_ROOMS_NUMBER)
                        ?: listOf(params.newTopRoomId)

        
        saveBreadcrumbsTask.execute(SaveBreadcrumbsTask.Params(newBreadcrumbs))

        
        
        updateUserAccountDataTask.execute(UpdateUserAccountDataTask.BreadcrumbsParams(
                breadcrumbsContent = BreadcrumbsContent(newBreadcrumbs)
        ))
    }
}
