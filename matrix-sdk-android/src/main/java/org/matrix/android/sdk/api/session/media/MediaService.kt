

package org.matrix.android.sdk.api.session.media

import org.matrix.android.sdk.api.cache.CacheStrategy
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.JsonDict

interface MediaService {
    
    fun extractUrls(event: TimelineEvent): List<String>

    
    suspend fun getRawPreviewUrl(url: String, timestamp: Long?): JsonDict

    
    suspend fun getPreviewUrl(url: String, timestamp: Long?, cacheStrategy: CacheStrategy): PreviewUrlData

    
    suspend fun clearCache()
}
