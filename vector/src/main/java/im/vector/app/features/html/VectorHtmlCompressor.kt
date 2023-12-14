

package im.vector.app.features.html

import com.googlecode.htmlcompressor.compressor.Compressor
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VectorHtmlCompressor @Inject constructor() {

    
    private val htmlCompressor: Compressor = HtmlCompressor()

    fun compress(html: String): String {
        var result = htmlCompressor.compress(html)

        
        result = result.replace("<br> ", "<br>")
        result = result.replace("<br/> ", "<br/>")
        result = result.replace("<p> ", "<p>")

        return result
    }
}
