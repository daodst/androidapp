

package org.matrix.android.sdk.api.session.identity

import org.matrix.android.sdk.api.session.identity.model.SignInvitationResult


interface IdentityService {
    
    fun getDefaultIdentityServer(): String?

    
    fun getCurrentIdentityServerUrl(): String?

    
    suspend fun isValidIdentityServer(url: String)

    
    suspend fun setNewIdentityServer(url: String): String

    
    suspend fun disconnect()

    
    suspend fun startBindThreePid(threePid: ThreePid)

    
    suspend fun cancelBindThreePid(threePid: ThreePid)

    
    suspend fun sendAgainValidationCode(threePid: ThreePid)

    
    suspend fun submitValidationToken(threePid: ThreePid, code: String)

    
    suspend fun finalizeBindThreePid(threePid: ThreePid)

    
    suspend fun unbindThreePid(threePid: ThreePid)

    
    suspend fun lookUp(threePids: List<ThreePid>): List<FoundThreePid>

    
    fun getUserConsent(): Boolean

    
    fun setUserConsent(newValue: Boolean)

    
    suspend fun getShareStatus(threePids: List<ThreePid>): Map<ThreePid, SharedState>

    
    suspend fun sign3pidInvitation(identiyServer: String, token: String, secret: String): SignInvitationResult

    fun addListener(listener: IdentityServiceListener)

    fun removeListener(listener: IdentityServiceListener)
}
