

package im.vector.app.features.settings

import android.os.Bundle
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.preference.VectorPreference
import im.vector.app.features.analytics.plan.MobileScreen
import javax.inject.Inject

class VectorSettingsRootFragment @Inject constructor() : VectorSettingsBaseFragment() {

    override var titleRes: Int = R.string.title_activity_settings
    override val preferenceXmlRes = R.xml.vector_settings_root

    
    private val securityPrivacy by lazy {
        findPreference<VectorPreference>("IC_SETTINGS_ROOT_SECURITY_PRIVACY")!!
    }

    private val settingsRootLabs by lazy {
        findPreference<VectorPreference>("SETTINGS_ROOT_LABS")!!
    }

    private val settingsRootAdvanced by lazy {
        findPreference<VectorPreference>("SETTINGS_ROOT_ADVANCED")!!
    }

    private val settingsRootNotification by lazy {
        findPreference<VectorPreference>("SETTINGS_ROOT_NOTIFICATION")!!
    }
    private val settingsRootPreferences by lazy {
        findPreference<VectorPreference>("SETTINGS_ROOT_PREFERENCES")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.Settings
        securityPrivacy.isVisible =BuildConfig.DEBUG
        settingsRootLabs.isVisible =BuildConfig.DEBUG
        settingsRootAdvanced.isVisible =BuildConfig.DEBUG
        settingsRootNotification.isVisible =BuildConfig.DEBUG
        settingsRootPreferences.isVisible =BuildConfig.DEBUG
    }

    override fun bindPref() {
        tintIcons()
    }

    private fun tintIcons() {
        for (i in 0 until preferenceScreen.preferenceCount) {
            (preferenceScreen.getPreference(i) as? VectorPreference)?.let { it.tintIcon = true }
        }
    }
}
