

package org.matrix.android.sdk.internal.session.call

import org.matrix.android.sdk.api.session.call.TurnServerResponse
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal abstract class GetTurnServerTask : Task<GetTurnServerTask.Params, TurnServerResponse> {
    object Params
}

internal class DefaultGetTurnServerTask @Inject constructor(private val voipAPI: VoipApi,
                                                            private val globalErrorReceiver: GlobalErrorReceiver) : GetTurnServerTask() {

    override suspend fun execute(params: Params): TurnServerResponse {
        return executeRequest(globalErrorReceiver) {
            voipAPI.getTurnServer()
        }
    }
}
