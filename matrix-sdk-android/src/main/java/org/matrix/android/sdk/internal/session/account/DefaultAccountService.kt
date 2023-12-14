

package org.matrix.android.sdk.internal.session.account

import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.session.account.AccountService
import javax.inject.Inject

internal class DefaultAccountService @Inject constructor(private val changePasswordTask: ChangePasswordTask,
                                                         private val deactivateAccountTask: DeactivateAccountTask) : AccountService {

    override suspend fun changePassword(password: String, newPassword: String) {
        changePasswordTask.execute(ChangePasswordTask.Params(password, newPassword))
    }

    override suspend fun deactivateAccount(eraseAllData: Boolean, userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor) {
        deactivateAccountTask.execute(DeactivateAccountTask.Params(eraseAllData, userInteractiveAuthInterceptor))
    }
}
