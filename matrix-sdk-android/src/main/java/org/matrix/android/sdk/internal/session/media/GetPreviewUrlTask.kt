

package org.matrix.android.sdk.internal.session.media

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.cache.CacheStrategy
import org.matrix.android.sdk.api.session.media.PreviewUrlData
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntity
import org.matrix.android.sdk.internal.database.query.get
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import org.matrix.android.sdk.internal.util.unescapeHtml
import java.util.Date
import javax.inject.Inject

internal interface GetPreviewUrlTask : Task<GetPreviewUrlTask.Params, PreviewUrlData> {
    data class Params(
            val url: String,
            val timestamp: Long?,
            val cacheStrategy: CacheStrategy
    )
}

internal class DefaultGetPreviewUrlTask @Inject constructor(
        private val mediaAPI: MediaAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        @SessionDatabase private val monarchy: Monarchy
) : GetPreviewUrlTask {

    override suspend fun execute(params: GetPreviewUrlTask.Params): PreviewUrlData {
        return when (params.cacheStrategy) {
            CacheStrategy.NoCache       -> doRequest(params.url, params.timestamp)
            is CacheStrategy.TtlCache   -> doRequestWithCache(
                    params.url,
                    params.timestamp,
                    params.cacheStrategy.validityDurationInMillis,
                    params.cacheStrategy.strict
            )
            CacheStrategy.InfiniteCache -> doRequestWithCache(
                    params.url,
                    params.timestamp,
                    Long.MAX_VALUE,
                    true
            )
        }
    }

    private suspend fun doRequest(url: String, timestamp: Long?): PreviewUrlData {
        return executeRequest(globalErrorReceiver) {
            mediaAPI.getPreviewUrlData(url, timestamp)
        }
                .toPreviewUrlData(url)
    }

    private fun JsonDict.toPreviewUrlData(url: String): PreviewUrlData {
        return PreviewUrlData(
                url = (get("og:url") as? String) ?: url,
                siteName = (get("og:site_name") as? String)?.unescapeHtml(),
                title = (get("og:title") as? String)?.unescapeHtml(),
                description = (get("og:description") as? String)?.unescapeHtml(),
                mxcUrl = get("og:image") as? String,
                imageHeight = (get("og:image:height") as? Double)?.toInt(),
                imageWidth = (get("og:image:width") as? Double)?.toInt(),
        )
    }

    private suspend fun doRequestWithCache(url: String, timestamp: Long?, validityDurationInMillis: Long, strict: Boolean): PreviewUrlData {
        
        var dataFromCache: PreviewUrlData? = null
        var isCacheValid = false
        monarchy.doWithRealm { realm ->
            val entity = PreviewUrlCacheEntity.get(realm, url)
            dataFromCache = entity?.toDomain()
            isCacheValid = entity != null && Date().time < entity.lastUpdatedTimestamp + validityDurationInMillis
        }

        val finalDataFromCache = dataFromCache
        if (finalDataFromCache != null && isCacheValid) {
            return finalDataFromCache
        }

        
        val data = try {
            doRequest(url, timestamp)
        } catch (throwable: Throwable) {
            
            return finalDataFromCache
                    ?.takeIf { !strict }
                    ?: throw throwable
        }

        
        monarchy.awaitTransaction { realm ->
            val previewUrlCacheEntity = PreviewUrlCacheEntity.getOrCreate(realm, url)
            previewUrlCacheEntity.urlFromServer = data.url
            previewUrlCacheEntity.siteName = data.siteName
            previewUrlCacheEntity.title = data.title
            previewUrlCacheEntity.description = data.description
            previewUrlCacheEntity.mxcUrl = data.mxcUrl
            previewUrlCacheEntity.imageHeight = data.imageHeight
            previewUrlCacheEntity.imageWidth = data.imageWidth
            previewUrlCacheEntity.lastUpdatedTimestamp = Date().time
        }

        return data
    }
}
