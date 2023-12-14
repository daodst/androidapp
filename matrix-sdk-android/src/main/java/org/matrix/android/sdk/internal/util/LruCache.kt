

package org.matrix.android.sdk.internal.util

import androidx.collection.LruCache

@Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
internal inline fun <K, V> LruCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    return get(key) ?: defaultValue().also { put(key, it) }
}
