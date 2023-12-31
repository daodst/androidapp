

package im.vector.app.features.html

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import androidx.core.text.toSpannable
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.settings.VectorPreferences
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.PrecomputedFutureTextSetterCompat
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.latex.JLatexMathTheme
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import org.commonmark.node.Node
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventHtmlRenderer @Inject constructor(
        htmlConfigure: MatrixHtmlPluginConfigure,
        context: Context,
        vectorPreferences: VectorPreferences
) {

    interface PostProcessor {
        fun afterRender(renderedText: Spannable)
    }

    private val builder = Markwon.builder(context)
            .usePlugin(HtmlPlugin.create(htmlConfigure))

    private val markwon = if (vectorPreferences.latexMathsIsEnabled()) {
        builder
                .usePlugin(object : AbstractMarkwonPlugin() { 
                    override fun processMarkdown(markdown: String): String {
                        return markdown
                                .replace(Regex("""<span\s+data-mx-maths="([^"]*)">.*?</span>""")) { matchResult ->
                                    "$$" + matchResult.groupValues[1] + "$$"
                                }
                                .replace(Regex("""<div\s+data-mx-maths="([^"]*)">.*?</div>""")) { matchResult ->
                                    "\n$$\n" + matchResult.groupValues[1] + "\n$$\n"
                                }
                    }
                })
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(44F) { builder ->
                    builder.inlinesEnabled(true)
                    builder.theme().inlinePadding(JLatexMathTheme.Padding.symmetric(24, 8))
                })
    } else {
        builder
    }.textSetter(PrecomputedFutureTextSetterCompat.create()).build()

    val plugins: List<MarkwonPlugin> = markwon.plugins

    fun parse(text: String): Node {
        return markwon.parse(text)
    }

    
    fun render(text: String, vararg postProcessors: PostProcessor): CharSequence {
        return try {
            val parsed = markwon.parse(text)
            renderAndProcess(parsed, postProcessors)
        } catch (failure: Throwable) {
            Timber.v("Fail to render $text to html")
            text
        }
    }

    
    fun render(node: Node, vararg postProcessors: PostProcessor): CharSequence? {
        return try {
            renderAndProcess(node, postProcessors)
        } catch (failure: Throwable) {
            Timber.v("Fail to render $node to html")
            return null
        }
    }

    private fun renderAndProcess(node: Node, postProcessors: Array<out PostProcessor>): CharSequence {
        val renderedText = markwon.render(node).toSpannable()
        postProcessors.forEach {
            it.afterRender(renderedText)
        }
        return renderedText
    }
}

class MatrixHtmlPluginConfigure @Inject constructor(private val colorProvider: ColorProvider, private val resources: Resources) : HtmlPlugin.HtmlConfigure {

    override fun configureHtml(plugin: HtmlPlugin) {
        plugin
                .addHandler(FontTagHandler())
                .addHandler(ParagraphHandler(DimensionConverter(resources)))
                .addHandler(MxReplyTagHandler())
                .addHandler(CodePreTagHandler())
                .addHandler(CodeTagHandler())
                .addHandler(SpanHandler(colorProvider))
    }
}
