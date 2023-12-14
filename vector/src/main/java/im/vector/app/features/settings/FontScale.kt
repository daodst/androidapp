

package im.vector.app.features.settings

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.edit
import im.vector.app.R
import im.vector.app.core.di.DefaultSharedPreferences


object FontScale {
    
    private const val APPLICATION_FONT_SCALE_KEY = "APPLICATION_FONT_SCALE_KEY"

    data class FontScaleValue(
            val index: Int,
            
            val preferenceValue: String,
            val scale: Float,
            @StringRes
            val nameResId: Int
    )

    private val fontScaleValues = listOf(
            FontScaleValue(0, "FONT_SCALE_TINY", 0.70f, R.string.tiny),
            FontScaleValue(1, "FONT_SCALE_SMALL", 0.85f, R.string.small),
            FontScaleValue(2, "FONT_SCALE_NORMAL", 1.00f, R.string.normal),
            FontScaleValue(3, "FONT_SCALE_LARGE", 1.15f, R.string.large),
            FontScaleValue(4, "FONT_SCALE_LARGER", 1.30f, R.string.larger),
            FontScaleValue(5, "FONT_SCALE_LARGEST", 1.45f, R.string.largest),
            FontScaleValue(6, "FONT_SCALE_HUGE", 1.60f, R.string.huge)
    )

    private val normalFontScaleValue = fontScaleValues[2]

    
    fun getFontScaleValue(context: Context): FontScaleValue {
        val preferences = DefaultSharedPreferences.getInstance(context)

        return if (APPLICATION_FONT_SCALE_KEY !in preferences) {
            val fontScale = context.resources.configuration.fontScale

            (fontScaleValues.firstOrNull { it.scale == fontScale } ?: normalFontScaleValue)
                    .also { preferences.edit { putString(APPLICATION_FONT_SCALE_KEY, it.preferenceValue) } }
        } else {
            val pref = preferences.getString(APPLICATION_FONT_SCALE_KEY, null)
            fontScaleValues.firstOrNull { it.preferenceValue == pref } ?: normalFontScaleValue
        }
    }

    fun updateFontScale(context: Context, index: Int) {
        fontScaleValues.getOrNull(index)?.let {
            saveFontScaleValue(context, it)
        }
    }

    
    private fun saveFontScaleValue(context: Context, fontScaleValue: FontScaleValue) {
        DefaultSharedPreferences.getInstance(context)
                .edit { putString(APPLICATION_FONT_SCALE_KEY, fontScaleValue.preferenceValue) }
    }
}
