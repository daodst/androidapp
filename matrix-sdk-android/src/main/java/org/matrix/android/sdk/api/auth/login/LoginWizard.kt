

package org.matrix.android.sdk.api.auth.login

import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict


interface LoginWizard {
    
    suspend fun getProfileInfo(matrixId: String): LoginProfileInfo

    
    suspend fun login(login: String,
                      password: String,
                      initialDeviceName: String,
                      deviceId: String? = null): Session

    
    suspend fun loginWithBlockChain(login: String,
                                    password: String,
                                    sign: String,
                                    timestamp: String,
                                    pubkey: String,
                                    initialDeviceName: String,
                                    deviceId: String? = null,
                                    chat_pub_key: String,
                                    chat_sign: String): Session

    
    suspend fun loginWithJwt(userId: String,
                             jwt: String,
                             initialDeviceName: String,
                             deviceId: String? = null): Session

    
    suspend fun loginWithToken(loginToken: String): Session

    
    suspend fun loginCustom(data: JsonDict): Session

    
    suspend fun resetPassword(email: String,
                              newPassword: String)

    
    suspend fun resetPasswordMailConfirmed()
}
