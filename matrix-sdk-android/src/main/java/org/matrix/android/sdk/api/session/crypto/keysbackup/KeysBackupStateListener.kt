

package org.matrix.android.sdk.api.session.crypto.keysbackup

interface KeysBackupStateListener {

    
    fun onStateChange(newState: KeysBackupState)
}
