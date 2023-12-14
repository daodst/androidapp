
package org.matrix.android.sdk.api.util

import org.matrix.android.sdk.internal.util.unescapeHtml

object ContentUtils {
    fun extractUsefulTextFromReply(repliedBody: String): String {
        val lines = repliedBody.lines()
        var wellFormed = repliedBody.startsWith(">")
        var endOfPreviousFound = false
        val usefulLines = ArrayList<String>()
        lines.forEach {
            if (it == "") {
                endOfPreviousFound = true
                return@forEach
            }
            if (!endOfPreviousFound) {
                wellFormed = wellFormed && it.startsWith(">")
            } else {
                usefulLines.add(it)
            }
        }
        return usefulLines.joinToString("\n").takeIf { wellFormed } ?: repliedBody
    }

    fun extractUsefulTextFromHtmlReply(repliedBody: String): String {
        if (repliedBody.startsWith("<mx-reply>")) {
            val closingTagIndex = repliedBody.lastIndexOf("</mx-reply>")
            if (closingTagIndex != -1) {
                return repliedBody.substring(closingTagIndex + "</mx-reply>".length).trim()
            }
        }
        return repliedBody
    }

    @Suppress("RegExpRedundantEscape")
    fun formatSpoilerTextFromHtml(formattedBody: String): String {
        
        
        return formattedBody.replace("(?<=<span data-mx-spoiler)=\\\".+?\\\">".toRegex(), ">")
                .replace("(?<=<span data-mx-spoiler>).+?(?=</span>)".toRegex()) { SPOILER_CHAR.repeat(it.value.length) }
                .unescapeHtml()
    }

    private const val SPOILER_CHAR = "█"
}
