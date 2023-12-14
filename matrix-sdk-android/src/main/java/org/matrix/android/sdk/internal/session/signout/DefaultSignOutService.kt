

package org.matrix.android.sdk.internal.session.signout

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.session.signout.SignOutService
import org.matrix.android.sdk.internal.auth.SessionParamsStore
import javax.inject.Inject

internal class DefaultSignOutService @Inject constructor(private val signOutTask: SignOutTask,
                                                         private val signInAgainTask: SignInAgainTask,
                                                         private val sessionParamsStore: SessionParamsStore
) : SignOutService {

    override suspend fun signInAgain(password: String) {
        signInAgainTask.execute(SignInAgainTask.Params(password))
    }

    override suspend fun updateCredentials(credentials: Credentials) {
        sessionParamsStore.updateCredentials(credentials)
    }

    override suspend fun signOut(signOutFromHomeserver: Boolean) {
        return signOutTask.execute(SignOutTask.Params(signOutFromHomeserver))
    }
}
