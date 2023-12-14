
package im.vector.app.features.settings.notifications

import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import im.vector.app.R
import im.vector.app.core.preference.PushRulePreference
import im.vector.app.core.preference.VectorPreference
import im.vector.app.core.utils.toast
import im.vector.app.features.settings.VectorSettingsBaseFragment
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.pushrules.RuleIds
import org.matrix.android.sdk.api.pushrules.rest.PushRuleAndKind
import javax.inject.Inject

class VectorSettingsAdvancedNotificationPreferenceFragment @Inject constructor() :
        VectorSettingsBaseFragment() {

    override var titleRes: Int = R.string.settings_notification_advanced

    override val preferenceXmlRes = R.xml.vector_settings_notification_advanced_preferences

    override fun bindPref() {
        for (preferenceKey in prefKeyToPushRuleId.keys) {
            val preference = findPreference<VectorPreference>(preferenceKey)
            if (preference is PushRulePreference) {
                val ruleAndKind: PushRuleAndKind? = session.getPushRules().findDefaultRule(prefKeyToPushRuleId[preferenceKey])

                if (ruleAndKind == null) {
                    
                    preference.isVisible = false
                } else {
                    preference.isVisible = true
                    val initialIndex = ruleAndKind.pushRule.notificationIndex
                    preference.setIndex(initialIndex)
                    preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                        val newIndex = newValue as NotificationIndex
                        val standardAction = getStandardAction(ruleAndKind.pushRule.ruleId, newIndex)
                        if (standardAction != null) {
                            val enabled = standardAction != StandardActions.Disabled
                            val newActions = standardAction.actions
                            displayLoadingView()

                            lifecycleScope.launch {
                                val result = runCatching {
                                    session.updatePushRuleActions(ruleAndKind.kind,
                                            ruleAndKind.pushRule.ruleId,
                                            enabled,
                                            newActions)
                                }
                                if (!isAdded) {
                                    return@launch
                                }
                                hideLoadingView()
                                result.onSuccess {
                                    preference.setIndex(newIndex)
                                }
                                result.onFailure { failure ->
                                    
                                    refreshDisplay()
                                    activity?.toast(errorFormatter.toHumanReadable(failure))
                                }
                            }
                        }
                        false
                    }
                }
            }
        }
    }

    private fun refreshDisplay() {
        listView?.adapter?.notifyDataSetChanged()
    }

    

    companion object {
        
        private val prefKeyToPushRuleId = mapOf(
                "SETTINGS_PUSH_RULE_CONTAINING_MY_DISPLAY_NAME_PREFERENCE_KEY" to RuleIds.RULE_ID_CONTAIN_DISPLAY_NAME,
                "SETTINGS_PUSH_RULE_CONTAINING_MY_USER_NAME_PREFERENCE_KEY" to RuleIds.RULE_ID_CONTAIN_USER_NAME,
                "SETTINGS_PUSH_RULE_MESSAGES_IN_ONE_TO_ONE_PREFERENCE_KEY" to RuleIds.RULE_ID_ONE_TO_ONE_ROOM,
                "SETTINGS_PUSH_RULE_MESSAGES_IN_GROUP_CHAT_PREFERENCE_KEY" to RuleIds.RULE_ID_ALL_OTHER_MESSAGES_ROOMS,
                "SETTINGS_PUSH_RULE_INVITED_TO_ROOM_PREFERENCE_KEY" to RuleIds.RULE_ID_INVITE_ME,
                "SETTINGS_PUSH_RULE_CALL_INVITATIONS_PREFERENCE_KEY" to RuleIds.RULE_ID_CALL,
                "SETTINGS_PUSH_RULE_MESSAGES_SENT_BY_BOT_PREFERENCE_KEY" to RuleIds.RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS,
                "SETTINGS_PUSH_RULE_MESSAGES_CONTAINING_AT_ROOM_PREFERENCE_KEY" to RuleIds.RULE_ID_ROOM_NOTIF,
                "SETTINGS_PUSH_RULE_MESSAGES_IN_E2E_ONE_ONE_CHAT_PREFERENCE_KEY" to RuleIds.RULE_ID_ONE_TO_ONE_ENCRYPTED_ROOM,
                "SETTINGS_PUSH_RULE_MESSAGES_IN_E2E_GROUP_CHAT_PREFERENCE_KEY" to RuleIds.RULE_ID_ENCRYPTED,
                "SETTINGS_PUSH_RULE_ROOMS_UPGRADED_KEY" to RuleIds.RULE_ID_TOMBSTONE
        )
    }
}
