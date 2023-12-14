

package org.matrix.android.sdk.internal.crypto.keysbackup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupStateListener
import java.util.concurrent.CountDownLatch


internal class StateObserver(private val keysBackup: KeysBackupService,
                             private val latch: CountDownLatch? = null,
                             private val expectedStateChange: Int = -1) : KeysBackupStateListener {

    private val allowedStateTransitions = listOf(
            KeysBackupState.BackingUp to KeysBackupState.ReadyToBackUp,
            KeysBackupState.BackingUp to KeysBackupState.WrongBackUpVersion,

            KeysBackupState.CheckingBackUpOnHomeserver to KeysBackupState.Disabled,
            KeysBackupState.CheckingBackUpOnHomeserver to KeysBackupState.NotTrusted,
            KeysBackupState.CheckingBackUpOnHomeserver to KeysBackupState.ReadyToBackUp,
            KeysBackupState.CheckingBackUpOnHomeserver to KeysBackupState.Unknown,
            KeysBackupState.CheckingBackUpOnHomeserver to KeysBackupState.WrongBackUpVersion,

            KeysBackupState.Disabled to KeysBackupState.Enabling,

            KeysBackupState.Enabling to KeysBackupState.Disabled,
            KeysBackupState.Enabling to KeysBackupState.ReadyToBackUp,

            KeysBackupState.NotTrusted to KeysBackupState.CheckingBackUpOnHomeserver,
            
            KeysBackupState.NotTrusted to KeysBackupState.ReadyToBackUp,

            KeysBackupState.ReadyToBackUp to KeysBackupState.WillBackUp,

            KeysBackupState.Unknown to KeysBackupState.CheckingBackUpOnHomeserver,

            KeysBackupState.WillBackUp to KeysBackupState.BackingUp,

            KeysBackupState.WrongBackUpVersion to KeysBackupState.CheckingBackUpOnHomeserver,

            
            KeysBackupState.ReadyToBackUp to KeysBackupState.BackingUp,
            KeysBackupState.ReadyToBackUp to KeysBackupState.ReadyToBackUp,
            KeysBackupState.WillBackUp to KeysBackupState.ReadyToBackUp,
            KeysBackupState.WillBackUp to KeysBackupState.Unknown
    )

    private val stateList = ArrayList<KeysBackupState>()
    private var lastTransitionError: String? = null

    init {
        keysBackup.addListener(this)
    }

    
    fun stopAndCheckStates(expectedStates: List<KeysBackupState>?) {
        keysBackup.removeListener(this)

        expectedStates?.let {
            assertEquals(it.size, stateList.size)

            for (i in it.indices) {
                assertEquals("The state $i is not correct. states: " + stateList.joinToString(separator = " "), it[i], stateList[i])
            }
        }

        assertNull("states: " + stateList.joinToString(separator = " "), lastTransitionError)
    }

    override fun onStateChange(newState: KeysBackupState) {
        stateList.add(newState)

        
        if (stateList.size >= 2 &&
                !allowedStateTransitions.contains(stateList[stateList.size - 2] to newState)) {
            
            lastTransitionError = "Forbidden transition detected from " + stateList[stateList.size - 2] + " to " + newState
        }

        if (expectedStateChange == stateList.size) {
            latch?.countDown()
        }
    }
}
