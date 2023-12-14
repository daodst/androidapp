

package im.vector.app.features.themes

import android.content.Context
import javax.inject.Inject


class ThemeProvider @Inject constructor(
        private val context: Context
) {
    fun isLightTheme() = ThemeUtils.isLightTheme(context)
}
