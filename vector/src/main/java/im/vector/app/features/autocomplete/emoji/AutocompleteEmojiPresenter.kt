

package im.vector.app.features.autocomplete.emoji

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.features.autocomplete.AutocompleteClickListener
import im.vector.app.features.autocomplete.RecyclerViewPresenter
import im.vector.app.features.reactions.data.EmojiDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

class AutocompleteEmojiPresenter @Inject constructor(context: Context,
                                                     private val emojiDataSource: EmojiDataSource,
                                                     private val controller: AutocompleteEmojiController) :
        RecyclerViewPresenter<String>(context), AutocompleteClickListener<String> {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        controller.listener = this
    }

    fun clear() {
        coroutineScope.coroutineContext.cancelChildren()
        controller.listener = null
    }

    override fun instantiateAdapter(): RecyclerView.Adapter<*> {
        return controller.adapter
    }

    override fun onItemClick(t: String) {
        dispatchClick(t)
    }

    override fun onQuery(query: CharSequence?) {
        coroutineScope.launch {
            val data = if (query.isNullOrBlank()) {
                
                emojiDataSource.getQuickReactions()
            } else {
                emojiDataSource.filterWith(query.toString())
            }
            controller.setData(data)
        }
    }
}
