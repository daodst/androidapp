

package org.matrix.android.sdk.internal.session.homeserver

import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.TaskExecutor
import javax.inject.Inject

internal class HomeServerPinger @Inject constructor(private val taskExecutor: TaskExecutor,
                                                    private val capabilitiesAPI: CapabilitiesAPI) {

    fun canReachHomeServer(callback: (Boolean) -> Unit) {
        taskExecutor.executorScope.launch {
            val canReach = canReachHomeServer()
            callback(canReach)
        }
    }

    suspend fun canReachHomeServer(): Boolean {
        return try {
            executeRequest(null) {
                capabilitiesAPI.ping()
            }
            true
        } catch (throwable: Throwable) {
            if (throwable is Failure.OtherServerError) {
                (throwable.httpCode == 404 || throwable.httpCode == 400)
            } else {
                false
            }
        }
    }
}
