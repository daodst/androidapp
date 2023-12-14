

package org.matrix.android.sdk.internal.util



internal fun base64UrlToBase64(base64Url: String): String {
    return base64Url.replace('-', '+')
            .replace('_', '/')
}

internal fun base64ToBase64Url(base64: String): String {
    return base64.replace("\n".toRegex(), "")
            .replace("\\+".toRegex(), "-")
            .replace('/', '_')
            .replace("=", "")
}

internal fun base64ToUnpaddedBase64(base64: String): String {
    return base64.replace("\n".toRegex(), "")
            .replace("=", "")
}
