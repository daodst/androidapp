

package org.matrix.android.sdk.internal.session.signout

import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.cleanup.CleanupSession
import org.matrix.android.sdk.internal.session.identity.IdentityDisconnectTask
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject

internal interface SignOutTask : Task<SignOutTask.Params, Unit> {
    data class Params(
            val signOutFromHomeserver: Boolean
    )
}

internal class DefaultSignOutTask @Inject constructor(
        private val signOutAPI: SignOutAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        private val identityDisconnectTask: IdentityDisconnectTask,
        private val cleanupSession: CleanupSession
) : SignOutTask {

    override suspend fun execute(params: SignOutTask.Params) {
        
        if (params.signOutFromHomeserver) {
            cleanupSession.stopActiveTasks()
            Timber.d("SignOut: send request...")
            try {
                executeRequest(globalErrorReceiver) {
                    signOutAPI.signOut()
                }
            } catch (throwable: Throwable) {
                
                if (throwable is Failure.ServerError &&
                        throwable.httpCode == HttpURLConnection.HTTP_UNAUTHORIZED && 
                        throwable.error.code == MatrixError.M_UNKNOWN_TOKEN) {
                    
                    
                    Timber.w("Ignore error due to https://github.com/matrix-org/synapse/issues/5755")
                } else {
                    throw throwable
                }
            }
        }

        
        runCatching { identityDisconnectTask.execute(Unit) }
                .onFailure {
                    if (it is IdentityServiceError.NoIdentityServerConfigured) {
                        Timber.i("No identity server configured to disconnect")
                    } else {
                        Timber.w(it, "Unable to disconnect identity server")
                    }
                }

        Timber.d("SignOut: cleanup session...")
        cleanupSession.cleanup()
    }
}
