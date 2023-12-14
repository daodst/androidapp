

package im.vector.app.core.utils

import java.net.URL

fun String.isValidUrl(): Boolean {
    return try {
        URL(this)
        true
    } catch (t: Throwable) {
        false
    }
}


fun String.ensureProtocol(): String {
    return when {
        isEmpty()           -> this
        !startsWith("http") -> "https://$this"
        else                -> this
    }
}

fun String.ensureTrailingSlash(): String {
    return when {
        isEmpty()      -> this
        !endsWith("/") -> "$this/"
        else           -> this
    }
}
