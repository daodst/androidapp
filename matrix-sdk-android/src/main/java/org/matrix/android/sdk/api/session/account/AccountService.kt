

package org.matrix.android.sdk.api.session.account

import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor


interface AccountService {
    
    suspend fun changePassword(password: String,
                               newPassword: String)

    
    suspend fun deactivateAccount(eraseAllData: Boolean,
                                  userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor)
}
