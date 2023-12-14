

package im.vector.app.core.resources

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import im.vector.app.features.themes.ThemeUtils
import javax.inject.Inject

class ColorProvider @Inject constructor(private val context: Context) {

    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    
    @ColorInt
    fun getColorFromAttribute(@AttrRes colorAttribute: Int): Int {
        return ThemeUtils.getColor(context, colorAttribute)
    }
}
