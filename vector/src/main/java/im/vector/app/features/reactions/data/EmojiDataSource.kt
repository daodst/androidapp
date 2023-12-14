
package im.vector.app.features.reactions.data

import android.content.res.Resources
import android.graphics.Paint
import androidx.core.graphics.PaintCompat
import com.squareup.moshi.Moshi
import im.vector.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmojiDataSource @Inject constructor(
        appScope: CoroutineScope,
        resources: Resources
) {
    private val paint = Paint()
    val rawData = appScope.async(Dispatchers.IO, CoroutineStart.LAZY) {
        resources.openRawResource(R.raw.emoji_picker_datasource)
                .use { input ->
                    Moshi.Builder()
                            .build()
                            .adapter(EmojiData::class.java)
                            .fromJson(input.bufferedReader().use { it.readText() })
                }
                ?.let { parsedRawData ->
                    
                    
                    parsedRawData.copy(
                            emojis = mutableMapOf<String, EmojiItem>().apply {
                                parsedRawData.emojis.keys.forEach { key ->
                                    val origin = parsedRawData.emojis[key] ?: return@forEach

                                    
                                    if (isEmojiRenderable(origin.emoji)) {
                                        if (origin.keywords.contains(key) || key.contains("_")) {
                                            put(key, origin)
                                        } else {
                                            put(key, origin.copy(keywords = origin.keywords + key))
                                        }
                                    }
                                }
                            },
                            categories = mutableListOf<EmojiCategory>().apply {
                                parsedRawData.categories.forEach { entry ->
                                    add(EmojiCategory(entry.id, entry.name, mutableListOf<String>().apply {
                                        entry.emojis.forEach { e ->
                                            if (isEmojiRenderable(parsedRawData.emojis[e]!!.emoji)) {
                                                add(e)
                                            }
                                        }
                                    }))
                                }
                            }
                    )
                }
                ?: EmojiData(emptyList(), emptyMap(), emptyMap())
    }

    private val quickReactions = mutableListOf<EmojiItem>()

    private fun isEmojiRenderable(emoji: String): Boolean {
        return PaintCompat.hasGlyph(paint, emoji)
    }

    suspend fun filterWith(query: String): List<EmojiItem> {
        val words = query.split("\\s".toRegex())
        val rawData = this.rawData.await()
        
        return (rawData.emojis.values
                .asSequence()
                .filter { emojiItem ->
                    emojiItem.name.contains(query, true)
                }
                .sortedBy { it.name } +
                
                rawData.emojis.values
                        .filter { emojiItem ->
                            words.fold(true) { prev, word ->
                                prev && emojiItem.keywords.any { keyword -> keyword.contains(word, true) }
                            }
                        }
                        .sortedBy { it.name })
                
                .distinct()
                .toList()
    }

    suspend fun getQuickReactions(): List<EmojiItem> {
        if (quickReactions.isEmpty()) {
            listOf(
                    "thumbs-up", 
                    "thumbs-down", 
                    "grinning-face-with-smiling-eyes", 
                    "party-popper", 
                    "confused-face", 
                    "red-heart", 
                    "rocket", 
                    "eyes" 
            )
                    .mapNotNullTo(quickReactions) { rawData.await().emojis[it] }
        }

        return quickReactions
    }

    companion object {
        val quickEmojis = listOf("\uD83D\uDC4D️", "\uD83D\uDC4E️", "😄", "🎉", "😕", "❤️", "🚀", "👀")
    }
}
