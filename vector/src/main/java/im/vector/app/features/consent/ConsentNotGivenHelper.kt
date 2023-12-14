

package im.vector.app.features.consent

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.dialogs.DialogLocker
import im.vector.app.core.platform.Restorable
import im.vector.app.features.webview.VectorWebViewActivity
import im.vector.app.features.webview.WebViewMode

class ConsentNotGivenHelper(private val activity: Activity,
                            private val dialogLocker: DialogLocker) :
        Restorable by dialogLocker {

    

    
    fun displayDialog(consentUri: String, homeServerHost: String) {
        dialogLocker.displayDialog {
            MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.settings_app_term_conditions)
                    .setMessage(activity.getString(R.string.dialog_user_consent_content, homeServerHost))
                    .setPositiveButton(R.string.dialog_user_consent_submit) { _, _ ->
                        openWebViewActivity(consentUri)
                    }
        }
    }

    

    private fun openWebViewActivity(consentUri: String) {
        val intent = VectorWebViewActivity.getIntent(activity, consentUri, activity.getString(R.string.settings_app_term_conditions), WebViewMode.CONSENT)
        activity.startActivity(intent)
    }
}
