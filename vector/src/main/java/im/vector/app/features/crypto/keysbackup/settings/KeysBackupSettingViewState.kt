

package im.vector.app.features.crypto.keysbackup.settings

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupVersionTrust
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersionResult

data class KeysBackupSettingViewState(val keysBackupVersionTrust: Async<KeysBackupVersionTrust> = Uninitialized,
                                      val keysBackupState: KeysBackupState? = null,
                                      val keysBackupVersion: KeysVersionResult? = null,
                                      val deleteBackupRequest: Async<Unit> = Uninitialized) :
        MavericksState
