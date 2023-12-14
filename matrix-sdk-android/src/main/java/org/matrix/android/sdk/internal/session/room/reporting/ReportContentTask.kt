

package org.matrix.android.sdk.internal.session.room.reporting

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface ReportContentTask : Task<ReportContentTask.Params, Unit> {
    data class Params(
            val roomId: String,
            val eventId: String,
            val score: Int,
            val reason: String
    )
}

internal class DefaultReportContentTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : ReportContentTask {

    override suspend fun execute(params: ReportContentTask.Params) {
        return executeRequest(globalErrorReceiver) {
            roomAPI.reportContent(params.roomId, params.eventId, ReportContentBody(params.score, params.reason))
        }
    }
}
