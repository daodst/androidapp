
package org.commonmark.ext.maths.internal

import org.commonmark.ext.maths.DisplayMaths
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter
import java.util.Collections

internal class MathsHtmlNodeRenderer(private val context: HtmlNodeRendererContext) : MathsNodeRenderer() {
    private val html: HtmlWriter = context.writer
    override fun render(node: Node) {
        val display = node.javaClass == DisplayMaths::class.java
        val contents = node.firstChild 
        val latex = (contents as Text).literal
        val attributes = context.extendAttributes(node, if (display) "div" else "span", Collections.singletonMap("data-mx-maths",
                latex))
        html.tag(if (display) "div" else "span", attributes)
        html.tag("code")
        context.render(contents)
        html.tag("/code")
        html.tag(if (display) "/div" else "/span")
    }
}
