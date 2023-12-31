
package im.vector.app.features.settings.troubleshoot

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.session.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.pushrules.RuleIds
import org.matrix.android.sdk.api.pushrules.RuleKind
import javax.inject.Inject


class TestAccountSettings @Inject constructor(private val stringProvider: StringProvider,
                                              private val activeSessionHolder: ActiveSessionHolder) :
        TroubleshootTest(R.string.settings_troubleshoot_test_account_settings_title) {

    override fun perform(activityResultLauncher: ActivityResultLauncher<Intent>) {
        val session = activeSessionHolder.getSafeActiveSession() ?: return
        val defaultRule = session.getPushRules().getAllRules()
                .find { it.ruleId == RuleIds.RULE_ID_DISABLE_ALL }

        if (defaultRule != null) {
            if (!defaultRule.enabled) {
                description = stringProvider.getString(R.string.settings_troubleshoot_test_account_settings_success)
                quickFix = null
                status = TestStatus.SUCCESS
            } else {
                description = stringProvider.getString(R.string.settings_troubleshoot_test_account_settings_failed)
                quickFix = object : TroubleshootQuickFix(R.string.settings_troubleshoot_test_account_settings_quickfix) {
                    override fun doFix() {
                        if (manager?.diagStatus == TestStatus.RUNNING) return 

                        session.coroutineScope.launch {
                            tryOrNull {
                                session.updatePushRuleEnableStatus(RuleKind.OVERRIDE, defaultRule, !defaultRule.enabled)
                            }
                            withContext(Dispatchers.Main) {
                                manager?.retry(activityResultLauncher)
                            }
                        }
                    }
                }
                status = TestStatus.FAILED
            }
        } else {
            
            status = TestStatus.FAILED
        }
    }
}
