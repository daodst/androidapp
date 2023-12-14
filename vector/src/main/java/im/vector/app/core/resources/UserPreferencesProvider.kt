

package im.vector.app.core.resources

import im.vector.app.features.settings.VectorPreferences
import javax.inject.Inject

class UserPreferencesProvider @Inject constructor(private val vectorPreferences: VectorPreferences) {

    fun shouldShowHiddenEvents(): Boolean {
        return vectorPreferences.shouldShowHiddenEvents()
    }

    fun shouldShowReadReceipts(): Boolean {
        return vectorPreferences.showReadReceipts()
    }

    fun shouldShowRedactedMessages(): Boolean {
        return vectorPreferences.showRedactedMessages()
    }

    fun shouldShowLongClickOnRoomHelp(): Boolean {
        return vectorPreferences.shouldShowLongClickOnRoomHelp()
    }

    fun neverShowLongClickOnRoomHelpAgain() {
        vectorPreferences.neverShowLongClickOnRoomHelpAgain()
    }

    fun shouldShowJoinLeaves(): Boolean {
        return vectorPreferences.showJoinLeaveMessages()
    }

    fun shouldShowAvatarDisplayNameChanges(): Boolean {
        return vectorPreferences.showAvatarDisplayNameChangeMessages()
    }

    fun areThreadMessagesEnabled(): Boolean {
        return vectorPreferences.areThreadMessagesEnabled()
    }
}
