
package im.vector.app.gplay.features.settings.troubleshoot

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.settings.troubleshoot.TroubleshootTest
import timber.log.Timber
import javax.inject.Inject


class TestPlayServices @Inject constructor(private val context: FragmentActivity,
                                           private val stringProvider: StringProvider) :
        TroubleshootTest(R.string.settings_troubleshoot_test_play_services_title) {

    override fun perform(activityResultLauncher: ActivityResultLauncher<Intent>) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode == ConnectionResult.SUCCESS) {
            quickFix = null
            description = stringProvider.getString(R.string.settings_troubleshoot_test_play_services_success)
            status = TestStatus.SUCCESS
        } else {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                quickFix = object : TroubleshootQuickFix(R.string.settings_troubleshoot_test_play_services_quickfix) {
                    override fun doFix() {
                        apiAvailability.getErrorDialog(context, resultCode, 9000 ).show()
                    }
                }
                Timber.e("Play Services apk error $resultCode -> ${apiAvailability.getErrorString(resultCode)}.")
            }

            description = stringProvider.getString(R.string.settings_troubleshoot_test_play_services_failed, apiAvailability.getErrorString(resultCode))
            status = TestStatus.FAILED
        }
    }
}
