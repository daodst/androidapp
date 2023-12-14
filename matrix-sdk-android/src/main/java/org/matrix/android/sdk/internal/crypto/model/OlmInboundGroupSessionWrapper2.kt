

package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.internal.crypto.MegolmSessionData
import org.matrix.olm.OlmInboundGroupSession
import timber.log.Timber
import java.io.Serializable


internal class OlmInboundGroupSessionWrapper2 : Serializable {

    
    var olmInboundGroupSession: OlmInboundGroupSession? = null

    
    var roomId: String? = null

    
    var senderKey: String? = null

    
    var keysClaimed: Map<String, String>? = null

    
    var forwardingCurve25519KeyChain: List<String>? = ArrayList()

    
    val firstKnownIndex: Long?
        get() {
            return try {
                olmInboundGroupSession?.firstKnownIndex
            } catch (e: Exception) {
                Timber.e(e, "## getFirstKnownIndex() : getFirstKnownIndex failed")
                null
            }
        }

    
    constructor(sessionKey: String, isImported: Boolean) {
        try {
            if (!isImported) {
                olmInboundGroupSession = OlmInboundGroupSession(sessionKey)
            } else {
                olmInboundGroupSession = OlmInboundGroupSession.importSession(sessionKey)
            }
        } catch (e: Exception) {
            Timber.e(e, "Cannot create")
        }
    }

    constructor() {
        
    }

    
    @Throws(Exception::class)
    constructor(megolmSessionData: MegolmSessionData) {
        try {
            val safeSessionKey = megolmSessionData.sessionKey ?: throw Exception("invalid data")
            olmInboundGroupSession = OlmInboundGroupSession.importSession(safeSessionKey)
                    .also {
                        if (it.sessionIdentifier() != megolmSessionData.sessionId) {
                            throw Exception("Mismatched group session Id")
                        }
                    }

            senderKey = megolmSessionData.senderKey
            keysClaimed = megolmSessionData.senderClaimedKeys
            roomId = megolmSessionData.roomId
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    
    fun exportKeys(index: Long? = null): MegolmSessionData? {
        return try {
            if (null == forwardingCurve25519KeyChain) {
                forwardingCurve25519KeyChain = ArrayList()
            }

            if (keysClaimed == null) {
                return null
            }

            val safeOlmInboundGroupSession = olmInboundGroupSession ?: return null

            val wantedIndex = index ?: safeOlmInboundGroupSession.firstKnownIndex

            MegolmSessionData(
                    senderClaimedEd25519Key = keysClaimed?.get("ed25519"),
                    forwardingCurve25519KeyChain = forwardingCurve25519KeyChain?.toList().orEmpty(),
                    senderKey = senderKey,
                    senderClaimedKeys = keysClaimed,
                    roomId = roomId,
                    sessionId = safeOlmInboundGroupSession.sessionIdentifier(),
                    sessionKey = safeOlmInboundGroupSession.export(wantedIndex),
                    algorithm = MXCRYPTO_ALGORITHM_MEGOLM
            )
        } catch (e: Exception) {
            Timber.e(e, "## export() : senderKey $senderKey failed")
            null
        }
    }

    
    fun exportSession(messageIndex: Long): String? {
        return try {
            return olmInboundGroupSession?.export(messageIndex)
        } catch (e: Exception) {
            Timber.e(e, "## exportSession() : export failed")
            null
        }
    }
}
