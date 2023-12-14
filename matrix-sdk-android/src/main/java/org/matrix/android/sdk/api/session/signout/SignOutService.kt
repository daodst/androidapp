

package org.matrix.android.sdk.api.session.signout

import org.matrix.android.sdk.api.auth.data.Credentials


interface SignOutService {

    
    suspend fun signInAgain(password: String)

    
    suspend fun updateCredentials(credentials: Credentials)

    
    suspend fun signOut(signOutFromHomeserver: Boolean)
}
