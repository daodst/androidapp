

package im.vector.app.features.crypto.keysbackup.settings

import im.vector.app.core.platform.VectorViewModelAction

sealed class KeyBackupSettingsAction : VectorViewModelAction {
    object Init : KeyBackupSettingsAction()
    object GetKeyBackupTrust : KeyBackupSettingsAction()
    object DeleteKeyBackup : KeyBackupSettingsAction()
}
