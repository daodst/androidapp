

package im.vector.app.features.reactions.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class EmojiItem(
        @Json(name = "a") val name: String,
        @Json(name = "b") val unicode: String,
        @Json(name = "j") val keywords: List<String> = emptyList()
) {
    
    var cache: String? = null

    val emoji: String
        get() {
            cache?.let { return it }

            
            val utf8Text = unicode
                    .split("-")
                    .joinToString("") { "\\u$it" }
            return fromUnicode(utf8Text)
                    .also { cache = it }
        }

    companion object {
        private fun fromUnicode(unicode: String): String {
            val arr = unicode
                    .replace("\\", "")
                    .split("u".toRegex())
                    .dropLastWhile { it.isEmpty() }
            return buildString {
                for (i in 1 until arr.size) {
                    val hexVal = Integer.parseInt(arr[i], 16)
                    append(Character.toChars(hexVal))
                }
            }
        }
    }
}
