

package im.vector.app.core.utils

import com.vanniktech.emoji.EmojiUtils


fun containsOnlyEmojis(str: String?): Boolean {
    
    return EmojiUtils.isOnlyEmojis(str)
}


fun CharSequence.splitEmoji(): List<CharSequence> {
    val result = mutableListOf<CharSequence>()

    var index = 0

    while (index < length) {
        val firstChar = get(index)

        if (firstChar.code == 0x200e) {
            
        } else if (firstChar.code in 0xD800..0xDBFF && index + 1 < length) {
            
            val secondChar = get(index + 1)

            if (secondChar.code in 0xDC00..0xDFFF) {
                
                result.add("$firstChar$secondChar")
                index++
            } else {
                
                result.add("$firstChar")
            }
        } else {
            
            result.add("$firstChar")
        }

        index++
    }

    return result
}
