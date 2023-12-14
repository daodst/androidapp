
package im.vector.app.gplay.features.settings.troubleshoot

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.google.firebase.messaging.FirebaseMessaging
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.startAddGoogleAccountIntent
import im.vector.app.features.settings.troubleshoot.TroubleshootTest
import im.vector.app.push.fcm.FcmHelper
import timber.log.Timber
import javax.inject.Inject


class TestFirebaseToken @Inject constructor(private val context: FragmentActivity,
                                            private val stringProvider: StringProvider) : TroubleshootTest(R.string.settings_troubleshoot_test_fcm_title) {

    override fun perform(activityResultLauncher: ActivityResultLauncher<Intent>) {
        status = TestStatus.RUNNING
        try {
            FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(context) { task ->
                        if (!task.isSuccessful) {
                            
                            description = when (val errorMsg = task.exception?.localizedMessage ?: "Unknown") {
                                "SERVICE_NOT_AVAILABLE"  -> {
                                    stringProvider.getString(R.string.settings_troubleshoot_test_fcm_failed_service_not_available, errorMsg)
                                }
                                "TOO_MANY_REGISTRATIONS" -> {
                                    stringProvider.getString(R.string.settings_troubleshoot_test_fcm_failed_too_many_registration, errorMsg)
                                }
                                "ACCOUNT_MISSING"        -> {
                                    quickFix = object : TroubleshootQuickFix(R.string.settings_troubleshoot_test_fcm_failed_account_missing_quick_fix) {
                                        override fun doFix() {
                                            startAddGoogleAccountIntent(context, activityResultLauncher)
                                        }
                                    }
                                    stringProvider.getString(R.string.settings_troubleshoot_test_fcm_failed_account_missing, errorMsg)
                                }
                                else                     -> {
                                    stringProvider.getString(R.string.settings_troubleshoot_test_fcm_failed, errorMsg)
                                }
                            }
                            status = TestStatus.FAILED
                        } else {
                            task.result?.let { token ->
                                val tok = token.take(8) + "********************"
                                description = stringProvider.getString(R.string.settings_troubleshoot_test_fcm_success, tok)
                                Timber.e("Retrieved FCM token success [$tok].")
                                
                                FcmHelper.storeFcmToken(context, token)
                            }
                            status = TestStatus.SUCCESS
                        }
                    }
        } catch (e: Throwable) {
            description = stringProvider.getString(R.string.settings_troubleshoot_test_fcm_failed, e.localizedMessage)
            status = TestStatus.FAILED
        }
    }
}
