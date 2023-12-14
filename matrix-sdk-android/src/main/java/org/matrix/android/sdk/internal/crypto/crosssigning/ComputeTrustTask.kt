
package org.matrix.android.sdk.internal.crypto.crosssigning

import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.crypto.crosssigning.MXCrossSigningInfo
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface ComputeTrustTask : Task<ComputeTrustTask.Params, RoomEncryptionTrustLevel> {
    data class Params(
            val activeMemberUserIds: List<String>,
            val isDirectRoom: Boolean
    )
}

internal class DefaultComputeTrustTask @Inject constructor(
        private val cryptoStore: IMXCryptoStore,
        @UserId private val userId: String,
        private val coroutineDispatchers: MatrixCoroutineDispatchers
) : ComputeTrustTask {

    override suspend fun execute(params: ComputeTrustTask.Params): RoomEncryptionTrustLevel = withContext(coroutineDispatchers.crypto) {
        
        
        
        val listToCheck = if (params.isDirectRoom) {
            params.activeMemberUserIds.filter { it != userId }
        } else {
            params.activeMemberUserIds
        }

        val allTrustedUserIds = listToCheck
                .filter { userId -> getUserCrossSigningKeys(userId)?.isTrusted() == true }

        if (allTrustedUserIds.isEmpty()) {
            RoomEncryptionTrustLevel.Default
        } else {
            
            
            
            allTrustedUserIds
                    .mapNotNull { cryptoStore.getUserDeviceList(it) }
                    .flatten()
                    .let { allDevices ->
                        if (getMyCrossSigningKeys() != null) {
                            allDevices.any { !it.trustLevel?.crossSigningVerified.orFalse() }
                        } else {
                            
                            allDevices.any { !it.isVerified }
                        }
                    }
                    .let { hasWarning ->
                        if (hasWarning) {
                            RoomEncryptionTrustLevel.Warning
                        } else {
                            if (listToCheck.size == allTrustedUserIds.size) {
                                
                                RoomEncryptionTrustLevel.Trusted
                            } else {
                                RoomEncryptionTrustLevel.Default
                            }
                        }
                    }
        }
    }

    private fun getUserCrossSigningKeys(otherUserId: String): MXCrossSigningInfo? {
        return cryptoStore.getCrossSigningInfo(otherUserId)
    }

    private fun getMyCrossSigningKeys(): MXCrossSigningInfo? {
        return cryptoStore.getMyCrossSigningInfo()
    }
}
