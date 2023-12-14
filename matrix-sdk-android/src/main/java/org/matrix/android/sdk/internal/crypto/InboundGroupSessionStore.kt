

package org.matrix.android.sdk.internal.crypto

import android.util.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper2
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

internal data class InboundGroupSessionHolder(
        val wrapper: OlmInboundGroupSessionWrapper2,
        val mutex: Mutex = Mutex()
)

private val loggerTag = LoggerTag("InboundGroupSessionStore", LoggerTag.CRYPTO)


internal class InboundGroupSessionStore @Inject constructor(
        private val store: IMXCryptoStore,
        private val cryptoCoroutineScope: CoroutineScope,
        private val coroutineDispatchers: MatrixCoroutineDispatchers) {

    private data class CacheKey(
            val sessionId: String,
            val senderKey: String
    )

    private val sessionCache = object : LruCache<CacheKey, InboundGroupSessionHolder>(100) {
        override fun entryRemoved(evicted: Boolean, key: CacheKey?, oldValue: InboundGroupSessionHolder?, newValue: InboundGroupSessionHolder?) {
            if (oldValue != null) {
                cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
                    Timber.tag(loggerTag.value).v("## Inbound: entryRemoved ${oldValue.wrapper.roomId}-${oldValue.wrapper.senderKey}")
                    store.storeInboundGroupSessions(listOf(oldValue).map { it.wrapper })
                    oldValue.wrapper.olmInboundGroupSession?.releaseSession()
                }
            }
        }
    }

    private val timer = Timer()
    private var timerTask: TimerTask? = null

    private val dirtySession = mutableListOf<OlmInboundGroupSessionWrapper2>()

    @Synchronized
    fun clear() {
        sessionCache.evictAll()
    }

    @Synchronized
    fun getInboundGroupSession(sessionId: String, senderKey: String): InboundGroupSessionHolder? {
        val known = sessionCache[CacheKey(sessionId, senderKey)]
        Timber.tag(loggerTag.value).v("## Inbound: getInboundGroupSession  $sessionId in cache ${known != null}")
        return known
                ?: store.getInboundGroupSession(sessionId, senderKey)?.also {
                    Timber.tag(loggerTag.value).v("## Inbound: getInboundGroupSession cache populate ${it.roomId}")
                    sessionCache.put(CacheKey(sessionId, senderKey), InboundGroupSessionHolder(it))
                }?.let {
                    InboundGroupSessionHolder(it)
                }
    }

    @Synchronized
    fun replaceGroupSession(old: InboundGroupSessionHolder, new: InboundGroupSessionHolder, sessionId: String, senderKey: String) {
        Timber.tag(loggerTag.value).v("## Replacing outdated session ${old.wrapper.roomId}-${old.wrapper.senderKey}")
        dirtySession.remove(old.wrapper)
        store.removeInboundGroupSession(sessionId, senderKey)
        sessionCache.remove(CacheKey(sessionId, senderKey))

        
        old.wrapper.olmInboundGroupSession?.releaseSession()

        internalStoreGroupSession(new, sessionId, senderKey)
    }

    @Synchronized
    fun storeInBoundGroupSession(holder: InboundGroupSessionHolder, sessionId: String, senderKey: String) {
        internalStoreGroupSession(holder, sessionId, senderKey)
    }

    private fun internalStoreGroupSession(holder: InboundGroupSessionHolder, sessionId: String, senderKey: String) {
        Timber.tag(loggerTag.value).v("## Inbound: getInboundGroupSession mark as dirty ${holder.wrapper.roomId}-${holder.wrapper.senderKey}")
        
        dirtySession.add(holder.wrapper)

        if (sessionCache[CacheKey(sessionId, senderKey)] == null) {
            
            
            sessionCache.put(CacheKey(sessionId, senderKey), holder)
        }

        timerTask?.cancel()
        timerTask = object : TimerTask() {
            override fun run() {
                batchSave()
            }
        }
        timer.schedule(timerTask!!, 300)
    }

    @Synchronized
    private fun batchSave() {
        val toSave = mutableListOf<OlmInboundGroupSessionWrapper2>().apply { addAll(dirtySession) }
        dirtySession.clear()
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            Timber.tag(loggerTag.value).v("## Inbound: getInboundGroupSession batching save of ${toSave.size}")
            tryOrNull {
                store.storeInboundGroupSessions(toSave)
            }
        }
    }
}
