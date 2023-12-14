
package im.vector.app

import android.content.Context
import androidx.core.provider.FontRequest
import androidx.emoji2.text.EmojiCompat
import androidx.emoji2.text.FontRequestEmojiCompatConfig
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

fun interface EmojiSpanify {
    fun spanify(sequence: CharSequence): CharSequence
}

@Singleton
class EmojiCompatWrapper @Inject constructor(private val context: Context) : EmojiSpanify {

    private var initialized = false

    fun init(fontRequest: FontRequest) {
        
        val config = FontRequestEmojiCompatConfig(context, fontRequest)
                
                .setReplaceAll(true)
        
        EmojiCompat.init(config)
                .registerInitCallback(object : EmojiCompat.InitCallback() {
                    override fun onInitialized() {
                        Timber.v("Emoji compat onInitialized success ")
                        initialized = true
                    }

                    override fun onFailed(throwable: Throwable?) {
                        Timber.e(throwable, "Failed to init EmojiCompat")
                    }
                })
    }

    override fun spanify(sequence: CharSequence): CharSequence {
        if (initialized) {
            try {
                return EmojiCompat.get().process(sequence) ?: sequence
            } catch (throwable: Throwable) {
                
                Timber.e(throwable, "Failed to init EmojiCompat")
                return sequence
            }
        } else {
            return sequence
        }
    }
}
