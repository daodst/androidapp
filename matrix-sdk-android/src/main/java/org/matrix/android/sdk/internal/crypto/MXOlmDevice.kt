

package org.matrix.android.sdk.internal.crypto

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.util.JSON_DICT_PARAMETERIZED_TYPE
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.crypto.algorithms.megolm.MXOutboundSessionInfo
import org.matrix.android.sdk.internal.crypto.algorithms.megolm.SharedWithHelper
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper2
import org.matrix.android.sdk.internal.crypto.model.OlmSessionWrapper
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.android.sdk.internal.util.convertFromUTF8
import org.matrix.android.sdk.internal.util.convertToUTF8
import org.matrix.olm.OlmAccount
import org.matrix.olm.OlmException
import org.matrix.olm.OlmMessage
import org.matrix.olm.OlmOutboundGroupSession
import org.matrix.olm.OlmSession
import org.matrix.olm.OlmUtility
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject

private val loggerTag = LoggerTag("MXOlmDevice", LoggerTag.CRYPTO)

@SessionScope
internal class MXOlmDevice @Inject constructor(
        
        private val store: IMXCryptoStore,
        private val olmSessionStore: OlmSessionStore,
        private val inboundGroupSessionStore: InboundGroupSessionStore
) {

    val mutex = Mutex()

    
    var deviceCurve25519Key: String? = null
        private set

    
    var deviceEd25519Key: String? = null
        private set

    
    private var olmUtility: OlmUtility? = null

    private data class GroupSessionCacheItem(
            val groupId: String,
            val groupSession: OlmOutboundGroupSession
    )

    
    
    
    private val outboundGroupSessionCache: MutableMap<String, GroupSessionCacheItem> = HashMap()

    
    
    
    
    
    
    
    
    
    
    
    private val inboundGroupSessionMessageIndexes: MutableMap<String, MutableSet<String>> = HashMap()

    init {
        
        try {
            store.getOrCreateOlmAccount()
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "MXOlmDevice : cannot initialize olmAccount")
        }

        try {
            olmUtility = OlmUtility()
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## MXOlmDevice : OlmUtility failed with error")
            olmUtility = null
        }

        try {
            deviceCurve25519Key = store.doWithOlmAccount { it.identityKeys()[OlmAccount.JSON_KEY_IDENTITY_KEY] }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## MXOlmDevice : cannot find ${OlmAccount.JSON_KEY_IDENTITY_KEY} with error")
        }

        try {
            deviceEd25519Key = store.doWithOlmAccount { it.identityKeys()[OlmAccount.JSON_KEY_FINGER_PRINT_KEY] }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## MXOlmDevice : cannot find ${OlmAccount.JSON_KEY_FINGER_PRINT_KEY} with error")
        }
    }

    
    fun getOneTimeKeys(): Map<String, Map<String, String>>? {
        try {
            return store.doWithOlmAccount { it.oneTimeKeys() }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## getOneTimeKeys() : failed")
        }

        return null
    }

    
    fun getMaxNumberOfOneTimeKeys(): Long {
        return store.doWithOlmAccount { it.maxOneTimeKeys() }
    }

    
    fun getFallbackKey(): MutableMap<String, MutableMap<String, String>>? {
        try {
            return store.doWithOlmAccount { it.fallbackKey() }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e("## getFallbackKey() : failed")
        }
        return null
    }

    
    fun generateFallbackKeyIfNeeded(): Boolean {
        try {
            if (!hasUnpublishedFallbackKey()) {
                store.doWithOlmAccount {
                    it.generateFallbackKey()
                    store.saveOlmAccount()
                }
                return true
            }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e("## generateFallbackKey() : failed")
        }
        return false
    }

    internal fun hasUnpublishedFallbackKey(): Boolean {
        return getFallbackKey()?.get(OlmAccount.JSON_KEY_ONE_TIME_KEY).orEmpty().isNotEmpty()
    }

    fun forgetFallbackKey() {
        try {
            store.doWithOlmAccount {
                it.forgetFallbackKey()
                store.saveOlmAccount()
            }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e("## forgetFallbackKey() : failed")
        }
    }

    
    fun release() {
        olmUtility?.releaseUtility()
        outboundGroupSessionCache.values.forEach {
            it.groupSession.releaseSession()
        }
        outboundGroupSessionCache.clear()
        inboundGroupSessionStore.clear()
        olmSessionStore.clear()
    }

    
    fun signMessage(message: String): String? {
        try {
            return store.doWithOlmAccount { it.signMessage(message) }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## signMessage() : failed")
        }

        return null
    }

    
    fun markKeysAsPublished() {
        try {
            store.doWithOlmAccount {
                it.markOneTimeKeysAsPublished()
                store.saveOlmAccount()
            }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## markKeysAsPublished() : failed")
        }
    }

    
    fun generateOneTimeKeys(numKeys: Int) {
        try {
            store.doWithOlmAccount {
                it.generateOneTimeKeys(numKeys)
                store.saveOlmAccount()
            }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## generateOneTimeKeys() : failed")
        }
    }

    
    fun createOutboundSession(theirIdentityKey: String, theirOneTimeKey: String): String? {
        Timber.tag(loggerTag.value).d("## createOutboundSession() ; theirIdentityKey $theirIdentityKey theirOneTimeKey $theirOneTimeKey")
        var olmSession: OlmSession? = null

        try {
            olmSession = OlmSession()
            store.doWithOlmAccount { olmAccount ->
                olmSession.initOutboundSession(olmAccount, theirIdentityKey, theirOneTimeKey)
            }

            val olmSessionWrapper = OlmSessionWrapper(olmSession, 0)

            
            
            
            olmSessionWrapper.onMessageReceived()

            olmSessionStore.storeSession(olmSessionWrapper, theirIdentityKey)

            val sessionIdentifier = olmSession.sessionIdentifier()

            Timber.tag(loggerTag.value).v("## createOutboundSession() ;  olmSession.sessionIdentifier: $sessionIdentifier")
            return sessionIdentifier
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## createOutboundSession() failed")

            olmSession?.releaseSession()
        }

        return null
    }

    
    fun createInboundSession(theirDeviceIdentityKey: String, messageType: Int, ciphertext: String): Map<String, String>? {
        Timber.tag(loggerTag.value).d("## createInboundSession() : theirIdentityKey: $theirDeviceIdentityKey")

        var olmSession: OlmSession? = null

        try {
            try {
                olmSession = OlmSession()
                store.doWithOlmAccount { olmAccount ->
                    olmSession.initInboundSessionFrom(olmAccount, theirDeviceIdentityKey, ciphertext)
                }
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## createInboundSession() : the session creation failed")
                return null
            }

            Timber.tag(loggerTag.value).v("## createInboundSession() : sessionId: ${olmSession.sessionIdentifier()}")

            try {
                store.doWithOlmAccount { olmAccount ->
                    olmAccount.removeOneTimeKeys(olmSession)
                    store.saveOlmAccount()
                }
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## createInboundSession() : removeOneTimeKeys failed")
            }

            Timber.tag(loggerTag.value).v("## createInboundSession() : ciphertext: $ciphertext")
            try {
                val sha256 = olmUtility!!.sha256(URLEncoder.encode(ciphertext, "utf-8"))
                Timber.tag(loggerTag.value).v("## createInboundSession() :ciphertext: SHA256: $sha256")
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## createInboundSession() :ciphertext: cannot encode ciphertext")
            }

            val olmMessage = OlmMessage()
            olmMessage.mCipherText = ciphertext
            olmMessage.mType = messageType.toLong()

            var payloadString: String? = null

            try {
                payloadString = olmSession.decryptMessage(olmMessage)

                val olmSessionWrapper = OlmSessionWrapper(olmSession, 0)
                
                olmSessionWrapper.onMessageReceived()

                olmSessionStore.storeSession(olmSessionWrapper, theirDeviceIdentityKey)
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## createInboundSession() : decryptMessage failed")
            }

            val res = HashMap<String, String>()

            if (!payloadString.isNullOrEmpty()) {
                res["payload"] = payloadString
            }

            val sessionIdentifier = olmSession.sessionIdentifier()

            if (!sessionIdentifier.isNullOrEmpty()) {
                res["session_id"] = sessionIdentifier
            }

            return res
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## createInboundSession() : OlmSession creation failed")

            olmSession?.releaseSession()
        }

        return null
    }

    
    fun getSessionIds(theirDeviceIdentityKey: String): List<String> {
        return olmSessionStore.getDeviceSessionIds(theirDeviceIdentityKey)
    }

    
    fun getSessionId(theirDeviceIdentityKey: String): String? {
        return olmSessionStore.getLastUsedSessionId(theirDeviceIdentityKey)
    }

    
    suspend fun encryptMessage(theirDeviceIdentityKey: String, sessionId: String, payloadString: String): Map<String, Any>? {
        val olmSessionWrapper = getSessionForDevice(theirDeviceIdentityKey, sessionId)

        if (olmSessionWrapper != null) {
            try {
                Timber.tag(loggerTag.value).v("## encryptMessage() : olmSession.sessionIdentifier: $sessionId")

                val olmMessage = olmSessionWrapper.mutex.withLock {
                    olmSessionWrapper.olmSession.encryptMessage(payloadString)
                }
                return mapOf(
                        "body" to olmMessage.mCipherText,
                        "type" to olmMessage.mType,
                ).also {
                    olmSessionStore.storeSession(olmSessionWrapper, theirDeviceIdentityKey)
                }
            } catch (e: Throwable) {
                Timber.tag(loggerTag.value).e(e, "## encryptMessage() : failed to encrypt olm with device|session:$theirDeviceIdentityKey|$sessionId")
                return null
            }
        } else {
            Timber.tag(loggerTag.value).e("## encryptMessage() : Failed to encrypt unknown session $sessionId")
            return null
        }
    }

    
    @kotlin.jvm.Throws
    suspend fun decryptMessage(ciphertext: String, messageType: Int, sessionId: String, theirDeviceIdentityKey: String): String? {
        var payloadString: String? = null

        val olmSessionWrapper = getSessionForDevice(theirDeviceIdentityKey, sessionId)

        if (null != olmSessionWrapper) {
            val olmMessage = OlmMessage()
            olmMessage.mCipherText = ciphertext
            olmMessage.mType = messageType.toLong()

            payloadString =
                    olmSessionWrapper.mutex.withLock {
                        olmSessionWrapper.olmSession.decryptMessage(olmMessage).also {
                            olmSessionWrapper.onMessageReceived()
                        }
                    }
            olmSessionStore.storeSession(olmSessionWrapper, theirDeviceIdentityKey)
        }

        return payloadString
    }

    
    fun matchesSession(theirDeviceIdentityKey: String, sessionId: String, messageType: Int, ciphertext: String): Boolean {
        if (messageType != 0) {
            return false
        }

        val olmSessionWrapper = getSessionForDevice(theirDeviceIdentityKey, sessionId)
        return null != olmSessionWrapper && olmSessionWrapper.olmSession.matchesInboundSession(ciphertext)
    }

    

    
    fun createOutboundGroupSessionForRoom(roomId: String): String? {
        var session: OlmOutboundGroupSession? = null
        try {
            session = OlmOutboundGroupSession()
            outboundGroupSessionCache[session.sessionIdentifier()] = GroupSessionCacheItem(roomId, session)
            store.storeCurrentOutboundGroupSessionForRoom(roomId, session)
            return session.sessionIdentifier()
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "createOutboundGroupSession")

            session?.releaseSession()
        }

        return null
    }

    fun storeOutboundGroupSessionForRoom(roomId: String, sessionId: String) {
        outboundGroupSessionCache[sessionId]?.let {
            store.storeCurrentOutboundGroupSessionForRoom(roomId, it.groupSession)
        }
    }

    fun restoreOutboundGroupSessionForRoom(roomId: String): MXOutboundSessionInfo? {
        val restoredOutboundGroupSession = store.getCurrentOutboundGroupSessionForRoom(roomId)
        if (restoredOutboundGroupSession != null) {
            val sessionId = restoredOutboundGroupSession.outboundGroupSession.sessionIdentifier()
            
            outboundGroupSessionCache[sessionId] = GroupSessionCacheItem(roomId, restoredOutboundGroupSession.outboundGroupSession)

            return MXOutboundSessionInfo(
                    sessionId = sessionId,
                    sharedWithHelper = SharedWithHelper(roomId, sessionId, store),
                    restoredOutboundGroupSession.creationTime
            )
        }
        return null
    }

    fun discardOutboundGroupSessionForRoom(roomId: String) {
        val toDiscard = outboundGroupSessionCache.filter {
            it.value.groupId == roomId
        }
        toDiscard.forEach { (sessionId, cacheItem) ->
            cacheItem.groupSession.releaseSession()
            outboundGroupSessionCache.remove(sessionId)
        }
        store.storeCurrentOutboundGroupSessionForRoom(roomId, null)
    }

    
    fun getSessionKey(sessionId: String): String? {
        if (sessionId.isNotEmpty()) {
            try {
                return outboundGroupSessionCache[sessionId]!!.groupSession.sessionKey()
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## getSessionKey() : failed")
            }
        }
        return null
    }

    
    fun getMessageIndex(sessionId: String): Int {
        return if (sessionId.isNotEmpty()) {
            outboundGroupSessionCache[sessionId]!!.groupSession.messageIndex()
        } else 0
    }

    
    fun encryptGroupMessage(sessionId: String, payloadString: String): String? {
        if (sessionId.isNotEmpty() && payloadString.isNotEmpty()) {
            try {
                return outboundGroupSessionCache[sessionId]!!.groupSession.encryptMessage(payloadString)
            } catch (e: Throwable) {
                Timber.tag(loggerTag.value).e(e, "## encryptGroupMessage() : failed")
            }
        }
        return null
    }

    

    
    fun addInboundGroupSession(sessionId: String,
                               sessionKey: String,
                               roomId: String,
                               senderKey: String,
                               forwardingCurve25519KeyChain: List<String>,
                               keysClaimed: Map<String, String>,
                               exportFormat: Boolean): Boolean {
        val candidateSession = OlmInboundGroupSessionWrapper2(sessionKey, exportFormat)
        val existingSessionHolder = tryOrNull { getInboundGroupSession(sessionId, senderKey, roomId) }
        val existingSession = existingSessionHolder?.wrapper
        
        if (existingSession != null) {
            Timber.tag(loggerTag.value).d("## addInboundGroupSession() check if known session is better than candidate session")
            try {
                val existingFirstKnown = existingSession.firstKnownIndex ?: return false.also {
                    
                    Timber.tag(loggerTag.value).e("## addInboundGroupSession() null firstKnownIndex on existing session")
                    candidateSession.olmInboundGroupSession?.releaseSession()
                    
                }
                val newKnownFirstIndex = candidateSession.firstKnownIndex
                
                if (newKnownFirstIndex != null && existingFirstKnown <= newKnownFirstIndex) {
                    Timber.tag(loggerTag.value).d("## addInboundGroupSession() : ignore session our is better $senderKey/$sessionId")
                    candidateSession.olmInboundGroupSession?.releaseSession()
                    return false
                }
            } catch (failure: Throwable) {
                Timber.tag(loggerTag.value).e("## addInboundGroupSession() Failed to add inbound: ${failure.localizedMessage}")
                candidateSession.olmInboundGroupSession?.releaseSession()
                return false
            }
        }

        Timber.tag(loggerTag.value).d("## addInboundGroupSession() : Candidate session should be added $senderKey/$sessionId")

        
        val candidateOlmInboundSession = candidateSession.olmInboundGroupSession
        if (null == candidateOlmInboundSession) {
            Timber.tag(loggerTag.value).e("## addInboundGroupSession : invalid session <null>")
            return false
        }

        try {
            if (candidateOlmInboundSession.sessionIdentifier() != sessionId) {
                Timber.tag(loggerTag.value).e("## addInboundGroupSession : ERROR: Mismatched group session ID from senderKey: $senderKey")
                candidateOlmInboundSession.releaseSession()
                return false
            }
        } catch (e: Throwable) {
            candidateOlmInboundSession.releaseSession()
            Timber.tag(loggerTag.value).e(e, "## addInboundGroupSession : sessionIdentifier() failed")
            return false
        }

        candidateSession.senderKey = senderKey
        candidateSession.roomId = roomId
        candidateSession.keysClaimed = keysClaimed
        candidateSession.forwardingCurve25519KeyChain = forwardingCurve25519KeyChain

        if (existingSession != null) {
            inboundGroupSessionStore.replaceGroupSession(existingSessionHolder, InboundGroupSessionHolder(candidateSession), sessionId, senderKey)
        } else {
            inboundGroupSessionStore.storeInBoundGroupSession(InboundGroupSessionHolder(candidateSession), sessionId, senderKey)
        }

        return true
    }

    
    fun importInboundGroupSessions(megolmSessionsData: List<MegolmSessionData>): List<OlmInboundGroupSessionWrapper2> {
        val sessions = ArrayList<OlmInboundGroupSessionWrapper2>(megolmSessionsData.size)

        for (megolmSessionData in megolmSessionsData) {
            val sessionId = megolmSessionData.sessionId ?: continue
            val senderKey = megolmSessionData.senderKey ?: continue
            val roomId = megolmSessionData.roomId

            var candidateSessionToImport: OlmInboundGroupSessionWrapper2? = null

            try {
                candidateSessionToImport = OlmInboundGroupSessionWrapper2(megolmSessionData)
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## importInboundGroupSession() : Update for megolm session $senderKey/$sessionId")
            }

            
            if (candidateSessionToImport?.olmInboundGroupSession == null) {
                Timber.tag(loggerTag.value).e("## importInboundGroupSession : invalid session")
                continue
            }

            val candidateOlmInboundGroupSession = candidateSessionToImport.olmInboundGroupSession
            try {
                if (candidateOlmInboundGroupSession?.sessionIdentifier() != sessionId) {
                    Timber.tag(loggerTag.value).e("## importInboundGroupSession : ERROR: Mismatched group session ID from senderKey: $senderKey")
                    candidateOlmInboundGroupSession?.releaseSession()
                    continue
                }
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## importInboundGroupSession : sessionIdentifier() failed")
                candidateOlmInboundGroupSession?.releaseSession()
                continue
            }

            val existingSessionHolder = tryOrNull { getInboundGroupSession(sessionId, senderKey, roomId) }
            val existingSession = existingSessionHolder?.wrapper

            if (existingSession == null) {
                
                Timber.tag(loggerTag.value).d("## importInboundGroupSession() : importing new megolm session $senderKey/$sessionId")
                sessions.add(candidateSessionToImport)
            } else {
                Timber.tag(loggerTag.value).e("## importInboundGroupSession() : Update for megolm session $senderKey/$sessionId")
                val existingFirstKnown = tryOrNull { existingSession.firstKnownIndex }
                val candidateFirstKnownIndex = tryOrNull { candidateSessionToImport.firstKnownIndex }

                if (existingFirstKnown == null || candidateFirstKnownIndex == null) {
                    
                    candidateSessionToImport.olmInboundGroupSession?.releaseSession()
                    Timber.tag(loggerTag.value)
                            .w("## importInboundGroupSession() : Can't check session null index $existingFirstKnown/$candidateFirstKnownIndex")
                } else {
                    if (existingFirstKnown <= candidateSessionToImport.firstKnownIndex!!) {
                        
                        candidateOlmInboundGroupSession.releaseSession()
                    } else {
                        
                        inboundGroupSessionStore.replaceGroupSession(
                                existingSessionHolder,
                                InboundGroupSessionHolder(candidateSessionToImport),
                                sessionId,
                                senderKey
                        )
                        sessions.add(candidateSessionToImport)
                    }
                }
            }
        }

        store.storeInboundGroupSessions(sessions)

        return sessions
    }

    
    @Throws(MXCryptoError::class)
    suspend fun decryptGroupMessage(body: String,
                                    roomId: String,
                                    timeline: String?,
                                    sessionId: String,
                                    senderKey: String): OlmDecryptionResult {
        val sessionHolder = getInboundGroupSession(sessionId, senderKey, roomId)
        val wrapper = sessionHolder.wrapper
        val inboundGroupSession = wrapper.olmInboundGroupSession
                ?: throw MXCryptoError.Base(MXCryptoError.ErrorType.UNABLE_TO_DECRYPT, "Session is null")
        
        
        if (roomId == wrapper.roomId) {
            val decryptResult = try {
                sessionHolder.mutex.withLock {
                    inboundGroupSession.decryptMessage(body)
                }
            } catch (e: OlmException) {
                Timber.tag(loggerTag.value).e(e, "## decryptGroupMessage () : decryptMessage failed")
                throw MXCryptoError.OlmError(e)
            }

            if (timeline?.isNotBlank() == true) {
                val timelineSet = inboundGroupSessionMessageIndexes.getOrPut(timeline) { mutableSetOf() }

                val messageIndexKey = senderKey + "|" + sessionId + "|" + decryptResult.mIndex

                if (timelineSet.contains(messageIndexKey)) {
                    val reason = String.format(MXCryptoError.DUPLICATE_MESSAGE_INDEX_REASON, decryptResult.mIndex)
                    Timber.tag(loggerTag.value).e("## decryptGroupMessage() : $reason")
                    throw MXCryptoError.Base(MXCryptoError.ErrorType.DUPLICATED_MESSAGE_INDEX, reason)
                }

                timelineSet.add(messageIndexKey)
            }

            inboundGroupSessionStore.storeInBoundGroupSession(sessionHolder, sessionId, senderKey)
            val payload = try {
                val adapter = MoshiProvider.providesMoshi().adapter<JsonDict>(JSON_DICT_PARAMETERIZED_TYPE)
                val payloadString = convertFromUTF8(decryptResult.mDecryptedMessage)
                adapter.fromJson(payloadString)
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e("## decryptGroupMessage() : fails to parse the payload")
                throw MXCryptoError.Base(MXCryptoError.ErrorType.BAD_DECRYPTED_FORMAT, MXCryptoError.BAD_DECRYPTED_FORMAT_TEXT_REASON)
            }

            return OlmDecryptionResult(
                    payload,
                    wrapper.keysClaimed,
                    senderKey,
                    wrapper.forwardingCurve25519KeyChain
            )
        } else {
            val reason = String.format(MXCryptoError.INBOUND_SESSION_MISMATCH_ROOM_ID_REASON, roomId, wrapper.roomId)
            Timber.tag(loggerTag.value).e("## decryptGroupMessage() : $reason")
            throw MXCryptoError.Base(MXCryptoError.ErrorType.INBOUND_SESSION_MISMATCH_ROOM_ID, reason)
        }
    }

    
    fun resetReplayAttackCheckInTimeline(timeline: String?) {
        if (null != timeline) {
            inboundGroupSessionMessageIndexes.remove(timeline)
        }
    }


    
    @Throws(Exception::class)
    fun verifySignature(key: String, jsonDictionary: Map<String, Any>, signature: String) {
        
        olmUtility!!.verifyEd25519Signature(signature, key, JsonCanonicalizer.getCanonicalJson(Map::class.java, jsonDictionary))
    }

    
    fun sha256(message: String): String {
        return olmUtility!!.sha256(convertToUTF8(message))
    }

    
    private fun getSessionForDevice(theirDeviceIdentityKey: String, sessionId: String): OlmSessionWrapper? {
        
        return if (theirDeviceIdentityKey.isEmpty() || sessionId.isEmpty()) null else {
            olmSessionStore.getDeviceSession(sessionId, theirDeviceIdentityKey)
        }
    }

    
    fun getInboundGroupSession(sessionId: String?, senderKey: String?, roomId: String?): InboundGroupSessionHolder {
        if (sessionId.isNullOrBlank() || senderKey.isNullOrBlank()) {
            throw MXCryptoError.Base(MXCryptoError.ErrorType.MISSING_SENDER_KEY, MXCryptoError.ERROR_MISSING_PROPERTY_REASON)
        }

        val holder = inboundGroupSessionStore.getInboundGroupSession(sessionId, senderKey)
        val session = holder?.wrapper

        if (session != null) {
            
            
            if (roomId != session.roomId) {
                val errorDescription = String.format(MXCryptoError.INBOUND_SESSION_MISMATCH_ROOM_ID_REASON, roomId, session.roomId)
                Timber.tag(loggerTag.value).e("## getInboundGroupSession() : $errorDescription")
                throw MXCryptoError.Base(MXCryptoError.ErrorType.INBOUND_SESSION_MISMATCH_ROOM_ID, errorDescription)
            } else {
                return holder
            }
        } else {
            Timber.tag(loggerTag.value).w("## getInboundGroupSession() : UISI $sessionId")
            throw MXCryptoError.Base(MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID, MXCryptoError.UNKNOWN_INBOUND_SESSION_ID_REASON)
        }
    }

    
    fun hasInboundSessionKeys(roomId: String, senderKey: String, sessionId: String): Boolean {
        return runCatching { getInboundGroupSession(sessionId, senderKey, roomId) }.isSuccess
    }

    @VisibleForTesting
    fun clearOlmSessionCache() {
        olmSessionStore.clear()
    }
}
