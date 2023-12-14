

package org.matrix.android.sdk.internal.session.filter

import org.matrix.android.sdk.api.session.sync.FilterService
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface SaveFilterTask : Task<SaveFilterTask.Params, Unit> {

    data class Params(
            val filterPreset: FilterService.FilterPreset
    )
}

internal class DefaultSaveFilterTask @Inject constructor(
        @UserId private val userId: String,
        private val filterAPI: FilterApi,
        private val filterRepository: FilterRepository,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SaveFilterTask {

    override suspend fun execute(params: SaveFilterTask.Params) {
        val filterBody = when (params.filterPreset) {
            FilterService.FilterPreset.ElementFilter -> {
                FilterFactory.createElementFilter()
            }
            FilterService.FilterPreset.NoFilter      -> {
                FilterFactory.createDefaultFilter()
            }
        }
        val roomFilter = when (params.filterPreset) {
            FilterService.FilterPreset.ElementFilter -> {
                FilterFactory.createElementRoomFilter()
            }
            FilterService.FilterPreset.NoFilter      -> {
                FilterFactory.createDefaultRoomFilter()
            }
        }
        val updated = filterRepository.storeFilter(filterBody, roomFilter)
        if (updated) {
            val filterResponse = executeRequest(globalErrorReceiver) {
                
                filterAPI.uploadFilter(userId, filterBody)
            }
            filterRepository.storeFilterId(filterBody, filterResponse.filterId)
        }
    }
}
