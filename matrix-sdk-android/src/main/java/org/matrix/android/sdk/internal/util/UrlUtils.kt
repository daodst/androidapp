

package org.matrix.android.sdk.internal.util

import java.net.URL

internal fun String.isValidUrl(): Boolean {
    return try {
        URL(this)
        true
    } catch (t: Throwable) {
        false
    }
}


internal fun String.ensureProtocol(): String {
    return when {
        isEmpty()           -> this
        !startsWith("http") -> "https://$this"
        else                -> this
    }
}


internal fun String.ensureTrailingSlash(): String {
    return when {
        isEmpty()      -> this
        !endsWith("/") -> "$this/"
        else           -> this
    }
}
