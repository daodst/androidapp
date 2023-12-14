

package org.matrix.android.sdk.internal.crypto.verification.qrcode

import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.QrCodeVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.util.fromBase64
import org.matrix.android.sdk.internal.crypto.IncomingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.actions.SetDeviceVerificationAction
import org.matrix.android.sdk.internal.crypto.crosssigning.fromBase64Safe
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.verification.DefaultVerificationTransaction
import org.matrix.android.sdk.internal.crypto.verification.ValidVerificationInfoStart
import timber.log.Timber

internal class DefaultQrCodeVerificationTransaction(
        setDeviceVerificationAction: SetDeviceVerificationAction,
        override val transactionId: String,
        override val otherUserId: String,
        override var otherDeviceId: String?,
        private val crossSigningService: CrossSigningService,
        outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        incomingGossipingRequestManager: IncomingGossipingRequestManager,
        private val cryptoStore: IMXCryptoStore,
        
        private val qrCodeData: QrCodeData?,
        val userId: String,
        val deviceId: String,
        override val isIncoming: Boolean
) : DefaultVerificationTransaction(
        setDeviceVerificationAction,
        crossSigningService,
        outgoingGossipingRequestManager,
        incomingGossipingRequestManager,
        userId,
        transactionId,
        otherUserId,
        otherDeviceId,
        isIncoming),
        QrCodeVerificationTransaction {

    override val qrCodeText: String?
        get() = qrCodeData?.toEncodedString()

    override var state: VerificationTxState = VerificationTxState.None
        set(newState) {
            field = newState

            listeners.forEach {
                try {
                    it.transactionUpdated(this)
                } catch (e: Throwable) {
                    Timber.e(e, "## Error while notifying listeners")
                }
            }
        }

    override fun userHasScannedOtherQrCode(otherQrCodeText: String) {
        val otherQrCodeData = otherQrCodeText.toQrCodeData() ?: run {
            Timber.d("## Verification QR: Invalid QR Code Data")
            cancel(CancelCode.QrCodeInvalid)
            return
        }

        
        if (otherQrCodeData.transactionId != transactionId) {
            Timber.d("## Verification QR: Invalid transaction actual ${otherQrCodeData.transactionId} expected:$transactionId")
            cancel(CancelCode.QrCodeInvalid)
            return
        }

        
        val myMasterKey = crossSigningService.getUserCrossSigningKeys(userId)?.masterKey()?.unpaddedBase64PublicKey
        var canTrustOtherUserMasterKey = false

        
        when (otherQrCodeData) {
            is QrCodeData.VerifyingAnotherUser             -> {
                
                
                
                if (otherQrCodeData.otherUserMasterCrossSigningPublicKey != myMasterKey) {
                    Timber.d("## Verification QR: Invalid other master key ${otherQrCodeData.otherUserMasterCrossSigningPublicKey}")
                    cancel(CancelCode.MismatchedKeys)
                    return
                } else Unit
            }
            is QrCodeData.SelfVerifyingMasterKeyTrusted    -> {
                
                
                
                if (otherQrCodeData.userMasterCrossSigningPublicKey != myMasterKey) {
                    Timber.d("## Verification QR: Invalid other master key ${otherQrCodeData.userMasterCrossSigningPublicKey}")
                    cancel(CancelCode.MismatchedKeys)
                    return
                } else {
                    
                    canTrustOtherUserMasterKey = true
                }
            }
            is QrCodeData.SelfVerifyingMasterKeyNotTrusted -> {
                
                
                
                if (otherQrCodeData.userMasterCrossSigningPublicKey != myMasterKey) {
                    Timber.d("## Verification QR: Invalid other master key ${otherQrCodeData.userMasterCrossSigningPublicKey}")
                    cancel(CancelCode.MismatchedKeys)
                    return
                } else {
                    
                }
            }
        }

        val toVerifyDeviceIds = mutableListOf<String>()

        
        when (otherQrCodeData) {
            is QrCodeData.VerifyingAnotherUser             -> {
                
                
                if (otherQrCodeData.userMasterCrossSigningPublicKey
                        != crossSigningService.getUserCrossSigningKeys(otherUserId)?.masterKey()?.unpaddedBase64PublicKey) {
                    Timber.d("## Verification QR: Invalid user master key ${otherQrCodeData.userMasterCrossSigningPublicKey}")
                    cancel(CancelCode.MismatchedKeys)
                    return
                } else {
                    
                    canTrustOtherUserMasterKey = true
                    Unit
                }
            }
            is QrCodeData.SelfVerifyingMasterKeyTrusted    -> {
                
                
                if (otherQrCodeData.otherDeviceKey
                        != cryptoStore.getUserDevice(userId, deviceId)?.fingerprint()) {
                    Timber.d("## Verification QR: Invalid other device key ${otherQrCodeData.otherDeviceKey}")
                    cancel(CancelCode.MismatchedKeys)
                    return
                } else Unit 
                
            }
            is QrCodeData.SelfVerifyingMasterKeyNotTrusted -> {
                
                
                if (otherQrCodeData.deviceKey
                        != cryptoStore.getUserDevice(otherUserId, otherDeviceId ?: "")?.fingerprint()) {
                    Timber.d("## Verification QR: Invalid device key ${otherQrCodeData.deviceKey}")
                    cancel(CancelCode.MismatchedKeys)
                    return
                } else {
                    
                    toVerifyDeviceIds.add(otherDeviceId ?: "")
                    Unit
                }
            }
        }

        if (!canTrustOtherUserMasterKey && toVerifyDeviceIds.isEmpty()) {
            
            cancel(CancelCode.MismatchedKeys)
            return
        }

        
        
        
        start(otherQrCodeData.sharedSecret)

        trust(
                canTrustOtherUserMasterKey = canTrustOtherUserMasterKey,
                toVerifyDeviceIds = toVerifyDeviceIds.distinct(),
                eventuallyMarkMyMasterKeyAsTrusted = true,
                autoDone = false
        )
    }

    private fun start(remoteSecret: String, onDone: (() -> Unit)? = null) {
        if (state != VerificationTxState.None) {
            Timber.e("## Verification QR: start verification from invalid state")
            
            throw IllegalStateException("Interactive Key verification already started")
        }

        state = VerificationTxState.Started
        val startMessage = transport.createStartForQrCode(
                deviceId,
                transactionId,
                remoteSecret
        )

        transport.sendToOther(
                EventType.KEY_VERIFICATION_START,
                startMessage,
                VerificationTxState.WaitingOtherReciprocateConfirm,
                CancelCode.User,
                onDone
        )
    }

    override fun cancel() {
        cancel(CancelCode.User)
    }

    override fun cancel(code: CancelCode) {
        state = VerificationTxState.Cancelled(code, true)
        transport.cancelTransaction(transactionId, otherUserId, otherDeviceId ?: "", code)
    }

    override fun isToDeviceTransport() = false

    
    fun onStartReceived(startReq: ValidVerificationInfoStart.ReciprocateVerificationInfoStart) {
        if (qrCodeData == null) {
            
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        if (startReq.sharedSecret.fromBase64Safe()?.contentEquals(qrCodeData.sharedSecret.fromBase64()) == true) {
            
            
            
            state = VerificationTxState.QrScannedByOther
        } else {
            
            cancel(CancelCode.MismatchedKeys)
        }
    }

    fun onDoneReceived() {
        if (state != VerificationTxState.WaitingOtherReciprocateConfirm) {
            cancel(CancelCode.UnexpectedMessage)
            return
        }
        state = VerificationTxState.Verified
        transport.done(transactionId) {}
    }

    override fun otherUserScannedMyQrCode() {
        when (qrCodeData) {
            is QrCodeData.VerifyingAnotherUser             -> {
                
                trust(true, emptyList(), false)
            }
            is QrCodeData.SelfVerifyingMasterKeyTrusted    -> {
                
                
                trust(false, listOf(otherDeviceId ?: ""), false)
            }
            is QrCodeData.SelfVerifyingMasterKeyNotTrusted -> {
                
                trust(true, emptyList(), true)
            }
            null                                           -> Unit
        }
    }

    override fun otherUserDidNotScannedMyQrCode() {
        
        
        cancel(CancelCode.MismatchedKeys)
    }
}
