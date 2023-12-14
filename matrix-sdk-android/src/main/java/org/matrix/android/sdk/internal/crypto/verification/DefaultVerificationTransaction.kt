
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.internal.crypto.IncomingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.actions.SetDeviceVerificationAction
import timber.log.Timber


internal abstract class DefaultVerificationTransaction(
        private val setDeviceVerificationAction: SetDeviceVerificationAction,
        private val crossSigningService: CrossSigningService,
        private val outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        private val incomingGossipingRequestManager: IncomingGossipingRequestManager,
        private val userId: String,
        override val transactionId: String,
        override val otherUserId: String,
        override var otherDeviceId: String? = null,
        override val isIncoming: Boolean) : VerificationTransaction {

    lateinit var transport: VerificationTransport

    interface Listener {
        fun transactionUpdated(tx: VerificationTransaction)
    }

    protected var listeners = ArrayList<Listener>()

    fun addListener(listener: Listener) {
        if (!listeners.contains(listener)) listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    protected fun trust(canTrustOtherUserMasterKey: Boolean,
                        toVerifyDeviceIds: List<String>,
                        eventuallyMarkMyMasterKeyAsTrusted: Boolean, autoDone: Boolean = true) {
        Timber.d("## Verification: trust ($otherUserId,$otherDeviceId) , verifiedDevices:$toVerifyDeviceIds")
        Timber.d("## Verification: trust Mark myMSK trusted $eventuallyMarkMyMasterKeyAsTrusted")

        
        toVerifyDeviceIds.forEach {
            setDeviceVerified(otherUserId, it)
        }

        
        if (canTrustOtherUserMasterKey) {
            
            
            if (otherUserId != userId) {
                crossSigningService.trustUser(otherUserId, object : MatrixCallback<Unit> {
                    override fun onFailure(failure: Throwable) {
                        Timber.e(failure, "## Verification: Failed to trust User $otherUserId")
                    }
                })
            } else {
                
                if (eventuallyMarkMyMasterKeyAsTrusted) {
                    
                    crossSigningService.markMyMasterKeyAsTrusted()
                }
            }
        }

        if (otherUserId == userId) {
            incomingGossipingRequestManager.onVerificationCompleteForDevice(otherDeviceId!!)

            
            
            crossSigningService.trustDevice(otherDeviceId!!, object : MatrixCallback<Unit> {
                override fun onFailure(failure: Throwable) {
                    Timber.w("## Verification: Failed to sign new device $otherDeviceId, ${failure.localizedMessage}")
                }
            })
        }

        if (autoDone) {
            state = VerificationTxState.Verified
            transport.done(transactionId) {}
        }
    }

    private fun setDeviceVerified(userId: String, deviceId: String) {
        
        setDeviceVerificationAction.handle(DeviceTrustLevel(crossSigningVerified = false, locallyVerified = true),
                userId,
                deviceId)
    }
}
