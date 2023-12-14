
package im.vector.app.features.reactions

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import im.vector.app.EmojiCompatFontProvider
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.genericFooterItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import javax.inject.Inject

class EmojiSearchResultController @Inject constructor(
        private val stringProvider: StringProvider,
        private val fontProvider: EmojiCompatFontProvider
) : TypedEpoxyController<EmojiSearchResultViewState>() {

    var emojiTypeface: Typeface? = fontProvider.typeface

    private val fontProviderListener = object : EmojiCompatFontProvider.FontProviderListener {
        override fun compatibilityFontUpdate(typeface: Typeface?) {
            emojiTypeface = typeface
        }
    }

    init {
        fontProvider.addListener(fontProviderListener)
    }

    var listener: ReactionClickListener? = null

    override fun buildModels(data: EmojiSearchResultViewState?) {
        val results = data?.results ?: return
        val host = this

        if (results.isEmpty()) {
            if (data.query.isEmpty()) {
                
                genericFooterItem {
                    id("type.query.item")
                    text(host.stringProvider.getString(R.string.reaction_search_type_hint).toEpoxyCharSequence())
                }
            } else {
                
                genericFooterItem {
                    id("no.results.item")
                    text(host.stringProvider.getString(R.string.no_result_placeholder).toEpoxyCharSequence())
                }
            }
        } else {
            
            results.forEach { emojiItem ->
                emojiSearchResultItem {
                    id(emojiItem.name)
                    emojiItem(emojiItem)
                    emojiTypeFace(host.emojiTypeface)
                    currentQuery(data.query)
                    onClickListener { host.listener?.onReactionSelected(emojiItem.emoji) }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        fontProvider.removeListener(fontProviderListener)
    }
}
