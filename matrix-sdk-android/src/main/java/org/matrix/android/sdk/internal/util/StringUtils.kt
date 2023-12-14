

package org.matrix.android.sdk.internal.util

import timber.log.Timber
import java.util.Locale


internal fun convertToUTF8(s: String): String {
    return try {
        val bytes = s.toByteArray(Charsets.UTF_8)
        String(bytes)
    } catch (e: Exception) {
        Timber.e(e, "## convertToUTF8()  failed")
        s
    }
}


internal fun convertFromUTF8(s: String): String {
    return try {
        val bytes = s.toByteArray()
        String(bytes, Charsets.UTF_8)
    } catch (e: Exception) {
        Timber.e(e, "## convertFromUTF8()  failed")
        s
    }
}


internal fun String.caseInsensitiveFind(subString: String): Boolean {
    
    if (subString.isEmpty() || isEmpty()) {
        return false
    }

    try {
        val regex = Regex("(\\W|^)" + Regex.escape(subString) + "(\\W|$)", RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(this)
    } catch (e: Exception) {
        Timber.e(e, "## caseInsensitiveFind() : failed")
    }

    return false
}

internal val spaceChars = "[\u00A0\u2000-\u200B\u2800\u3000]".toRegex()


internal fun String.replaceSpaceChars(replacement: String = "") = replace(spaceChars, replacement)

internal fun String.safeCapitalize(): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(Locale.getDefault())
        } else {
            char.toString()
        }
    }
}

internal fun String.removeInvalidRoomNameChars() = "[^a-z0-9._%#@=+-]".toRegex().replace(this, "")
