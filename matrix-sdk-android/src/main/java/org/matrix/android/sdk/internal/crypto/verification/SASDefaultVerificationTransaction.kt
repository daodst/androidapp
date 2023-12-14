
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.EmojiRepresentation
import org.matrix.android.sdk.api.session.crypto.verification.SasMode
import org.matrix.android.sdk.api.session.crypto.verification.SasVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.internal.crypto.IncomingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.actions.SetDeviceVerificationAction
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.extensions.toUnsignedInt
import org.matrix.olm.OlmSAS
import org.matrix.olm.OlmUtility
import timber.log.Timber
import java.util.Locale


internal abstract class SASDefaultVerificationTransaction(
        setDeviceVerificationAction: SetDeviceVerificationAction,
        open val userId: String,
        open val deviceId: String?,
        private val cryptoStore: IMXCryptoStore,
        crossSigningService: CrossSigningService,
        outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        incomingGossipingRequestManager: IncomingGossipingRequestManager,
        private val deviceFingerprint: String,
        transactionId: String,
        otherUserId: String,
        otherDeviceId: String?,
        isIncoming: Boolean
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
        SasVerificationTransaction {

    companion object {
        const val SAS_MAC_SHA256_LONGKDF = "hmac-sha256"
        const val SAS_MAC_SHA256 = "hkdf-hmac-sha256"

        
        const val KEY_AGREEMENT_V1 = "curve25519"
        const val KEY_AGREEMENT_V2 = "curve25519-hkdf-sha256"

        
        val KNOWN_AGREEMENT_PROTOCOLS = listOf(KEY_AGREEMENT_V2, KEY_AGREEMENT_V1)

        
        val KNOWN_HASHES = listOf("sha256")

        
        val KNOWN_MACS = listOf(SAS_MAC_SHA256, SAS_MAC_SHA256_LONGKDF)

        
        
        val KNOWN_SHORT_CODES = listOf(SasMode.EMOJI, SasMode.DECIMAL)
    }

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

            if (newState is VerificationTxState.TerminalTxState) {
                releaseSAS()
            }
        }

    private var olmSas: OlmSAS? = null

    
    var startReq: ValidVerificationInfoStart.SasVerificationInfoStart? = null

    
    var accepted: ValidVerificationInfoAccept? = null
    protected var otherKey: String? = null
    protected var shortCodeBytes: ByteArray? = null

    protected var myMac: ValidVerificationInfoMac? = null
    protected var theirMac: ValidVerificationInfoMac? = null

    protected fun getSAS(): OlmSAS {
        if (olmSas == null) olmSas = OlmSAS()
        return olmSas!!
    }

    
    protected fun finalize() {
        releaseSAS()
    }

    private fun releaseSAS() {
        
        olmSas?.releaseSas()
        olmSas = null
    }

    
    override fun userHasVerifiedShortCode() {
        Timber.v("## SAS short code verified by user for id:$transactionId")
        if (state != VerificationTxState.ShortCodeReady) {
            
            Timber.e("## Accepted short code from invalid state $state")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        state = VerificationTxState.ShortCodeAccepted
        
        
        
        
        
        
        
        
        
        
        val baseInfo = "MATRIX_KEY_VERIFICATION_MAC$userId$deviceId$otherUserId$otherDeviceId$transactionId"

        
        
        

        val keyMap = HashMap<String, String>()

        val keyId = "ed25519:$deviceId"
        val macString = macUsingAgreedMethod(deviceFingerprint, baseInfo + keyId)

        if (macString.isNullOrBlank()) {
            
            Timber.e("## SAS verification [$transactionId] failed to send KeyMac, empty key hashes.")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        keyMap[keyId] = macString

        cryptoStore.getMyCrossSigningInfo()?.takeIf { it.isTrusted() }
                ?.masterKey()
                ?.unpaddedBase64PublicKey
                ?.let { masterPublicKey ->
                    val crossSigningKeyId = "ed25519:$masterPublicKey"
                    macUsingAgreedMethod(masterPublicKey, baseInfo + crossSigningKeyId)?.let { mskMacString ->
                        keyMap[crossSigningKeyId] = mskMacString
                    }
                }

        val keyStrings = macUsingAgreedMethod(keyMap.keys.sorted().joinToString(","), baseInfo + "KEY_IDS")

        if (macString.isNullOrBlank() || keyStrings.isNullOrBlank()) {
            
            Timber.e("## SAS verification [$transactionId] failed to send KeyMac, empty key hashes.")
            cancel(CancelCode.UnexpectedMessage)
            return
        }

        val macMsg = transport.createMac(transactionId, keyMap, keyStrings)
        myMac = macMsg.asValidObject()
        state = VerificationTxState.SendingMac
        sendToOther(EventType.KEY_VERIFICATION_MAC, macMsg, VerificationTxState.MacSent, CancelCode.User) {
            if (state == VerificationTxState.SendingMac) {
                
                state = VerificationTxState.MacSent
            }
        }

        
        theirMac?.let { verifyMacs(it) }
        
    }

    override fun shortCodeDoesNotMatch() {
        Timber.v("## SAS short code do not match for id:$transactionId")
        cancel(CancelCode.MismatchedSas)
    }

    override fun isToDeviceTransport(): Boolean {
        return transport is VerificationTransportToDevice
    }

    abstract fun onVerificationStart(startReq: ValidVerificationInfoStart.SasVerificationInfoStart)

    abstract fun onVerificationAccept(accept: ValidVerificationInfoAccept)

    abstract fun onKeyVerificationKey(vKey: ValidVerificationInfoKey)

    abstract fun onKeyVerificationMac(vMac: ValidVerificationInfoMac)

    protected fun verifyMacs(theirMacSafe: ValidVerificationInfoMac) {
        Timber.v("## SAS verifying macs for id:$transactionId")
        state = VerificationTxState.Verifying

        
        val otherUserKnownDevices = cryptoStore.getUserDevices(otherUserId)

        
        
        
        
        val baseInfo = "MATRIX_KEY_VERIFICATION_MAC$otherUserId$otherDeviceId$userId$deviceId$transactionId"

        val commaSeparatedListOfKeyIds = theirMacSafe.mac.keys.sorted().joinToString(",")

        val keyStrings = macUsingAgreedMethod(commaSeparatedListOfKeyIds, baseInfo + "KEY_IDS")
        if (theirMacSafe.keys != keyStrings) {
            
            cancel(CancelCode.MismatchedKeys)
            return
        }

        val verifiedDevices = ArrayList<String>()

        
        theirMacSafe.mac.keys.forEach {
            val keyIDNoPrefix = it.removePrefix("ed25519:")
            val otherDeviceKey = otherUserKnownDevices?.get(keyIDNoPrefix)?.fingerprint()
            if (otherDeviceKey == null) {
                Timber.w("## SAS Verification: Could not find device $keyIDNoPrefix to verify")
                
                return@forEach
            }
            val mac = macUsingAgreedMethod(otherDeviceKey, baseInfo + it)
            if (mac != theirMacSafe.mac[it]) {
                
                Timber.e("## SAS Verification: mac mismatch for $otherDeviceKey with id $keyIDNoPrefix")
                cancel(CancelCode.MismatchedKeys)
                return
            }
            verifiedDevices.add(keyIDNoPrefix)
        }

        var otherMasterKeyIsVerified = false
        val otherMasterKey = cryptoStore.getCrossSigningInfo(otherUserId)?.masterKey()
        val otherCrossSigningMasterKeyPublic = otherMasterKey?.unpaddedBase64PublicKey
        if (otherCrossSigningMasterKeyPublic != null) {
            
            theirMacSafe.mac.keys.forEach {
                val keyIDNoPrefix = it.removePrefix("ed25519:")
                if (keyIDNoPrefix == otherCrossSigningMasterKeyPublic) {
                    
                    val mac = macUsingAgreedMethod(otherCrossSigningMasterKeyPublic, baseInfo + it)
                    if (mac != theirMacSafe.mac[it]) {
                        
                        Timber.e("## SAS Verification: mac mismatch for MasterKey with id $keyIDNoPrefix")
                        cancel(CancelCode.MismatchedKeys)
                        return
                    } else {
                        otherMasterKeyIsVerified = true
                    }
                }
            }
        }

        
        
        if (verifiedDevices.isEmpty() && !otherMasterKeyIsVerified) {
            Timber.e("## SAS Verification: No devices verified")
            cancel(CancelCode.MismatchedKeys)
            return
        }

        trust(otherMasterKeyIsVerified,
                verifiedDevices,
                eventuallyMarkMyMasterKeyAsTrusted = otherMasterKey?.trustLevel?.isVerified() == false)
    }

    override fun cancel() {
        cancel(CancelCode.User)
    }

    override fun cancel(code: CancelCode) {
        state = VerificationTxState.Cancelled(code, true)
        transport.cancelTransaction(transactionId, otherUserId, otherDeviceId ?: "", code)
    }

    protected fun <T> sendToOther(type: String,
                                  keyToDevice: VerificationInfo<T>,
                                  nextState: VerificationTxState,
                                  onErrorReason: CancelCode,
                                  onDone: (() -> Unit)?) {
        transport.sendToOther(type, keyToDevice, nextState, onErrorReason, onDone)
    }

    fun getShortCodeRepresentation(shortAuthenticationStringMode: String): String? {
        if (shortCodeBytes == null) {
            return null
        }
        when (shortAuthenticationStringMode) {
            SasMode.DECIMAL -> {
                if (shortCodeBytes!!.size < 5) return null
                return getDecimalCodeRepresentation(shortCodeBytes!!)
            }
            SasMode.EMOJI   -> {
                if (shortCodeBytes!!.size < 6) return null
                return getEmojiCodeRepresentation(shortCodeBytes!!).joinToString(" ") { it.emoji }
            }
            else            -> return null
        }
    }

    override fun supportsEmoji(): Boolean {
        return accepted?.shortAuthenticationStrings?.contains(SasMode.EMOJI).orFalse()
    }

    override fun supportsDecimal(): Boolean {
        return accepted?.shortAuthenticationStrings?.contains(SasMode.DECIMAL).orFalse()
    }

    protected fun hashUsingAgreedHashMethod(toHash: String): String? {
        if ("sha256" == accepted?.hash?.lowercase(Locale.ROOT)) {
            val olmUtil = OlmUtility()
            val hashBytes = olmUtil.sha256(toHash)
            olmUtil.releaseUtility()
            return hashBytes
        }
        return null
    }

    private fun macUsingAgreedMethod(message: String, info: String): String? {
        return when (accepted?.messageAuthenticationCode?.lowercase(Locale.ROOT)) {
            SAS_MAC_SHA256_LONGKDF -> getSAS().calculateMacLongKdf(message, info)
            SAS_MAC_SHA256         -> getSAS().calculateMac(message, info)
            else                   -> null
        }
    }

    override fun getDecimalCodeRepresentation(): String {
        return getDecimalCodeRepresentation(shortCodeBytes!!)
    }

    
    fun getDecimalCodeRepresentation(byteArray: ByteArray): String {
        val b0 = byteArray[0].toUnsignedInt() 
        val b1 = byteArray[1].toUnsignedInt() 
        val b2 = byteArray[2].toUnsignedInt() 
        val b3 = byteArray[3].toUnsignedInt() 
        val b4 = byteArray[4].toUnsignedInt() 
        
        val first = (b0.shl(5) or b1.shr(3)) + 1000
        
        val second = ((b1 and 0x7).shl(10) or b2.shl(2) or b3.shr(6)) + 1000
        
        val third = ((b3 and 0x3f).shl(7) or b4.shr(1)) + 1000
        return "$first $second $third"
    }

    override fun getEmojiCodeRepresentation(): List<EmojiRepresentation> {
        return getEmojiCodeRepresentation(shortCodeBytes!!)
    }

    
    private fun getEmojiCodeRepresentation(byteArray: ByteArray): List<EmojiRepresentation> {
        val b0 = byteArray[0].toUnsignedInt()
        val b1 = byteArray[1].toUnsignedInt()
        val b2 = byteArray[2].toUnsignedInt()
        val b3 = byteArray[3].toUnsignedInt()
        val b4 = byteArray[4].toUnsignedInt()
        val b5 = byteArray[5].toUnsignedInt()
        return listOf(
                getEmojiForCode((b0 and 0xFC).shr(2)),
                getEmojiForCode((b0 and 0x3).shl(4) or (b1 and 0xF0).shr(4)),
                getEmojiForCode((b1 and 0xF).shl(2) or (b2 and 0xC0).shr(6)),
                getEmojiForCode((b2 and 0x3F)),
                getEmojiForCode((b3 and 0xFC).shr(2)),
                getEmojiForCode((b3 and 0x3).shl(4) or (b4 and 0xF0).shr(4)),
                getEmojiForCode((b4 and 0xF).shl(2) or (b5 and 0xC0).shr(6))
        )
    }
}
