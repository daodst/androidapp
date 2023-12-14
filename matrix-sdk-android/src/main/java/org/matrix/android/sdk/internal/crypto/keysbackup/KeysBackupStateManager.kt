

package org.matrix.android.sdk.internal.crypto.keysbackup

import android.os.Handler
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupStateListener
import timber.log.Timber

internal class KeysBackupStateManager(private val uiHandler: Handler) {

    private val listeners = ArrayList<KeysBackupStateListener>()

    
    var state = KeysBackupState.Unknown
        set(newState) {
            Timber.v("KeysBackup: setState: $field -> $newState")

            field = newState

            
            uiHandler.post {
                synchronized(listeners) {
                    listeners.forEach {
                        
                        it.onStateChange(newState)
                    }
                }
            }
        }

    val isEnabled: Boolean
        get() = state == KeysBackupState.ReadyToBackUp ||
                state == KeysBackupState.WillBackUp ||
                state == KeysBackupState.BackingUp

    
    val isStucked: Boolean
        get() = state == KeysBackupState.Unknown ||
                state == KeysBackupState.Disabled ||
                state == KeysBackupState.WrongBackUpVersion ||
                state == KeysBackupState.NotTrusted

    fun addListener(listener: KeysBackupStateListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: KeysBackupStateListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }
}
