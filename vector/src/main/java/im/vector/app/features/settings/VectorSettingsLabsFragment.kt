

package im.vector.app.features.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.preference.VectorSwitchPreference
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.home.room.threads.ThreadsManager
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import javax.inject.Inject

class VectorSettingsLabsFragment @Inject constructor(
        private val vectorPreferences: VectorPreferences,
        private val lightweightSettingsStorage: LightweightSettingsStorage,
        private val threadsManager: ThreadsManager
) : VectorSettingsBaseFragment() {

    override var titleRes = R.string.room_settings_labs_pref_title
    override val preferenceXmlRes = R.xml.vector_settings_labs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.SettingsLabs
    }

    override fun bindPref() {
        findPreference<VectorSwitchPreference>(VectorPreferences.SETTINGS_LABS_AUTO_REPORT_UISI)?.let { pref ->
            
            pref.isChecked = vectorPreferences.labsAutoReportUISI()
        }

        
        findPreference<VectorSwitchPreference>(VectorPreferences.SETTINGS_LABS_ENABLE_THREAD_MESSAGES)?.let { vectorPref ->
            vectorPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                onThreadsPreferenceClickedInterceptor(vectorPref)
                false
            }
        }
    }

    
    private fun onThreadsPreferenceClickedInterceptor(vectorSwitchPreference: VectorSwitchPreference) {
        val userEnabledThreads = vectorPreferences.areThreadMessagesEnabled()
        if (!session.getHomeServerCapabilities().canUseThreading && userEnabledThreads) {
            activity?.let {
                MaterialAlertDialogBuilder(it)
                        .setTitle(R.string.threads_labs_enable_notice_title)
                        .setMessage(threadsManager.getLabsEnableThreadsMessage())
                        .setCancelable(true)
                        .setNegativeButton(R.string.action_not_now) { _, _ ->
                            vectorSwitchPreference.isChecked = false
                        }
                        .setPositiveButton(R.string.action_try_it_out) { _, _ ->
                            onThreadsPreferenceClicked()
                        }
                        .show()
                        ?.findViewById<TextView>(android.R.id.message)
                        ?.apply {
                            linksClickable = true
                            movementMethod = LinkMovementMethod.getInstance()
                        }
            }
        } else {
            onThreadsPreferenceClicked()
        }
    }

    
    private fun onThreadsPreferenceClicked() {
        
        vectorPreferences.setShouldMigrateThreads(!vectorPreferences.areThreadMessagesEnabled())
        lightweightSettingsStorage.setThreadMessagesEnabled(vectorPreferences.areThreadMessagesEnabled())
        displayLoadingView()
        MainActivity.restartApp(requireActivity(), MainActivityArgs(clearCache = true))
    }
}
