

package im.vector.app.features.settings

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.di.DefaultSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.IllformedLocaleException
import java.util.Locale


object VectorLocale {
    private const val APPLICATION_LOCALE_COUNTRY_KEY = "APPLICATION_LOCALE_COUNTRY_KEY"
    private const val APPLICATION_LOCALE_VARIANT_KEY = "APPLICATION_LOCALE_VARIANT_KEY"
    private const val APPLICATION_LOCALE_LANGUAGE_KEY = "APPLICATION_LOCALE_LANGUAGE_KEY"
    private const val APPLICATION_LOCALE_SCRIPT_KEY = "APPLICATION_LOCALE_SCRIPT_KEY"

    private val defaultLocale = Locale("en", "US")

    private const val ISO_15924_LATN = "Latn"

    
    private val supportedLocales = mutableListOf<Locale>()

    
    var applicationLocale = defaultLocale
        private set

    private lateinit var context: Context

    
    fun init(context: Context) {
        this.context = context
        val preferences = DefaultSharedPreferences.getInstance(context)

        if (preferences.contains(APPLICATION_LOCALE_LANGUAGE_KEY)) {
            applicationLocale = Locale(preferences.getString(APPLICATION_LOCALE_LANGUAGE_KEY, "")!!,
                    preferences.getString(APPLICATION_LOCALE_COUNTRY_KEY, "")!!,
                    preferences.getString(APPLICATION_LOCALE_VARIANT_KEY, "")!!
            )
        } else {
            applicationLocale = Locale.getDefault()

            
            val defaultStringValue = getString(context, defaultLocale, R.string.resources_country_code)
            if (defaultStringValue == getString(context, applicationLocale, R.string.resources_country_code)) {
                applicationLocale = defaultLocale
            }

            saveApplicationLocale(applicationLocale)
        }
    }

    
    fun saveApplicationLocale(locale: Locale) {
        applicationLocale = locale

        DefaultSharedPreferences.getInstance(context).edit {
            val language = locale.language
            if (language.isEmpty()) {
                remove(APPLICATION_LOCALE_LANGUAGE_KEY)
            } else {
                putString(APPLICATION_LOCALE_LANGUAGE_KEY, language)
            }

            val country = locale.country
            if (country.isEmpty()) {
                remove(APPLICATION_LOCALE_COUNTRY_KEY)
            } else {
                putString(APPLICATION_LOCALE_COUNTRY_KEY, country)
            }

            val variant = locale.variant
            if (variant.isEmpty()) {
                remove(APPLICATION_LOCALE_VARIANT_KEY)
            } else {
                putString(APPLICATION_LOCALE_VARIANT_KEY, variant)
            }

            val script = locale.script
            if (script.isEmpty()) {
                remove(APPLICATION_LOCALE_SCRIPT_KEY)
            } else {
                putString(APPLICATION_LOCALE_SCRIPT_KEY, script)
            }
        }
    }

    
    private fun getString(context: Context, locale: Locale, resourceId: Int): String {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return try {
            context.createConfigurationContext(config).getText(resourceId).toString()
        } catch (e: Exception) {
            Timber.e(e, "## getString() failed")
            
            context.getString(resourceId)
        }
    }

    
    private fun initApplicationLocales() {
        val knownLocalesSet = HashSet<Triple<String, String, String>>()

        try {
            val availableLocales = Locale.getAvailableLocales()

            for (locale in availableLocales) {
                knownLocalesSet.add(
                        Triple(
                                getString(context, locale, R.string.resources_language),
                                getString(context, locale, R.string.resources_country_code),
                                getString(context, locale, R.string.resources_script)
                        )
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "## getApplicationLocales() : failed")
            knownLocalesSet.add(
                    Triple(
                            context.getString(R.string.resources_language),
                            context.getString(R.string.resources_country_code),
                            context.getString(R.string.resources_script)
                    )
            )
        }

        val list = knownLocalesSet.mapNotNull { (language, country, script) ->
            try {
                Locale.Builder()
                        .setLanguage(language)
                        .setRegion(country)
                        .setScript(script)
                        .build()
            } catch (exception: IllformedLocaleException) {
                if (BuildConfig.DEBUG) {
                    throw exception
                }
                
                null
            }
        }
                
                .sortedBy { localeToLocalisedString(it).lowercase(it) }

        supportedLocales.clear()
        supportedLocales.addAll(list)
    }

    
    fun localeToLocalisedString(locale: Locale): String {
        return buildString {
            append(locale.getDisplayLanguage(locale))

            if (locale.script != ISO_15924_LATN && locale.getDisplayScript(locale).isNotEmpty()) {
                append(" - ")
                append(locale.getDisplayScript(locale))
            }

            if (locale.getDisplayCountry(locale).isNotEmpty()) {
                append(" (")
                append(locale.getDisplayCountry(locale))
                append(")")
            }
        }
    }

    
    fun localeToLocalisedStringInfo(locale: Locale): String {
        return buildString {
            append("[")
            append(locale.displayLanguage)
            if (locale.script != ISO_15924_LATN) {
                append(" - ")
                append(locale.displayScript)
            }
            if (locale.displayCountry.isNotEmpty()) {
                append(" (")
                append(locale.displayCountry)
                append(")")
            }
            append("]")
        }
    }

    suspend fun getSupportedLocales(): List<Locale> {
        if (supportedLocales.isEmpty()) {
            
            withContext(Dispatchers.IO) {
                initApplicationLocales()
            }
        }
        return supportedLocales
    }
}
