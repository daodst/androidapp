
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.OutgoingSasVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.internal.crypto.IncomingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.actions.SetDeviceVerificationAction
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import timber.log.Timber

internal class DefaultOutgoingSASDefaultVerificationTransaction(
        setDeviceVerificationAction: SetDeviceVerificationAction,
        userId: String,
        deviceId: String?,
        cryptoStore: IMXCryptoStore,
        crossSigningService: CrossSigningService,
        outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        incomingGossipingRequestManager: IncomingGossipingRequestManager,
        deviceFingerprint: String,
        transactionId: String,
        otherUserId: String,
        otherDeviceId: String
) : SASDefaultVerificationTransaction(
        setDeviceVerificationAction,
        userId,
        deviceId,
        cryptoStore,
        crossSigningService,
        outgoingGossipingRequestManager,
        incomingGossipingRequestManager,
        deviceFingerprint,
        transactionId,
        otherUserId,
        otherDeviceId,
        isIncoming = false),
        OutgoingSasVerificationTransaction {

    override val uxState: OutgoingSasVerificationTransaction.UxState
        get() {
            return when (val immutableState = state) {
                is VerificationTxState.None           -> OutgoingSasVerificationTransaction.UxState.WAIT_FOR_START
                is VerificationTxState.SendingStart,
                is VerificationTxState.Started,
                is VerificationTxState.OnAccepted,
                is VerificationTxState.SendingKey,
                is VerificationTxState.KeySent,
                is VerificationTxState.OnKeyReceived  -> OutgoingSasVerificationTransaction.UxState.WAIT_FOR_KEY_AGREEMENT
                is VerificationTxState.ShortCodeReady -> OutgoingSasVerificationTransaction.UxState.SHOW_SAS
                is VerificationTxState.ShortCodeAccepted,
                is VerificationTxState.SendingMac,
                is VerificationTxState.MacSent,
                is VerificationTxState.Verifying      -> OutgoingSasVerificationTransaction.UxState.WAIT_FOR_VERIFICATION
                is VerificationTxState.Verified       -> OutgoingSasVerificationTransaction.UxState.VERIFIED
                is VerificationTxState.Cancelled      -> {
                    if (immutableState.byMe) {
                        OutgoingSasVerificationTransaction.UxState.CANCELLED_BY_OTHER
                    } else {
                        OutgoingSasVerificationTransaction.UxState.CANCELLED_BY_ME
                    }
                }
                else                                  -> OutgoingSasVerificationTransaction.UxState.UNKNOWN
            }
        }

    override fun onVerificationStart(startReq: ValidVerificationInfoStart.SasVerificationInfoStart) {
        Timber.e("## SAS O: onVerificationStart - unexpected id:$transactionId")
        cancel(CancelCode.UnexpectedMessage)
    }

    fun start() {
        if (state != VerificationTxState.None) {
            Timber.e("## SAS O: start verification from invalid state")
            
            throw IllegalStateException("Interactive Key verification already started")
        }

        val startMessage = transport.createStartForSas(
                deviceId ?: "",
                transactionId,
                KNOWN_AGREEMENT_PROTOCOLS,
                KNOWN_HASHES,
                KNOWN_MACS,
                KNOWN_SHORT_CODES
        )

        startReq = startMessage.asValidObject() as? ValidVerificationInfoStart.SasVerificationInfoStart
        state = VerificationTxState.SendingStart

        sendToOther(
                EventType.KEY_VERIFICATION_START,
                startMessage,
                VerificationTxState.Started,
                CancelCode.User,
                null
        )
    }






    override fun onVerificationAccept(accept: ValidVerificationInfoAccept) {
        Timber.v("## SAS O: onVerificationAccept id:$transactionId")
        if (state != VerificationTxState.Started && state != VerificationTxState.SendingStart) {
            Timber.e("## SAS O: received accept request from invalid state $state")
            cancel(CancelCode.UnexpectedMessage)
            return
        }
        
        if (!KNOWN_AGREEMENT_PROTOCOLS.contains(accept.keyAgreementProtocol) ||
                !KNOWN_HASHES.contains(accept.hash) ||
                !KNOWN_MACS.contains(accept.messageAuthenticationCode) ||
                accept.shortAuthenticationStrings.intersect(KNOWN_SHORT_CODES).isEmpty()) {
            Timber.e("## SAS O: received invalid accept")
            cancel(CancelCode.UnknownMethod)
            return
        }

        
        
        accepted = accept
        state = VerificationTxState.OnAccepted

        
        
        val pubKey = getSAS().publicKey

        val keyToDevice = transport.createKey(transactionId, pubKey)
        
        state = VerificationTxState.SendingKey
        sendToOther(EventType.KEY_VERIFICATION_KEY, keyToDevice, VerificationTxState.KeySent, CancelCode.User) {
            
            if (state == VerificationTxState.SendingKey) {
                state = VerificationTxState.KeySent
            }
        }
    }

    override fun onKeyVerificationKey(vKey: ValidVerificationInfoKey) {
        Timber.v("## SAS O: onKeyVerificationKey id:$transactionId")
        if (state != VerificationTxState.SendingKey && state != VerificationTxState.KeySent) {
            Timber.e("## received key from invalid state $state")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        otherKey = vKey.key
        
        
        
        

        
        val concat = vKey.key + startReq!!.canonicalJson
        val otherCommitment = hashUsingAgreedHashMethod(concat) ?: ""

        if (accepted!!.commitment.equals(otherCommitment)) {
            getSAS().setTheirPublicKey(otherKey)
            shortCodeBytes = calculateSASBytes()
            state = VerificationTxState.ShortCodeReady
        } else {
            
            cancel(CancelCode.MismatchedCommitment)
        }
    }

    private fun calculateSASBytes(): ByteArray {
        when (accepted?.keyAgreementProtocol) {
            KEY_AGREEMENT_V1 -> {
                
                
                
                
                
                
                
                
                val sasInfo = "MATRIX_KEY_VERIFICATION_SAS$userId$deviceId$otherUserId$otherDeviceId$transactionId"

                
                
                return getSAS().generateShortCode(sasInfo, 6)
            }
            KEY_AGREEMENT_V2 -> {
                
                val sasInfo = "MATRIX_KEY_VERIFICATION_SAS|$userId|$deviceId|${getSAS().publicKey}|$otherUserId|$otherDeviceId|$otherKey|$transactionId"
                return getSAS().generateShortCode(sasInfo, 6)
            }
            else             -> {
                
                throw IllegalArgumentException()
            }
        }
    }

    override fun onKeyVerificationMac(vMac: ValidVerificationInfoMac) {
        Timber.v("## SAS O: onKeyVerificationMac id:$transactionId")
        
        if (state != VerificationTxState.OnKeyReceived &&
                state != VerificationTxState.ShortCodeReady &&
                state != VerificationTxState.ShortCodeAccepted &&
                state != VerificationTxState.KeySent &&
                state != VerificationTxState.SendingMac &&
                state != VerificationTxState.MacSent) {
            Timber.e("## SAS O: received mac from invalid state $state")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        theirMac = vMac

        
        if (myMac != null) {
            
            verifyMacs(vMac)
        }
        
    }
}
