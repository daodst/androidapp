
package im.vector.app.features.reactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.vector.app.core.utils.LiveEvent
import im.vector.app.features.reactions.data.EmojiData
import im.vector.app.features.reactions.data.EmojiDataSource
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmojiChooserViewModel @Inject constructor(private val emojiDataSource: EmojiDataSource) : ViewModel() {

    val emojiData: MutableLiveData<EmojiData> = MutableLiveData()
    val navigateEvent: MutableLiveData<LiveEvent<String>> = MutableLiveData()
    var selectedReaction: String? = null
    var eventId: String? = null

    val currentSection: MutableLiveData<Int> = MutableLiveData()
    val moveToSection: MutableLiveData<Int> = MutableLiveData()

    init {
        loadEmojiData()
    }

    private fun loadEmojiData() {
        viewModelScope.launch {
            val rawData = emojiDataSource.rawData.await()
            emojiData.postValue(rawData)
        }
    }

    fun onReactionSelected(reaction: String) {
        selectedReaction = reaction
        navigateEvent.value = LiveEvent(NAVIGATE_FINISH)
    }

    
    fun setCurrentSection(section: Int) {
        currentSection.value = section
    }

    
    fun scrollToSection(section: Int) {
        moveToSection.value = section
    }

    companion object {
        const val NAVIGATE_FINISH = "NAVIGATE_FINISH"
    }
}
