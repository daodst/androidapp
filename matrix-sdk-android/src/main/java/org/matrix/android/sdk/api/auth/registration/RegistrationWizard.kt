

package org.matrix.android.sdk.api.auth.registration

import org.matrix.android.sdk.api.util.JsonDict


interface RegistrationWizard {
    
    suspend fun getRegistrationFlow(): RegistrationResult

    
    suspend fun registrationAvailable(userName: String): RegistrationAvailability

    
    suspend fun createAccount(userName: String?,
                              password: String?,
                              initialDeviceDisplayName: String?): RegistrationResult

    
    suspend fun performReCaptcha(response: String): RegistrationResult

    
    suspend fun acceptTerms(): RegistrationResult

    
    suspend fun dummy(): RegistrationResult

    
    suspend fun registrationCustom(authParams: JsonDict): RegistrationResult

    
    suspend fun addThreePid(threePid: RegisterThreePid): RegistrationResult

    
    suspend fun sendAgainThreePid(): RegistrationResult

    
    suspend fun handleValidateThreePid(code: String): RegistrationResult

    
    suspend fun checkIfEmailHasBeenValidated(delayMillis: Long): RegistrationResult

    
    val currentThreePid: String?

    
    val isRegistrationStarted: Boolean
}
