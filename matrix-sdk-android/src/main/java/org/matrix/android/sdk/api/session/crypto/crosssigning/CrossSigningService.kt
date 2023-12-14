

package org.matrix.android.sdk.api.session.crypto.crosssigning

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.util.Optional

interface CrossSigningService {

    fun isCrossSigningVerified(): Boolean

    fun isUserTrusted(otherUserId: String): Boolean

    
    fun checkUserTrust(otherUserId: String): UserTrustResult

    
    fun initializeCrossSigning(uiaInterceptor: UserInteractiveAuthInterceptor?,
                               callback: MatrixCallback<Unit>)

    fun isCrossSigningInitialized(): Boolean = getMyCrossSigningKeys() != null

    fun checkTrustFromPrivateKeys(masterKeyPrivateKey: String?,
                                  uskKeyPrivateKey: String?,
                                  sskPrivateKey: String?): UserTrustResult

    fun getUserCrossSigningKeys(otherUserId: String): MXCrossSigningInfo?

    fun getLiveCrossSigningKeys(userId: String): LiveData<Optional<MXCrossSigningInfo>>

    fun getMyCrossSigningKeys(): MXCrossSigningInfo?

    fun getCrossSigningPrivateKeys(): PrivateKeysInfo?

    fun getLiveCrossSigningPrivateKeys(): LiveData<Optional<PrivateKeysInfo>>

    fun canCrossSign(): Boolean

    fun allPrivateKeysKnown(): Boolean

    fun trustUser(otherUserId: String,
                  callback: MatrixCallback<Unit>)

    fun markMyMasterKeyAsTrusted()

    
    fun trustDevice(deviceId: String,
                    callback: MatrixCallback<Unit>)

    fun checkDeviceTrust(otherUserId: String,
                         otherDeviceId: String,
                         locallyTrusted: Boolean?): DeviceTrustResult

    
    fun onSecretMSKGossip(mskPrivateKey: String)
    fun onSecretSSKGossip(sskPrivateKey: String)
    fun onSecretUSKGossip(uskPrivateKey: String)
}
