

package im.vector.app.features.home.room.threads

import android.app.Activity
import android.text.Spanned
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.settings.VectorPreferences
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import javax.inject.Inject


class ThreadsManager @Inject constructor(
        private val vectorPreferences: VectorPreferences,
        private val lightweightSettingsStorage: LightweightSettingsStorage,
        private val stringProvider: StringProvider
) {

    
    fun enableThreadsAndRestart(activity: Activity) {
        vectorPreferences.setThreadMessagesEnabled()
        lightweightSettingsStorage.setThreadMessagesEnabled(vectorPreferences.areThreadMessagesEnabled())
        MainActivity.restartApp(activity, MainActivityArgs(clearCache = true))
    }

    
    private fun generateLearnMoreHtmlString(@StringRes messageId: Int): Spanned {
        val learnMore = stringProvider.getString(R.string.action_learn_more)
        val learnMoreUrl = stringProvider.getString(R.string.threads_learn_more_url)
        val href = "<a href='$learnMoreUrl'>$learnMore</a>.<br><br>"
        val message = stringProvider.getString(messageId, href)
        return HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getBetaEnableThreadsMessage(): Spanned =
            generateLearnMoreHtmlString(R.string.threads_beta_enable_notice_message)

    fun getLabsEnableThreadsMessage(): Spanned =
            generateLearnMoreHtmlString(R.string.threads_labs_enable_notice_message)
}
