

package im.vector.app.features.rageshake

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.seismic.ShakeDetector
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.hardware.vibrate
import im.vector.app.features.navigation.Navigator
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.settings.VectorSettingsActivity
import javax.inject.Inject

class RageShake @Inject constructor(private val activity: FragmentActivity,
                                    private val bugReporter: BugReporter,
                                    private val navigator: Navigator,
                                    private val sessionHolder: ActiveSessionHolder,
                                    private val vectorPreferences: VectorPreferences) : ShakeDetector.Listener {

    private var shakeDetector: ShakeDetector? = null

    private var dialogDisplayed = false

    var interceptor: (() -> Unit)? = null

    fun start() {
        val sensorManager = activity.getSystemService<SensorManager>() ?: return

        shakeDetector = ShakeDetector(this).apply {
            setSensitivity(vectorPreferences.getRageshakeSensitivity())
            start(sensorManager, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        shakeDetector?.stop()
    }

    fun setSensitivity(sensitivity: Int) {
        shakeDetector?.setSensitivity(sensitivity)
    }

    override fun hearShake() {
        val i = interceptor
        if (i != null) {
            vibrate(activity)
            i.invoke()
        } else {
            if (dialogDisplayed) {
                
                return
            }

            vibrate(activity)
            dialogDisplayed = true

            MaterialAlertDialogBuilder(activity)
                    .setMessage(R.string.send_bug_report_alert_message)
                    .setPositiveButton(R.string.yes) { _, _ -> openBugReportScreen() }
                    .also {
                        if (sessionHolder.hasActiveSession()) {
                            it.setNeutralButton(R.string.settings) { _, _ -> openSettings() }
                        } else {
                            it.setNeutralButton(R.string.action_disable) { _, _ -> disableRageShake() }
                        }
                    }
                    .setOnDismissListener { dialogDisplayed = false }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
    }

    private fun openBugReportScreen() {
        bugReporter.openBugReportScreen(activity)
    }

    private fun openSettings() {
        navigator.openSettings(activity, VectorSettingsActivity.EXTRA_DIRECT_ACCESS_ADVANCED_SETTINGS)
    }

    private fun disableRageShake() {
        vectorPreferences.setRageshakeEnabled(false)
        stop()
    }

    companion object {
        
        fun isAvailable(context: Context): Boolean {
            return context.getSystemService<SensorManager>()?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        }
    }
}
