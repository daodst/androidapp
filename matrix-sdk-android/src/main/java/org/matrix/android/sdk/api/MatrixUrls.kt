

package org.matrix.android.sdk.api


object MatrixUrls {
    
    const val MATRIX_CONTENT_URI_SCHEME = "mxc://"

    
    fun String.isMxcUrl() = startsWith(MATRIX_CONTENT_URI_SCHEME)

    
    fun String.removeMxcPrefix() = removePrefix(MATRIX_CONTENT_URI_SCHEME)
}
