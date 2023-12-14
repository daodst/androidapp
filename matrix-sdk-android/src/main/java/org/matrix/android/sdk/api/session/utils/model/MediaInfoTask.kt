

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.api.session.utils.bean.MediaInfo
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface MediaInfoTask : Task<Unit, MediaInfo> {

}

internal class DefaultMediaInfoTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : MediaInfoTask {
    override suspend fun execute(params: Unit): MediaInfo {
        val response = executeRequest(globalErrorReceiver) {
            utilsAPI.getMediaInfo()
        }
        return response
    }
}
