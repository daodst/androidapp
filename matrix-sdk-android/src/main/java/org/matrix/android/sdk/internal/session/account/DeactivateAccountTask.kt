

package org.matrix.android.sdk.internal.session.account

import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.internal.auth.registration.handleUIA
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.cleanup.CleanupSession
import org.matrix.android.sdk.internal.session.identity.IdentityDisconnectTask
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface DeactivateAccountTask : Task<DeactivateAccountTask.Params, Unit> {
    data class Params(
            val eraseAllData: Boolean,
            val userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor,
            val userAuthParam: UIABaseAuth? = null
    )
}

internal class DefaultDeactivateAccountTask @Inject constructor(
        private val accountAPI: AccountAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        private val identityDisconnectTask: IdentityDisconnectTask,
        private val cleanupSession: CleanupSession
) : DeactivateAccountTask {

    override suspend fun execute(params: DeactivateAccountTask.Params) {
        val deactivateAccountParams = DeactivateAccountParams.create(params.userAuthParam, params.eraseAllData)
        cleanupSession.stopActiveTasks()
        val canCleanup = try {
            executeRequest(globalErrorReceiver) {
                accountAPI.deactivate(deactivateAccountParams)
            }
            true
        } catch (throwable: Throwable) {
            if (!handleUIA(
                            failure = throwable,
                            interceptor = params.userInteractiveAuthInterceptor,
                            retryBlock = { authUpdate ->
                                execute(params.copy(userAuthParam = authUpdate))
                            }
                    )
            ) {
                Timber.d("## UIA: propagate failure")
                throw throwable
            } else {
                false
            }
        }

        if (canCleanup) {
            
            runCatching { identityDisconnectTask.execute(Unit) }
                    .onFailure { Timber.w(it, "Unable to disconnect identity server") }

            cleanupSession.cleanup()
        }
    }
}
