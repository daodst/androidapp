
package im.vector.app.core.linkify

import android.text.Spannable
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.core.text.util.LinkifyCompat

object VectorLinkify {
    
    fun addLinks(spannable: Spannable, keepExistingUrlSpan: Boolean = false) {
        
        val createdSpans = ArrayList<LinkSpec>()

        if (keepExistingUrlSpan) {
            
            spannable.forEachUrlSpanIndexed { _, urlSpan, start, end ->
                createdSpans.add(LinkSpec(URLSpan(urlSpan.url), start, end, important = true))
            }
        }

        
        LinkifyCompat.addLinks(spannable, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)

        
        spannable.forEachUrlSpanIndexed { _, urlSpan, start, end ->
            spannable.removeSpan(urlSpan)

            
            if (urlSpan.url?.startsWith("tel:") == true) {
                if (end - start > 6) { 
                    createdSpans.add(LinkSpec(URLSpan(urlSpan.url), start, end))
                }
                return@forEachUrlSpanIndexed
            }

            
            if (urlSpan.url?.startsWith("mailto:") == true) {
                val protocolLength = "mailto:".length
                if (start - protocolLength >= 0 && "mailto:" == spannable.substring(start - protocolLength, start)) {
                    
                    createdSpans.add(LinkSpec(URLSpan(urlSpan.url), start - protocolLength, end))
                } else {
                    createdSpans.add(LinkSpec(URLSpan(urlSpan.url), start, end))
                }

                return@forEachUrlSpanIndexed
            }

            

            
            if (end < spannable.length - 1 && spannable[end] == '/') {
                
                val spec = LinkSpec(URLSpan(urlSpan.url + "/"), start, end + 1)
                createdSpans.add(spec)
                return@forEachUrlSpanIndexed
            }
            
            if (spannable[end - 1] == ')') {
                var lbehind = end - 2
                var isFullyContained = 1
                while (lbehind > start) {
                    val char = spannable[lbehind]
                    if (char == '(') isFullyContained -= 1
                    if (char == ')') isFullyContained += 1
                    lbehind--
                }
                if (isFullyContained != 0) {
                    
                    val span = URLSpan(spannable.substring(start, end - 1))
                    val spec = LinkSpec(span, start, end - 1)
                    createdSpans.add(spec)
                    return@forEachUrlSpanIndexed
                }
            }

            createdSpans.add(LinkSpec(URLSpan(urlSpan.url), start, end))
        }

        LinkifyCompat.addLinks(spannable, VectorAutoLinkPatterns.GEO_URI.toPattern(), "geo:", arrayOf("geo:"), geoMatchFilter, null)
        spannable.forEachUrlSpanIndexed { _, urlSpan, start, end ->
            spannable.removeSpan(urlSpan)
            createdSpans.add(LinkSpec(URLSpan(urlSpan.url), start, end))
        }

        pruneOverlaps(createdSpans)
        for (spec in createdSpans) {
            spannable.setSpan(spec.span, spec.start, spec.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun pruneOverlaps(links: ArrayList<LinkSpec>) {
        links.sortWith(COMPARATOR)
        var len = links.size
        var i = 0
        while (i < len - 1) {
            val a = links[i]
            val b = links[i + 1]
            var remove = -1

            
            if (b.start in a.start until a.end) {
                if (a.important != b.important) {
                    remove = if (a.important) i + 1 else i
                } else {
                    when {
                        b.end <= a.end                    ->
                            
                            remove = i + 1
                        a.end - a.start > b.end - b.start ->
                            
                            remove = i + 1
                        a.end - a.start < b.end - b.start ->
                            
                            remove = i
                    }
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

    private data class LinkSpec(val span: URLSpan,
                                val start: Int,
                                val end: Int,
                                val important: Boolean = false)

    private val COMPARATOR = Comparator<LinkSpec> { (_, startA, endA), (_, startB, endB) ->
        if (startA < startB) {
            return@Comparator -1
        }

        if (startA > startB) {
            return@Comparator 1
        }

        if (endA < endB) {
            return@Comparator 1
        }

        if (endA > endB) {
            -1
        } else 0
    }

    
    private val geoMatchFilter = Linkify.MatchFilter { s, start, end ->
        if (s[start] != 'g') { 
            return@MatchFilter end - start > 12
        }
        return@MatchFilter true
    }

    private inline fun Spannable.forEachUrlSpanIndexed(action: (index: Int, urlSpan: URLSpan, start: Int, end: Int) -> Unit) {
        getSpans(0, length, URLSpan::class.java)
                .forEachIndexed { index, urlSpan ->
                    val start = getSpanStart(urlSpan)
                    val end = getSpanEnd(urlSpan)
                    action.invoke(index, urlSpan, start, end)
                }
    }
}
