

package im.vector.app.core.glide

import com.bumptech.glide.load.Option
import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt

const val ElementToDecryptOptionKey = "im.vector.app.core.glide.ElementToDecrypt"

val ELEMENT_TO_DECRYPT = Option.memory(
        ElementToDecryptOptionKey, ElementToDecrypt("", "", ""))
