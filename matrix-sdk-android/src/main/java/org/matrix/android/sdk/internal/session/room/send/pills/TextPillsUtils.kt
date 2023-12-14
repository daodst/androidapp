
package org.matrix.android.sdk.internal.session.room.send.pills

import android.text.SpannableString
import org.matrix.android.sdk.api.session.permalinks.PermalinkService
import org.matrix.android.sdk.api.session.room.send.MatrixItemSpan
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.internal.session.displayname.DisplayNameResolver
import java.util.Collections
import javax.inject.Inject


internal class TextPillsUtils @Inject constructor(
        private val mentionLinkSpecComparator: MentionLinkSpecComparator,
        private val displayNameResolver: DisplayNameResolver,
        private val permalinkService: PermalinkService
) {

    
    fun processSpecialSpansToHtml(text: CharSequence): String? {
        return transformPills(text, permalinkService.createMentionSpanTemplate(PermalinkService.SpanTemplateType.HTML))
    }

    
    fun processSpecialSpansToMarkdown(text: CharSequence): String? {
        return transformPills(text, permalinkService.createMentionSpanTemplate(PermalinkService.SpanTemplateType.MARKDOWN))
    }

    private fun transformPills(text: CharSequence, template: String): String? {
        val spannableString = SpannableString.valueOf(text)
        val pills = spannableString
                ?.getSpans(0, text.length, MatrixItemSpan::class.java)
                ?.map { MentionLinkSpec(it, spannableString.getSpanStart(it), spannableString.getSpanEnd(it)) }
                
                ?.filterNot { it.span.matrixItem is MatrixItem.EveryoneInRoomItem }
                ?.toMutableList()
                ?.takeIf { it.isNotEmpty() }
                ?: return null

        
        pruneOverlaps(pills)

        return buildString {
            var currIndex = 0
            pills.forEachIndexed { _, (urlSpan, start, end) ->
                
                
                append(text, currIndex, start)
                
                append(String.format(template, urlSpan.matrixItem.id, displayNameResolver.getBestName(urlSpan.matrixItem)))
                currIndex = end
            }
            
            append(text, currIndex, text.length)
        }
    }

    private fun pruneOverlaps(links: MutableList<MentionLinkSpec>) {
        Collections.sort(links, mentionLinkSpecComparator)
        var len = links.size
        var i = 0
        while (i < len - 1) {
            val a = links[i]
            val b = links[i + 1]
            var remove = -1

            
            if (b.start in a.start until a.end) {
                when {
                    b.end <= a.end                    ->
                        
                        remove = i + 1
                    a.end - a.start > b.end - b.start ->
                        
                        remove = i + 1
                    a.end - a.start < b.end - b.start ->
                        
                        remove = i
                }

                if (remove != -1) {
                    links.removeAt(remove)
                    len--
                    continue
                }
            }
            i++
        }
    }
}
