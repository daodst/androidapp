

package org.matrix.android.sdk.api.auth

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.data.LoginFlowResult
import org.matrix.android.sdk.api.auth.login.LoginWizard
import org.matrix.android.sdk.api.auth.registration.RegistrationWizard
import org.matrix.android.sdk.api.auth.wellknown.WellknownResult
import org.matrix.android.sdk.api.session.Session


interface AuthenticationService {
    
    suspend fun getLoginFlow(homeServerConnectionConfig: HomeServerConnectionConfig): LoginFlowResult

    
    suspend fun getLoginFlowOfSession(sessionId: String): LoginFlowResult

    
    fun getSsoUrl(redirectUrl: String, deviceId: String?, providerId: String?): String?
    fun getAccessToken(): String?

    
    fun getFallbackUrl(forSignIn: Boolean, deviceId: String?): String?

    
    fun getLoginWizard(): LoginWizard

    
    fun getRegistrationWizard(): RegistrationWizard

    
    val isRegistrationStarted: Boolean

    
    suspend fun cancelPendingLoginOrRegistration()

    
    suspend fun reset()

    
    fun hasAuthenticatedSessions(): Boolean

    
    fun getLastAuthenticatedSession(): Session?

    fun isLogin(address: String): Boolean

    
    suspend fun createSessionFromSso(homeServerConnectionConfig: HomeServerConnectionConfig,
                                     credentials: Credentials): Session

    
    suspend fun getWellKnownData(matrixId: String,
                                 homeServerConnectionConfig: HomeServerConnectionConfig?): WellknownResult

    
    suspend fun directAuthentication(homeServerConnectionConfig: HomeServerConnectionConfig,
                                     matrixId: String,
                                     password: String,
                                     initialDeviceName: String,
                                     deviceId: String? = null): Session
}
