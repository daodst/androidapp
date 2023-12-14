

package im.vector.app.features.home.room.detail.timeline.url

import im.vector.app.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.cache.CacheStrategy
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLatestEventId

class PreviewUrlRetriever(session: Session,
                          private val coroutineScope: CoroutineScope) {
    private val mediaService = session.mediaService()

    private data class EventIdPreviewUrlUiState(
            
            val latestEventId: String,
            val previewUrlUiState: PreviewUrlUiState
    )

    
    private val data = mutableMapOf<String, EventIdPreviewUrlUiState>()
    private val listeners = mutableMapOf<String, MutableSet<PreviewUrlRetrieverListener>>()

    
    private val blockedUrl = mutableSetOf<String>()

    fun getPreviewUrl(event: TimelineEvent) {
        val eventId = event.root.eventId ?: return
        val latestEventId = event.getLatestEventId()

        synchronized(data) {
            val current = data[eventId]
            if (current?.latestEventId != latestEventId) {
                
                
                val url = mediaService.extractUrls(event)
                        .firstOrNull { canShowUrlPreview(it) }
                        ?.takeIf { it !in blockedUrl }
                if (url == null) {
                    updateState(eventId, latestEventId, PreviewUrlUiState.NoUrl)
                    null
                } else if (url != (current?.previewUrlUiState as? PreviewUrlUiState.Data)?.url) {
                    
                    updateState(eventId, latestEventId, PreviewUrlUiState.Loading)
                    url
                } else {
                    
                    null
                }
            } else {
                
                null
            }
        }?.let { urlToRetrieve ->
            coroutineScope.launch {
                runCatching {
                    mediaService.getPreviewUrl(
                            url = urlToRetrieve,
                            timestamp = null,
                            cacheStrategy = if (BuildConfig.DEBUG) CacheStrategy.NoCache else CacheStrategy.TtlCache(CACHE_VALIDITY, false)
                    )
                }.fold(
                        {
                            synchronized(data) {
                                
                                if (urlToRetrieve in blockedUrl) {
                                    updateState(eventId, latestEventId, PreviewUrlUiState.NoUrl)
                                } else {
                                    updateState(eventId, latestEventId, PreviewUrlUiState.Data(eventId, urlToRetrieve, it))
                                }
                            }
                        },
                        {
                            synchronized(data) {
                                updateState(eventId, latestEventId, PreviewUrlUiState.Error(it))
                            }
                        }
                )
            }
        }
    }

    private fun canShowUrlPreview(url: String): Boolean {
        return blockedDomains.all { !url.startsWith(it) }
    }

    fun doNotShowPreviewUrlFor(eventId: String, url: String) {
        blockedUrl.add(url)

        
        synchronized(data) {
            data[eventId]
                    ?.takeIf { it.previewUrlUiState is PreviewUrlUiState.Data && it.previewUrlUiState.url == url }
                    ?.let {
                        updateState(eventId, it.latestEventId, PreviewUrlUiState.NoUrl)
                    }
        }
    }

    private fun updateState(eventId: String, latestEventId: String, state: PreviewUrlUiState) {
        data[eventId] = EventIdPreviewUrlUiState(latestEventId, state)
        
        coroutineScope.launch(Dispatchers.Main) {
            listeners[eventId].orEmpty().forEach {
                it.onStateUpdated(state)
            }
        }
    }

    
    fun addListener(key: String, listener: PreviewUrlRetrieverListener) {
        listeners.getOrPut(key) { mutableSetOf() }.add(listener)

        
        synchronized(data) {
            listener.onStateUpdated(data[key]?.previewUrlUiState ?: PreviewUrlUiState.Unknown)
        }
    }

    
    fun removeListener(key: String, listener: PreviewUrlRetrieverListener) {
        listeners[key]?.remove(listener)
    }

    interface PreviewUrlRetrieverListener {
        fun onStateUpdated(state: PreviewUrlUiState)
    }

    companion object {
        
        private const val CACHE_VALIDITY = 604_800_000L 

        private val blockedDomains = listOf(
                "https://matrix.to",
                "https://app.element.io",
                "https://staging.element.io",
                "https://develop.element.io"
        )
    }
}
