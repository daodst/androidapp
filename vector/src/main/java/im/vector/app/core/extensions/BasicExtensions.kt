

package im.vector.app.core.extensions

import android.util.Patterns
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.matrix.android.sdk.api.extensions.ensurePrefix

fun Boolean.toOnOff() = if (this) "ON" else "OFF"

inline fun <T> T.ooi(block: (T) -> Unit): T = also(block)


fun CharSequence.isEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun CharSequence.isMsisdn(): Boolean {
    return try {
        PhoneNumberUtil.getInstance().parse(ensurePrefix("+"), null)
        true
    } catch (e: NumberParseException) {
        false
    }
}


fun String?.insertBeforeLast(insert: String, delimiter: String = "."): String {
    if (this == null) return insert
    val idx = lastIndexOf(delimiter)
    return if (idx == -1) {
        this + insert
    } else {
        replaceRange(idx, idx, insert)
    }
}

inline fun <reified R> Any?.takeAs(): R? {
    return takeIf { it is R } as R?
}
