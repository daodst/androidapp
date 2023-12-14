

package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.internal.crypto.MegolmSessionData
import org.matrix.olm.OlmInboundGroupSession
import timber.log.Timber
import java.io.Serializable


internal class OlmInboundGroupSessionWrapper : Serializable {

    
    var olmInboundGroupSession: OlmInboundGroupSession? = null

    
    var roomId: String? = null

    
    var senderKey: String? = null

    
    var keysClaimed: Map<String, String>? = null

    
    var forwardingCurve25519KeyChain: List<String>? = ArrayList()

    
    val firstKnownIndex: Long?
        get() {
            if (null != olmInboundGroupSession) {
                try {
                    return olmInboundGroupSession!!.firstKnownIndex
                } catch (e: Exception) {
                    Timber.e(e, "## getFirstKnownIndex() : getFirstKnownIndex failed")
                }
            }

            return null
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

    
    @Throws(Exception::class)
    constructor(megolmSessionData: MegolmSessionData) {
        try {
            olmInboundGroupSession = OlmInboundGroupSession.importSession(megolmSessionData.sessionKey!!)

            if (olmInboundGroupSession!!.sessionIdentifier() != megolmSessionData.sessionId) {
                throw Exception("Mismatched group session Id")
            }

            senderKey = megolmSessionData.senderKey
            keysClaimed = megolmSessionData.senderClaimedKeys
            roomId = megolmSessionData.roomId
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    
    fun exportKeys(): MegolmSessionData? {
        return try {
            if (null == forwardingCurve25519KeyChain) {
                forwardingCurve25519KeyChain = ArrayList()
            }

            if (keysClaimed == null) {
                return null
            }

            MegolmSessionData(
                    senderClaimedEd25519Key = keysClaimed?.get("ed25519"),
                    forwardingCurve25519KeyChain = ArrayList(forwardingCurve25519KeyChain!!),
                    senderKey = senderKey,
                    senderClaimedKeys = keysClaimed,
                    roomId = roomId,
                    sessionId = olmInboundGroupSession!!.sessionIdentifier(),
                    sessionKey = olmInboundGroupSession!!.export(olmInboundGroupSession!!.firstKnownIndex),
                    algorithm = MXCRYPTO_ALGORITHM_MEGOLM
            )
        } catch (e: Exception) {
            Timber.e(e, "## export() : senderKey $senderKey failed")
            null
        }
    }

    
    fun exportSession(messageIndex: Long): String? {
        if (null != olmInboundGroupSession) {
            try {
                return olmInboundGroupSession!!.export(messageIndex)
            } catch (e: Exception) {
                Timber.e(e, "## exportSession() : export failed")
            }
        }

        return null
    }
}
