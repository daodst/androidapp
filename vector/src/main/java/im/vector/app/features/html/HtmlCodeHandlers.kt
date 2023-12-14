

package im.vector.app.features.html

import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler

class CodeTagHandler : TagHandler() {

    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        SpannableBuilder.setSpans(
                visitor.builder(),
                HtmlCodeSpan(visitor.configuration().theme(), false),
                tag.start(),
                tag.end()
        )
    }

    override fun supportedTags(): List<String> {
        return listOf("code")
    }
}


class CodePreTagHandler : TagHandler() {
    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val htmlCodeSpan = visitor.builder()
                .getSpans(tag.start(), tag.end())
                .firstOrNull {
                    it.what is HtmlCodeSpan
                }
        if (htmlCodeSpan != null) {
            (htmlCodeSpan.what as HtmlCodeSpan).isBlock = true
        }
    }

    override fun supportedTags(): List<String> {
        return listOf("pre")
    }
}
