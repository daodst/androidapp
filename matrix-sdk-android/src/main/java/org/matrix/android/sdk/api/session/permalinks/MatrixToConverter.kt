

package org.matrix.android.sdk.api.session.permalinks

import android.net.Uri


object MatrixToConverter {

    
    fun convert(uri: Uri): Uri? {
        val uriString = uri.toString()

        return when {
            
            uriString.startsWith(PermalinkService.MATRIX_TO_URL_BASE) -> uri
            
            SUPPORTED_PATHS.any { it in uriString }                   -> {
                val path = SUPPORTED_PATHS.first { it in uriString }
                Uri.parse(PermalinkService.MATRIX_TO_URL_BASE + uriString.substringAfter(path))
            }
            
            else                                                      -> null
        }
    }

    private val SUPPORTED_PATHS = listOf(
            "/#/room/",
            "/#/user/",
            "/#/group/"
    )
}
