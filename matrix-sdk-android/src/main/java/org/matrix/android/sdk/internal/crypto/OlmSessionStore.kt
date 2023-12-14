

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.internal.crypto.model.OlmSessionWrapper
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.olm.OlmSession
import timber.log.Timber
import javax.inject.Inject

private val loggerTag = LoggerTag("OlmSessionStore", LoggerTag.CRYPTO)


internal class OlmSessionStore @Inject constructor(private val store: IMXCryptoStore) {
    
    private val olmSessions = HashMap<String, MutableList<OlmSessionWrapper>>()

    
    @Synchronized
    fun storeSession(olmSessionWrapper: OlmSessionWrapper, deviceKey: String) {
        
        
        addNewSessionInCache(olmSessionWrapper, deviceKey)
        store.storeSession(olmSessionWrapper, deviceKey)
    }

    
    @Synchronized
    fun getDeviceSessionIds(deviceKey: String): List<String> {
        
        val persistedKnownSessions = store.getDeviceSessionIds(deviceKey)
                .orEmpty()
                .toMutableList()
        
        olmSessions.getOrPut(deviceKey) { mutableListOf() }.forEach { cached ->
            getSafeSessionIdentifier(cached.olmSession)?.let { cachedSessionId ->
                if (!persistedKnownSessions.contains(cachedSessionId)) {
                    persistedKnownSessions.add(cachedSessionId)
                }
            }
        }
        return persistedKnownSessions
    }

    
    @Synchronized
    fun getDeviceSession(sessionId: String, deviceKey: String): OlmSessionWrapper? {
        
        return internalGetSession(sessionId, deviceKey)
    }

    
    @Synchronized
    fun getLastUsedSessionId(deviceKey: String): String? {
        
        val lastPersistedUsedSession = store.getLastUsedSessionId(deviceKey)
        var candidate = lastPersistedUsedSession?.let { internalGetSession(it, deviceKey) }
        
        olmSessions[deviceKey].orEmpty().forEach { inCache ->
            if (inCache.lastReceivedMessageTs > (candidate?.lastReceivedMessageTs ?: 0L)) {
                candidate = inCache
            }
        }

        return candidate?.olmSession?.sessionIdentifier()
    }

    
    @Synchronized
    fun clear() {
        olmSessions.entries.onEach { entry ->
            entry.value.onEach { it.olmSession.releaseSession() }
        }
        olmSessions.clear()
    }

    private fun internalGetSession(sessionId: String, deviceKey: String): OlmSessionWrapper? {
        return getSessionInCache(sessionId, deviceKey)
                ?: 
                return store.getDeviceSession(sessionId, deviceKey)?.also {
                    addNewSessionInCache(it, deviceKey)
                }
    }

    private fun getSessionInCache(sessionId: String, deviceKey: String): OlmSessionWrapper? {
        return olmSessions[deviceKey]?.firstOrNull {
            getSafeSessionIdentifier(it.olmSession) == sessionId
        }
    }

    private fun getSafeSessionIdentifier(session: OlmSession): String? {
        return try {
            session.sessionIdentifier()
        } catch (throwable: Throwable) {
            Timber.tag(loggerTag.value).w("Failed to load sessionId from loaded olm session")
            null
        }
    }

    private fun addNewSessionInCache(session: OlmSessionWrapper, deviceKey: String) {
        val sessionId = getSafeSessionIdentifier(session.olmSession) ?: return
        olmSessions.getOrPut(deviceKey) { mutableListOf() }.let {
            val existing = it.firstOrNull { getSafeSessionIdentifier(it.olmSession) == sessionId }
            it.add(session)
            
            if (existing != null && existing.olmSession != session.olmSession) {
                
                
                it.remove(existing)
                existing.olmSession.releaseSession()
            }
        }
    }
}
