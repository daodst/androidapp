

package org.matrix.android.sdk.internal.util

internal fun String.hasSpecialGlobChar(): Boolean {
    return contains("*") || contains("?")
}

internal fun String.simpleGlobToRegExp(): String {
    val string = this
    return buildString {
        
        string.forEach { char ->
            when (char) {
                '*'  -> append(".*")
                '?'  -> append(".")
                '.'  -> append("\\.")
                '\\' -> append("\\\\")
                else -> append(char)
            }
        }
        
    }
}
