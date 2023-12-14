

package org.matrix.android.sdk.api


object MatrixConstants {
    
    const val ALIAS_MAX_LENGTH = 255

    fun maxAliasLocalPartLength(domain: String): Int {
        return (ALIAS_MAX_LENGTH - 1  - 1  - domain.length)
                .coerceAtLeast(0)
    }
}
