

package im.vector.app.features.themes

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.DrawableCompat
import im.vector.app.R
import im.vector.app.core.di.DefaultSharedPreferences
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference


object ThemeUtils {
    
    const val APPLICATION_THEME_KEY = "APPLICATION_THEME_KEY"

    
    private const val SYSTEM_THEME_VALUE = "system"
    private const val THEME_DARK_VALUE = "dark"
    private const val THEME_LIGHT_VALUE = "light"
    private const val THEME_BLACK_VALUE = "black"

    
    private const val DEFAULT_THEME = SYSTEM_THEME_VALUE

    private var currentTheme = AtomicReference<String>(null)

    private val mColorByAttr = HashMap<Int, Int>()

    
    fun init(context: Context) {
        val theme = getApplicationTheme(context)
        setApplicationTheme(context, theme)
    }

    
    fun isSystemTheme(context: Context): Boolean {
        val theme = getApplicationTheme(context)
        return theme == SYSTEM_THEME_VALUE
    }

    
    fun isLightTheme(context: Context): Boolean {
        val theme = getApplicationTheme(context)
        return theme == THEME_LIGHT_VALUE ||
                (theme == SYSTEM_THEME_VALUE && !isSystemDarkTheme(context.resources))
    }

    
    fun getApplicationTheme(context: Context): String {
        val currentTheme = this.currentTheme.get()
        return if (currentTheme == null) {
            val prefs = DefaultSharedPreferences.getInstance(context)
            var themeFromPref = prefs.getString(APPLICATION_THEME_KEY, DEFAULT_THEME) ?: DEFAULT_THEME
            if (themeFromPref == "status") {
                
                themeFromPref = DEFAULT_THEME
                prefs.edit { putString(APPLICATION_THEME_KEY, DEFAULT_THEME) }
            }
            this.currentTheme.set(themeFromPref)
            themeFromPref
        } else {
            currentTheme
        }
    }

    
    private fun isSystemDarkTheme(resources: Resources): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    
    fun setApplicationTheme(context: Context, aTheme: String) {
        currentTheme.set(aTheme)


        context.setTheme(R.style.Theme_Vector_Light)

        
        mColorByAttr.clear()
    }

    
    fun setActivityTheme(activity: Activity, otherThemes: ActivityOtherThemes) {
        when (getApplicationTheme(activity)) {
            SYSTEM_THEME_VALUE -> if (isSystemDarkTheme(activity.resources)) activity.setTheme(otherThemes.dark)
            THEME_DARK_VALUE   -> activity.setTheme(otherThemes.dark)
            THEME_BLACK_VALUE  -> activity.setTheme(otherThemes.black)
        }

        mColorByAttr.clear()
    }

    
    @ColorInt
    fun getColor(c: Context, @AttrRes colorAttribute: Int): Int {
        return mColorByAttr.getOrPut(colorAttribute) {
            try {
                val color = TypedValue()
                c.theme.resolveAttribute(colorAttribute, color, true)
                color.data
            } catch (e: Exception) {
                Timber.e(e, "Unable to get color")
                ContextCompat.getColor(c, android.R.color.holo_red_dark)
            }
        }
    }

    fun getAttribute(c: Context, @AttrRes attribute: Int): TypedValue? {
        try {
            val typedValue = TypedValue()
            c.theme.resolveAttribute(attribute, typedValue, true)
            return typedValue
        } catch (e: Exception) {
            Timber.e(e, "Unable to get color")
        }
        return null
    }

    
    fun tintDrawable(context: Context, drawable: Drawable, @AttrRes attribute: Int): Drawable {
        return tintDrawableWithColor(drawable, getColor(context, attribute))
    }

    
    fun tintDrawableWithColor(drawable: Drawable, @ColorInt color: Int): Drawable {
        val tinted = DrawableCompat.wrap(drawable)
        drawable.mutate()
        DrawableCompat.setTint(tinted, color)
        return tinted
    }
}
