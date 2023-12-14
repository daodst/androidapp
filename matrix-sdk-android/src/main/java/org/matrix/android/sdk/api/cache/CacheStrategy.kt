

package org.matrix.android.sdk.api.cache

sealed class CacheStrategy {
    
    object NoCache : CacheStrategy()

    
    
    data class TtlCache(val validityDurationInMillis: Long, val strict: Boolean) : CacheStrategy()

    
    object InfiniteCache : CacheStrategy()
}
