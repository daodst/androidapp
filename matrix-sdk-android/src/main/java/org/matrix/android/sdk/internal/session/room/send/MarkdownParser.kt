

package org.matrix.android.sdk.internal.session.room.send

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.matrix.android.sdk.api.util.TextContent
import org.matrix.android.sdk.internal.session.room.AdvancedCommonmarkParser
import org.matrix.android.sdk.internal.session.room.SimpleCommonmarkParser
import org.matrix.android.sdk.internal.session.room.send.pills.TextPillsUtils
import javax.inject.Inject


internal class MarkdownParser @Inject constructor(
        @AdvancedCommonmarkParser private val advancedParser: Parser,
        @SimpleCommonmarkParser private val simpleParser: Parser,
        private val htmlRenderer: HtmlRenderer,
        private val textPillsUtils: TextPillsUtils
) {

    private val mdSpecialChars = "[`_\\-*>.\\[\\]#~$]".toRegex()

    
    fun parse(text: CharSequence, force: Boolean = false, advanced: Boolean = true): TextContent {
        val source = textPillsUtils.processSpecialSpansToMarkdown(text) ?: text.toString()

        
        if (!force && source.contains(mdSpecialChars).not()) {
            return TextContent(source)
        }

        val document = if (advanced) advancedParser.parse(source) else simpleParser.parse(source)
        val htmlText = htmlRenderer.render(document)

        
        val cleanHtmlText = if (htmlText.lastIndexOf("<p>") == 0) {
            htmlText.removeSurrounding("<p>", "</p>\n")
        } else {
            htmlText
        }

        return if (isFormattedTextPertinent(source, cleanHtmlText)) {
            
            
            
            
            TextContent(text.toString(), cleanHtmlText.postTreatment())
        } else {
            TextContent(source)
        }
    }

    private fun isFormattedTextPertinent(text: String, htmlText: String?) =
            text != htmlText && htmlText != "<p>${text.trim()}</p>\n"

    
    private fun String.postTreatment(): String {
        return this
                
                .trim()
        
        
        
    }
}
