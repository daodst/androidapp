
package org.matrix.android.sdk.internal.crypto.verification

import android.util.Base64
import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.IncomingSasVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.SasMode
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.internal.crypto.IncomingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.actions.SetDeviceVerificationAction
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import timber.log.Timber

internal class DefaultIncomingSASDefaultVerificationTransaction(
        setDeviceVerificationAction: SetDeviceVerificationAction,
        override val userId: String,
        override val deviceId: String?,
        private val cryptoStore: IMXCryptoStore,
        crossSigningService: CrossSigningService,
        outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        incomingGossipingRequestManager: IncomingGossipingRequestManager,
        deviceFingerprint: String,
        transactionId: String,
        otherUserID: String,
        private val autoAccept: Boolean = false
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
        otherUserID,
        null,
        isIncoming = true),
        IncomingSasVerificationTransaction {

    override val uxState: IncomingSasVerificationTransaction.UxState
        get() {
            return when (val immutableState = state) {
                is VerificationTxState.OnStarted      -> IncomingSasVerificationTransaction.UxState.SHOW_ACCEPT
                is VerificationTxState.SendingAccept,
                is VerificationTxState.Accepted,
                is VerificationTxState.OnKeyReceived,
                is VerificationTxState.SendingKey,
                is VerificationTxState.KeySent        -> IncomingSasVerificationTransaction.UxState.WAIT_FOR_KEY_AGREEMENT
                is VerificationTxState.ShortCodeReady -> IncomingSasVerificationTransaction.UxState.SHOW_SAS
                is VerificationTxState.ShortCodeAccepted,
                is VerificationTxState.SendingMac,
                is VerificationTxState.MacSent,
                is VerificationTxState.Verifying      -> IncomingSasVerificationTransaction.UxState.WAIT_FOR_VERIFICATION
                is VerificationTxState.Verified       -> IncomingSasVerificationTransaction.UxState.VERIFIED
                is VerificationTxState.Cancelled      -> {
                    if (immutableState.byMe) {
                        IncomingSasVerificationTransaction.UxState.CANCELLED_BY_ME
                    } else {
                        IncomingSasVerificationTransaction.UxState.CANCELLED_BY_OTHER
                    }
                }
                else                                  -> IncomingSasVerificationTransaction.UxState.UNKNOWN
            }
        }

    override fun onVerificationStart(startReq: ValidVerificationInfoStart.SasVerificationInfoStart) {
        Timber.v("## SAS I: received verification request from state $state")
        if (state != VerificationTxState.None) {
            Timber.e("## SAS I: received verification request from invalid state")
            
            throw IllegalStateException("Interactive Key verification already started")
        }
        this.startReq = startReq
        state = VerificationTxState.OnStarted
        this.otherDeviceId = startReq.fromDevice

        if (autoAccept) {
            performAccept()
        }
    }

    override fun performAccept() {
        if (state != VerificationTxState.OnStarted) {
            Timber.e("## SAS Cannot perform accept from state $state")
            return
        }

        
        
        val agreedProtocol = startReq!!.keyAgreementProtocols.firstOrNull { KNOWN_AGREEMENT_PROTOCOLS.contains(it) }
        val agreedHash = startReq!!.hashes.firstOrNull { KNOWN_HASHES.contains(it) }
        val agreedMac = startReq!!.messageAuthenticationCodes.firstOrNull { KNOWN_MACS.contains(it) }
        val agreedShortCode = startReq!!.shortAuthenticationStrings.filter { KNOWN_SHORT_CODES.contains(it) }

        
        
        
        if (listOf(agreedProtocol, agreedHash, agreedMac).any { it.isNullOrBlank() } ||
                agreedShortCode.isNullOrEmpty()) {
            
            Timber.e("## SAS Failed to find agreement ")
            cancel(CancelCode.UnknownMethod)
            return
        }

        
        val mxDeviceInfo = cryptoStore.getUserDevice(userId = otherUserId, deviceId = otherDeviceId!!)

        if (mxDeviceInfo?.fingerprint() == null) {
            Timber.e("## SAS Failed to find device key ")
            
            
            
            cancel(CancelCode.User)
        } else {
            
            
            val accept = transport.createAccept(
                    tid = transactionId,
                    keyAgreementProtocol = agreedProtocol!!,
                    hash = agreedHash!!,
                    messageAuthenticationCode = agreedMac!!,
                    shortAuthenticationStrings = agreedShortCode,
                    commitment = Base64.encodeToString("temporary commitment".toByteArray(), Base64.DEFAULT)
            )
            doAccept(accept)
        }
    }

    private fun doAccept(accept: VerificationInfoAccept) {
        this.accepted = accept.asValidObject()
        Timber.v("## SAS incoming accept request id:$transactionId")

        
        
        val concat = getSAS().publicKey + startReq!!.canonicalJson
        accept.commitment = hashUsingAgreedHashMethod(concat) ?: ""
        
        state = VerificationTxState.SendingAccept
        sendToOther(EventType.KEY_VERIFICATION_ACCEPT, accept, VerificationTxState.Accepted, CancelCode.User) {
            if (state == VerificationTxState.SendingAccept) {
                
                state = VerificationTxState.Accepted
            }
        }
    }

    override fun onVerificationAccept(accept: ValidVerificationInfoAccept) {
        Timber.v("## SAS invalid message for incoming request id:$transactionId")
        cancel(CancelCode.UnexpectedMessage)
    }

    override fun onKeyVerificationKey(vKey: ValidVerificationInfoKey) {
        Timber.v("## SAS received key for request id:$transactionId")
        if (state != VerificationTxState.SendingAccept && state != VerificationTxState.Accepted) {
            Timber.e("## SAS received key from invalid state $state")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        otherKey = vKey.key
        
        
        
        val pubKey = getSAS().publicKey

        val keyToDevice = transport.createKey(transactionId, pubKey)
        
        state = VerificationTxState.SendingKey
        this.sendToOther(EventType.KEY_VERIFICATION_KEY, keyToDevice, VerificationTxState.KeySent, CancelCode.User) {
            if (state == VerificationTxState.SendingKey) {
                
                state = VerificationTxState.KeySent
            }
        }

        
        
        

        getSAS().setTheirPublicKey(otherKey)

        shortCodeBytes = calculateSASBytes()

        if (BuildConfig.LOG_PRIVATE_DATA) {
            Timber.v("************  BOB CODE ${getDecimalCodeRepresentation(shortCodeBytes!!)}")
            Timber.v("************  BOB EMOJI CODE ${getShortCodeRepresentation(SasMode.EMOJI)}")
        }

        state = VerificationTxState.ShortCodeReady
    }

    private fun calculateSASBytes(): ByteArray {
        when (accepted?.keyAgreementProtocol) {
            KEY_AGREEMENT_V1 -> {
                
                
                
                
                
                
                
                
                val sasInfo = "MATRIX_KEY_VERIFICATION_SAS$otherUserId$otherDeviceId$userId$deviceId$transactionId"

                
                
                return getSAS().generateShortCode(sasInfo, 6)
            }
            KEY_AGREEMENT_V2 -> {
                
                val sasInfo = "MATRIX_KEY_VERIFICATION_SAS|$otherUserId|$otherDeviceId|$otherKey|$userId|$deviceId|${getSAS().publicKey}|$transactionId"
                return getSAS().generateShortCode(sasInfo, 6)
            }
            else             -> {
                
                throw IllegalArgumentException()
            }
        }
    }

    override fun onKeyVerificationMac(vMac: ValidVerificationInfoMac) {
        Timber.v("## SAS I: received mac for request id:$transactionId")
        
        if (state != VerificationTxState.SendingKey &&
                state != VerificationTxState.KeySent &&
                state != VerificationTxState.ShortCodeReady &&
                state != VerificationTxState.ShortCodeAccepted &&
                state != VerificationTxState.SendingMac &&
                state != VerificationTxState.MacSent) {
            Timber.e("## SAS I: received key from invalid state $state")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        theirMac = vMac

        
        if (myMac != null) {
            
            verifyMacs(vMac)
        }
        
    }
}
