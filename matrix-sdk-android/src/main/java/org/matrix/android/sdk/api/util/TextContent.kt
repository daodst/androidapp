

package org.matrix.android.sdk.api.util


data class TextContent(
        val text: String,
        val formattedText: String? = null
) {
    fun takeFormatted() = formattedText ?: text
}
