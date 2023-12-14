

package im.vector.app.features.html

import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler

class MxReplyTagHandler : TagHandler() {

    override fun supportedTags() = listOf("mx-reply")

    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        visitChildren(visitor, renderer, tag.asBlock)
        val replyText = visitor.builder().removeFromEnd(tag.end())
        visitor.builder().append("\n\n").append(replyText)
    }
}
