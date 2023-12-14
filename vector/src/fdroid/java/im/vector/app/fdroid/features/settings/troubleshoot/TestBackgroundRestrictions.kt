
package im.vector.app.fdroid.features.settings.troubleshoot

import android.content.Intent
import android.net.ConnectivityManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.getSystemService
import androidx.core.net.ConnectivityManagerCompat
import androidx.fragment.app.FragmentActivity
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.settings.troubleshoot.TroubleshootTest
import javax.inject.Inject

class TestBackgroundRestrictions @Inject constructor(private val context: FragmentActivity,
                                                     private val stringProvider: StringProvider) :
        TroubleshootTest(R.string.settings_troubleshoot_test_bg_restricted_title) {

    override fun perform(activityResultLauncher: ActivityResultLauncher<Intent>) {
        context.getSystemService<ConnectivityManager>()!!.apply {
            
            if (isActiveNetworkMetered) {
                
                when (ConnectivityManagerCompat.getRestrictBackgroundStatus(this)) {
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED     -> {
                        
                        
                        description = stringProvider.getString(R.string.settings_troubleshoot_test_bg_restricted_failed,
                                "RESTRICT_BACKGROUND_STATUS_ENABLED")
                        status = TestStatus.FAILED
                        quickFix = null
                    }
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED -> {
                        
                        
                        description = stringProvider.getString(R.string.settings_troubleshoot_test_bg_restricted_success,
                                "RESTRICT_BACKGROUND_STATUS_WHITELISTED")
                        status = TestStatus.SUCCESS
                        quickFix = null
                    }
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED    -> {
                        
                        
                        description = stringProvider.getString(R.string.settings_troubleshoot_test_bg_restricted_success,
                                "RESTRICT_BACKGROUND_STATUS_DISABLED")
                        status = TestStatus.SUCCESS
                        quickFix = null
                    }
                }
            } else {
                
                
                description = stringProvider.getString(R.string.settings_troubleshoot_test_bg_restricted_success, "")
                status = TestStatus.SUCCESS
                quickFix = null
            }
        }
    }
}
