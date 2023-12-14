

package im.vector.app.features.crypto.recover



sealed class BootstrapStep {
    
    object CheckingMigration : BootstrapStep()

    
    data class FirstForm(val keyBackUpExist: Boolean, val reset: Boolean = false) : BootstrapStep()

    object SetupPassphrase : BootstrapStep()
    object ConfirmPassphrase : BootstrapStep()

    data class AccountReAuth(val failure: String? = null) : BootstrapStep()

    abstract class GetBackupSecretForMigration : BootstrapStep()
    data class GetBackupSecretPassForMigration(val useKey: Boolean) : GetBackupSecretForMigration()
    object GetBackupSecretKeyForMigration : GetBackupSecretForMigration()

    object Initializing : BootstrapStep()
    data class SaveRecoveryKey(val isSaved: Boolean) : BootstrapStep()
    object DoneSuccess : BootstrapStep()
}

fun BootstrapStep.GetBackupSecretForMigration.useKey(): Boolean {
    return when (this) {
        is BootstrapStep.GetBackupSecretPassForMigration -> useKey
        else                                             -> true
    }
}
