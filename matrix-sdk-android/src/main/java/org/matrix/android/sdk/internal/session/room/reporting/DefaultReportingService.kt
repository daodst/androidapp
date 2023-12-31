

package org.matrix.android.sdk.internal.session.room.reporting

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.matrix.android.sdk.api.session.room.reporting.ReportingService

internal class DefaultReportingService @AssistedInject constructor(@Assisted private val roomId: String,
                                                                   private val reportContentTask: ReportContentTask
) : ReportingService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultReportingService
    }

    override suspend fun reportContent(eventId: String, score: Int, reason: String) {
        val params = ReportContentTask.Params(roomId, eventId, score, reason)
        reportContentTask.execute(params)
    }
}
