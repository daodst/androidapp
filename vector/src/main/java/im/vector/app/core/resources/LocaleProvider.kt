

package im.vector.app.core.resources

import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import androidx.core.os.ConfigurationCompat
import java.util.Locale
import javax.inject.Inject

class LocaleProvider @Inject constructor(private val resources: Resources) {

    fun current(): Locale {
        return ConfigurationCompat.getLocales(resources.configuration)[0]
    }
}

fun LocaleProvider.isEnglishSpeaking() = current().language.startsWith("en")

fun LocaleProvider.getLayoutDirectionFromCurrentLocale() = TextUtils.getLayoutDirectionFromLocale(current())

fun LocaleProvider.isRTL() = getLayoutDirectionFromCurrentLocale() == View.LAYOUT_DIRECTION_RTL
