

package im.vector.app.core.utils

import java.util.Locale

fun String.safeCapitalize(locale: Locale): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(locale)
        } else {
            char.toString()
        }
    }
}
