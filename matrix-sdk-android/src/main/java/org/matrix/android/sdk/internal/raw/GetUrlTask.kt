

package org.matrix.android.sdk.internal.raw

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.cache.CacheStrategy
import org.matrix.android.sdk.internal.database.model.RawCacheEntity
import org.matrix.android.sdk.internal.database.query.get
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.di.GlobalDatabase
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import java.util.Date
import javax.inject.Inject

internal interface GetUrlTask : Task<GetUrlTask.Params, String> {
    data class Params(
            val url: String,
            val cacheStrategy: CacheStrategy
    )
}

internal class DefaultGetUrlTask @Inject constructor(
        private val rawAPI: RawAPI,
        @GlobalDatabase private val monarchy: Monarchy
) : GetUrlTask {

    override suspend fun execute(params: GetUrlTask.Params): String {
        return when (params.cacheStrategy) {
            CacheStrategy.NoCache       -> doRequest(params.url)
            is CacheStrategy.TtlCache   -> doRequestWithCache(
                    params.url,
                    params.cacheStrategy.validityDurationInMillis,
                    params.cacheStrategy.strict
            )
            CacheStrategy.InfiniteCache -> doRequestWithCache(
                    params.url,
                    Long.MAX_VALUE,
                    true
            )
        }
    }

    private suspend fun doRequest(url: String): String {
        return executeRequest(null) {
            rawAPI.getUrl(url)
        }
                .string()
    }

    private suspend fun doRequestWithCache(url: String, validityDurationInMillis: Long, strict: Boolean): String {
        
        var dataFromCache: String? = null
        var isCacheValid = false
        monarchy.doWithRealm { realm ->
            val entity = RawCacheEntity.get(realm, url)
            dataFromCache = entity?.data
            isCacheValid = entity != null && Date().time < entity.lastUpdatedTimestamp + validityDurationInMillis
        }

        if (dataFromCache != null && isCacheValid) {
            return dataFromCache as String
        }

        
        val data = try {
            doRequest(url)
        } catch (throwable: Throwable) {
            
            return dataFromCache
                    ?.takeIf { !strict }
                    ?: throw throwable
        }

        
        monarchy.awaitTransaction { realm ->
            val rawCacheEntity = RawCacheEntity.getOrCreate(realm, url)
            rawCacheEntity.data = data
            rawCacheEntity.lastUpdatedTimestamp = Date().time
        }

        return data
    }
}
