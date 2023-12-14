

package org.matrix.android.sdk.api.raw

import org.matrix.android.sdk.api.cache.CacheStrategy


interface RawService {
    
    suspend fun getUrl(url: String, cacheStrategy: CacheStrategy): String

    
    suspend fun getWellknown(domain: String): String

    
    suspend fun clearCache()
}
