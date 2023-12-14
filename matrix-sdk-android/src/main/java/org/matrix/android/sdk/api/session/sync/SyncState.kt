

package org.matrix.android.sdk.api.session.sync

sealed class SyncState {
    object Idle : SyncState()
    data class Running(val afterPause: Boolean) : SyncState()
    object Paused : SyncState()
    object Killing : SyncState()
    object Killed : SyncState()
    object NoNetwork : SyncState()
    object InvalidToken : SyncState()
}
