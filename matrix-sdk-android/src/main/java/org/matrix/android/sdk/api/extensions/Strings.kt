

package org.matrix.android.sdk.api.extensions

fun CharSequence.ensurePrefix(prefix: CharSequence): CharSequence {
    return when {
        startsWith(prefix) -> this
        else               -> "$prefix$this"
    }
}


fun StringBuilder.appendNl(str: String) = append("\n").append(str)
